package me.vrekt.arc.check.moving;

import cn.nukkit.Player;
import cn.nukkit.level.Location;
import me.vrekt.arc.Arc;
import me.vrekt.arc.check.Check;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.check.moving.configuration.MovingFlightConfig;
import me.vrekt.arc.check.result.CheckResult;
import me.vrekt.arc.compatibility.NukkitAccess;
import me.vrekt.arc.data.moving.MovingData;
import me.vrekt.arc.exemption.type.ExemptionType;
import me.vrekt.arc.timings.CheckTimings;
import me.vrekt.arc.utility.MovingAccess;
import me.vrekt.arc.utility.block.BlockAccess;
import me.vrekt.arc.utility.math.MathUtil;

/**
 * Checks if the player is flying.
 * <p>
 * -> Player is ascending too high
 * -> Player is ascending too long
 * -> Player is climbing a ladder too fast
 * -> Player is not falling fast enough (gliding)
 * -> Etc.
 */
public final class Flight extends Check {

    private final MovingFlightConfig cc;

    public Flight() {
        super(CheckType.FLIGHT);
        enabled(true)
                .cancel(true)
                .cancelLevel(0)
                .notify(true)
                .notifyEvery(1)
                .ban(false)
                .kick(false)
                .build();

        cc = new MovingFlightConfig();
        cc.write(configuration);

        if (enabled()) load();
    }

    /**
     * Check the player
     *
     * @param player the player
     * @param data   their data
     */
    public void check(Player player, MovingData data) {
        if (exempt(player) || exempt(player, ExemptionType.TELEPORT)) return;

        // TODO: Elytra
        if (player.isGliding()) return;

        final CheckResult result = new CheckResult();
        startTiming(player);

        // from, to, ground and safe location(s)
        final Location from = data.from();
        final Location to = data.to();
        final Location ground = data.ground();
        final double vertical = data.vertical();

        // initially, update any moving data we may need.
        updateMovingData(data);

        // check if the player is on skulls, slabs, stairs, fences, gates, beds, etc.
        final boolean hasVerticalModifier = BlockAccess.hasVerticalModifierAt(to, to.getLevel(), 0.3, -0.1, 0.3)
                || BlockAccess.hasVerticalModifierAt(to, to.getLevel(), 0.5, -1, 0.5);

        // check if its a valid vertical move.
        // TODO: We need a better way to determine
        // TODO: If the player walked up a slab.
        // TODO: Currently, this method allows the player
        // TODO: To fly higher than normal on ground (if they are on a slab)
        final boolean validVerticalMove = vertical > 0.0
                && player.getRiding() == null
                && !data.inLiquid()
                && !data.hasClimbable();

        // check the vertical move of this player.
        final double distanceToGround = MathUtil.vertical(ground, to);
        if (validVerticalMove) {
            // check vertical clip regardless of ascending or descending state.
            // return here since we don't want the rest of the check interfering with setback.
            //  final boolean failed = checkIfMovedThroughSolidBlock(player, result, safe, from, to, vertical);
            if (!hasVerticalModifier) {
                checkVerticalMove(player, data, ground, from, vertical, distanceToGround, data.ascendingTime(), result);
            }
        }

        // check gliding, descending movement.
        if (player.getRiding() == null
                && !data.inLiquid()
                && !data.hasClimbable()
                && !player.hasEffect(NukkitAccess.SLOW_FALLING_EFFECT)) {
            checkGlide(player, data, ground, from, to, vertical, distanceToGround, result);
        }

        if (data.hasClimbable()) {
            // the ascending cooldown, will need to be reversed if descending.
            final double cooldown = cc.climbingCooldown - (vertical * 2);
            if (data.climbTime() >= cooldown) checkClimbingMovement(player, data, from, vertical, cooldown, result);
        }

        // update safe location if not failed and on ground.
        if (!result.hasFailedBefore() && data.onGround()) data.setSafeLocation(to);

        stopTiming(player);
    }

    /**
     * Check the players vertical movement.
     *
     * @param player        the player
     * @param data          their data
     * @param ground        ground location
     * @param from          the from
     * @param vertical      vertical
     * @param distance      distance from ground
     * @param ascendingTime ascendingTime
     * @param result        result
     */
    private void checkVerticalMove(Player player, MovingData data, Location ground, Location from, double vertical, double distance, int ascendingTime, CheckResult result) {
        if (data.ascending()) {

            Arc.debug("v=" + vertical + ", dist=" + distance + ", time=" + ascendingTime);

            final boolean hasSlimeblock = data.hasSlimeblock();
            if (hasSlimeblock && vertical > 0.42 && distance > 1f) {
                data.setHasSlimeBlockLaunch(true);
            }

            // tighter distance check, more moves allowed though, don't use ascend that resets.
            if (distance >= cc.noResetAscendGroundDistanceThreshold) {
                // check ascending moves, no reset.
                final int moves = data.getNoResetAscendTime();

                if (moves >= cc.maxNoResetAscendMoves
                        && !data.hasSlimeBlockLaunch()) {
                    // we have a few moves here to work with.
                    result.setFailed("Ascending too long")
                            .withParameter("distance", distance)
                            .withParameter("moves", moves)
                            .withParameter("max", cc.maxNoResetAscendMoves);
                    handleCheckViolationAndReset(player, result, ground);
                }
            }

            if (distance >= cc.groundDistanceThreshold) {
                // high off ground (hopefully) check.
                // make sure we are within the limits of the ground.
                // we don't want a flag when the player is wildly jumping around.
                final double hDist = data.getGroundHorizontalDistance();
                if (ascendingTime >= 5 && hDist < cc.groundDistanceHorizontalCap && !data.hasSlimeBlockLaunch()) {
                    result.setFailed("Vertical distance from ground greater than allowed within limits.")
                            .withParameter("distance", distance)
                            .withParameter("threshold", cc.groundDistanceThreshold)
                            .withParameter("hDist", hDist)
                            .withParameter("cap", cc.groundDistanceHorizontalCap);
                    handleCheckViolationAndReset(player, result, ground);
                }
            }

            // TODO: Maybe in the future, watch slime-block movement.
            // TODO: Redo all the below, since movement is fixed now.
            // TODO: Ignore that comment? i don't know what needs to be done looks fine

            // ensure we didn't walk up a block that modifies your vertical
            final double maxJumpHeight = getJumpHeight(player);

            if (vertical > maxJumpHeight) {
                result.setFailed("Vertical move greater than max jump height.")
                        .withParameter("vertical", vertical)
                        .withParameter("max", maxJumpHeight);
                handleCheckViolationAndReset(player, result, from);
            }

            // add to our modifier if we have a jump effect.
            // this will need to be amplified by the amplifier.
            final int modifier = player.hasEffect(NukkitAccess.JUMP_BOOST_EFFECT)
                    ? player.getEffect(NukkitAccess.JUMP_BOOST_EFFECT).getAmplifier() + cc.jumpBoostAscendAmplifier
                    : 0;

            if (ascendingTime > (cc.maxAscendTime + modifier) && !data.hadClimbable() && !data.hasSlimeBlockLaunch()) {
                result.setFailed("Ascending for too long")
                        .withParameter("vertical", vertical)
                        .withParameter("time", data.ascendingTime())
                        .withParameter("max", (cc.maxAscendTime + modifier));
                handleCheckViolationAndReset(player, result, ground);
            }
        }
    }

    /**
     * Generally check player gliding and flight movements.
     *
     * @param player   the player
     * @param data     their data
     * @param ground   ground location
     * @param from     the from
     * @param to       to
     * @param vertical vertical
     * @param distance the distance to ground
     * @param result   result
     */
    private void checkGlide(Player player, MovingData data, Location ground, Location from, Location to, double vertical, double distance, CheckResult result) {

        // check no ground stuff
        // TODO: Can be bypassed, needs to be improved, see comment below.
        if (!data.onGround() && (data.getNoResetDescendTime() >= 5 || data.getNoResetAscendTime() >= 5)) {
            final double horizontal = data.getGroundHorizontalDistance();
            if (horizontal > 3f && data.getInAirTime() >= 15) {
                // player is far from ground, so we should expect to be a certain distance by this point.
                // This can still be abused here, so in the future calculate what we should expect.
                // TODO: Bypassable by falling a greater amount over time.
                if (distance < 1f) {
                    result.setFailed("Not gliding over-time (experimental)")
                            .withParameter("horizontal", horizontal)
                            .withParameter("min", 3f)
                            .withParameter("air", data.getInAirTime())
                            .withParameter("min", 15)
                            .withParameter("distance", distance)
                            .withParameter("min", 1f);
                    handleCheckViolationAndReset(player, result, ground);
                }
            }
        }

        // first, basic glide check
        // ensure player is actually moving down when off the ground here.
        if (!data.onGround() && !data.ascending()) {
            // calculate how we moved since last time.
            final double delta = Math.abs(vertical - data.lastVertical());

            // player hasn't moved, increase 'time'
            if (vertical == 0.0 || delta == 0.0) {
                data.setNoGlideTime(data.getNoGlideTime() + 1);
            } else {
                // decrease / reward
                data.setNoGlideTime(data.getNoGlideTime() - 1);
            }

            if (data.getNoGlideTime() > cc.noGlideDifferenceMax) {
                // check off slab movements.
                final boolean mod = BlockAccess.hasVerticalModifierAt(from, from.getLevel(), 0.3, -0.1, 0.3);
                if (!mod) {
                    result.setFailed("No vertical difference while off ground")
                            .withParameter("vertical", vertical)
                            .withParameter("last", data.lastVertical())
                            .withParameter("delta", delta)
                            .withParameter("time", data.getNoGlideTime())
                            .withParameter("max", cc.noGlideDifferenceMax);
                    handleCheckViolationAndReset(player, result, ground);
                }
            }

            // next, calculate how we should be falling.
            // ensure we have been falling though, and have at-least decent distance.
            // Check horizontal distance as-well since its possible to glide pretty far
            // before hitting the vertical distance required.
            if (data.getNoResetDescendTime() >= cc.glideDescendTimeMin
                    && ((distance >= cc.glideDescendDistanceMin)
                    || data.getGroundHorizontalDistance() >= cc.glideDescendDistanceMin)) {

                final int time = data.getInAirTime();

                // meant to stop increasing the overall expected after a certain distance
                // sort of like a reset.
                final double mod = time <= 50 ? 0.000006 :
                        MathUtil.vertical(data.getFlightDescendingLocation(), to) >= 50 ? 0.00000456
                                : 0.000006;

                final double expected = mod * Math.pow(data.getInAirTime(), 2) - 0.0011 * data.getInAirTime() + 0.077;
                final double difference = expected - delta;

                // TODO: Check for fast falling players.
                if (difference >= cc.glideMaxDifference) {
                    result.setFailed("Gliding delta not expected")
                            .withParameter("delta", delta)
                            .withParameter("e", expected)
                            .withParameter("diff", difference)
                            .withParameter("max", cc.glideMaxDifference);
                    handleCheckViolationAndReset(player, result, ground);
                }
            }
        }
    }

    /**
     * Check climbing movement.
     *
     * @param player   the player
     * @param data     the data
     * @param from     the from
     * @param vertical the vertical
     * @param cooldown the cooldown time
     * @param result   the result
     */
    private void checkClimbingMovement(Player player, MovingData data, Location from, double vertical, double cooldown, CheckResult result) {
        final double modifiedCooldown = data.ascending() ? cooldown : (cc.climbingCooldown) + (vertical * 2);
        final double max = data.ascending() ? cc.maxClimbSpeedUp : cc.maxClimbSpeedDown;
        final int time = data.ascending() ? data.ascendingTime() : data.descendingTime();

        if (time >= modifiedCooldown && vertical > max) {
            result.setFailed("Climbing a ladder too fast")
                    .withParameter("vertical", vertical)
                    .withParameter("max", max)
                    .withParameter("cooldown", modifiedCooldown)
                    .withParameter("ascending", data.ascending());
            handleCheckViolationAndReset(player, result, from);
        }
    }

    /**
     * Check the player when they haven't moved in awhile.
     * <p>
     * As of right now this is mostly a hover check.
     * <p>
     * TODO: Might not need a temporary data set.
     *
     * @param player the player
     * @param data   the data
     */
    public void checkNoMovement(Player player, MovingData data) {
        if (exempt(player)) return;
        // nukkit likes to include players before fully joining.
        if (data.to() == null) return;

        // don't wanna update the current player moving data set
        // in-case it causes issues else-where, so for now use a temporary one
        // to retrieve stuff we need right now
        final MovingData temp = MovingData.retrieveTemporary();
        MovingAccess.calculateMovement(player, temp, data.to(), player.getLocation());

        // update in-air time here since we're not moving or calculating movement.
        int inAirTime = data.getInAirTime();
        if (!temp.onGround()) {
            data.setInAirTime(data.getInAirTime() + 1);
            inAirTime++;
        } else {
            data.setInAirTime(0);
        }

        if (!temp.onGround() && temp.vertical() == 0.0 && !MovingAccess.isOnBoat(player)) {
            // player is hovering
            if (inAirTime >= cc.maxInAirHoverTime) {
                // flag player, hovering too long.
                final CheckResult result = new CheckResult();
                result.setFailed("Hovering off the ground for too long")
                        .withParameter("inAirTime", inAirTime)
                        .withParameter("max", cc.maxInAirHoverTime);

                handleCheckViolation(player, result, data.ground());
            }
        }

    }

    /**
     * Update moving data before checking flight.
     *
     * @param data the data
     */
    private void updateMovingData(MovingData data) {
        if (data.onGround()) {
            if (!data.hasSlimeblock()
                    && data.hasSlimeBlockLaunch()) {
                data.setHasSlimeBlockLaunch(false);
            }
        } else {
            if (data.descending()) {
                data.setHasSlimeBlockLaunch(false);

                // we just started descending, set.
                if (data.descendingTime() == 1) data.setFlightDescendingLocation(data.from());
            }
        }
    }

    /**
     * Retrieve the jump height.
     * TODO: Maybe too much compensation
     *
     * @param player the player
     * @return the jump height.
     */
    private double getJumpHeight(Player player) {
        double current = cc.maxJumpDistance;

        if (player.hasEffect(NukkitAccess.JUMP_BOOST_EFFECT)) {
            current += (0.4) * player.getEffect(NukkitAccess.JUMP_BOOST_EFFECT).getAmplifier();
        }
        return current;
    }

    @Override
    public void reloadConfig() {
        load();
    }

    @Override
    public void load() {
        cc.load(configuration);
        CheckTimings.registerTiming(checkType);
    }
}
