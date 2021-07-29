package me.vrekt.arc.check.moving;

import cn.nukkit.Player;
import cn.nukkit.level.Location;
import me.vrekt.arc.check.Check;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.check.result.CheckResult;
import me.vrekt.arc.compatibility.NukkitAccess;
import me.vrekt.arc.compatibility.block.BlockAccess;
import me.vrekt.arc.data.moving.MovingData;
import me.vrekt.arc.utility.MovingAccess;
import me.vrekt.arc.utility.math.MathUtil;

/**
 * Checks if the player is flying among other things
 * TODO: Trade-off: due to weird client behavior or maybe batching of packets
 * TODO: Player will send a vertical of 1.0+ during a normal jump.
 * TODO: This causes false positives, so added a ascend cooldown.
 * TODO: This allows step to bypass, but only once or twice until its flagged.
 */
public final class Flight extends Check {

    /**
     * The max jump distance.
     * Max climbing speeds
     * The amount of time a player has to be on a climbable
     * <p>
     * The distance (from the ground) required to start checking ascending stuff.
     * The distance (from the ground) (horizontal) that is capped, if the hDist > capped, no check is executed.
     * <p>
     * Ground distance threshold is lenient here to account for bedrock movement.
     */
    private double maxJumpDistance, maxClimbSpeedUp, maxClimbSpeedDown, climbingCooldown, groundDistanceThreshold, groundDistanceHorizontalCap;

    /**
     * The max ascend time
     * The amount to add to {@code maxAscendTime} when the player has jump boost.
     * Ascend cooldown work around.
     * Max time allowed to b e hovering
     */
    private int maxAscendTime, jumpBoostAscendAmplifier, ascendCooldown, maxInAirHoverTime;

    /**
     * The max difference allowed between a=(vertical speed - expected) then, (vertical speed - a)
     */
    private double slimeblockMaxScoreDiff;

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

        addConfigurationValue("max-jump-distance", 0.42);
        addConfigurationValue("max-climbing-speed-up", 0.21);
        addConfigurationValue("max-climbing-speed-down", 0.21);
        addConfigurationValue("climbing-cooldown", 7);
        addConfigurationValue("max-ascend-time", 7);
        addConfigurationValue("ascend-cooldown", 3);
        addConfigurationValue("jump-boost-ascend-amplifier", 3);
        addConfigurationValue("ground-distance-threshold", 2.0);
        addConfigurationValue("ground-distance-horizontal-cap", 0.50);
        addConfigurationValue("max-in-air-hover-time", 6);
        addConfigurationValue("slimeblock-max-score-difference", 0.42);

        if (enabled()) load();
    }

    /**
     * Check the player
     *
     * @param player the player
     * @param data   their data
     */
    public void check(Player player, MovingData data) {
        if (exempt(player)) return;

        // TODO: Elytra
        if (player.isGliding()) return;

        final CheckResult result = new CheckResult();

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
        if (validVerticalMove) {
            // check vertical clip regardless of ascending or descending state.
            // return here since we don't want the rest of the check interfering with setback.
            //  final boolean failed = checkIfMovedThroughSolidBlock(player, result, safe, from, to, vertical);
            if (!hasVerticalModifier) {
                checkVerticalMove(player, data, ground, from, to, vertical, data.ascendingTime(), result);
            }
        }

        if (data.hasClimbable()) {
            // the ascending cooldown, will need to be reversed if descending.
            final double cooldown = climbingCooldown - (vertical * 2);
            if (data.climbTime() >= cooldown) checkClimbingMovement(player, data, from, vertical, cooldown, result);
        }

        // update safe location if not failed and on ground.
        if (!result.hasFailedBefore() && data.onGround()) data.setSafeLocation(to);
    }

    /**
     * Check the players vertical movement.
     *
     * @param player        the player
     * @param data          their data
     * @param ground        ground location
     * @param from          the from
     * @param to            movedTo
     * @param vertical      vertical
     * @param ascendingTime ascendingTime
     * @param result        result
     */
    private void checkVerticalMove(Player player, MovingData data, Location ground, Location from, Location to, double vertical, int ascendingTime, CheckResult result) {
        if (data.ascending()) {
            // check ground distance.
            final double distance = MathUtil.vertical(ground, to);

            final boolean hasSlimeblock = data.hasSlimeblock();
            if (hasSlimeblock && vertical > 0.42 && distance > 3f) {
                data.setHasSlimeBlockLaunch(true);
            }


            if (distance >= groundDistanceThreshold) {
                // high off ground (hopefully) check.
                // make sure we are within the limits of the ground.
                // we don't want a flag when the player is wildly jumping around.
                final double hDist = MathUtil.horizontal(ground, to);
                if (ascendingTime >= 5 && hDist < groundDistanceHorizontalCap && !data.hasSlimeBlockLaunch()) {
                    result.setFailed("Vertical distance from ground greater than allowed within limits.")
                            .withParameter("distance", distance)
                            .withParameter("threshold", groundDistanceThreshold)
                            .withParameter("hDist", hDist)
                            .withParameter("cap", groundDistanceHorizontalCap);
                    handleCheckViolationAndReset(player, result, ground);
                }
            }

            // TODO: check if we have launch from a slime-block.
            // ensure we didn't walk up a block that modifies your vertical
            final double maxJumpHeight = getJumpHeight(player);

            if (hasSlimeblock) {
                // player was launched, set state
                final boolean launched = data.hasSlimeBlockLaunch();
                if (launched) {
                    // get rough estimate of player fall distance
                    final double fallen = MathUtil.vertical(data.getFlightDescendingLocation() == null ? from
                            : data.getFlightDescendingLocation(), from);
                    if (data.getFlightDescendingLocation() != null) {
                        // decrease the modifier if we fall from a significant height, +
                        // to prevent one time abuse
                        final double modifier = fallen > 15 ? 0.11D : 0.18d;
                        final double rough = 0.4D + fallen * modifier;
                        final double diff = Math.abs(vertical - rough);
                        final double score = vertical - diff;
                        if (score >= slimeblockMaxScoreDiff) {
                            result.setFailed("Bounced too high from a slimeblock")
                                    .withParameter("vertical", vertical)
                                    .withParameter("rough", rough)
                                    .withParameter("diff", diff)
                                    .withParameter("score", score)
                                    .withParameter("max", slimeblockMaxScoreDiff);
                            handleCheckViolationAndReset(player, result, ground);
                        }
                    }

                    // player is jumping really high with no velocity
                    if (distance <= 1f && vertical > maxJumpHeight && fallen <= 1f) {
                        result.setFailed("Vertical greater than max jump height on slimeblock")
                                .withParameter("groundDistance", distance)
                                .withParameter("vertical", vertical)
                                .withParameter("max", maxJumpHeight);
                        handleCheckViolationAndReset(player, result, ground);
                    }
                }
            }

            // go back to where we were.
            // maybe ground later.

            // TODO: Workaround here with the ascend cooldown.
            // TODO: Bedrock movement is weird!
            // If vertical is greater than 1.44, ignore the cooldown.
            // Vertical is too high for the player so its sketchy.
            if (vertical > maxJumpHeight && (vertical >= 1.44 || ascendingTime >= ascendCooldown)
                    && !data.hasSlimeBlockLaunch()) {
                result.setFailed("Vertical move greater than max jump height.")
                        .withParameter("vertical", vertical)
                        .withParameter("max", maxJumpHeight);
                handleCheckViolationAndReset(player, result, from);
            }

            // add to our modifier if we have a jump effect.
            // this will need to be amplified by the amplifier.
            final int modifier = player.hasEffect(NukkitAccess.JUMP_BOOST_EFFECT)
                    ? player.getEffect(NukkitAccess.JUMP_BOOST_EFFECT).getAmplifier() + jumpBoostAscendAmplifier
                    : 0;

            if (ascendingTime > (maxAscendTime + modifier) && !data.hadClimbable() && !data.hasSlimeBlockLaunch()) {
                result.setFailed("Ascending for too long")
                        .withParameter("vertical", vertical)
                        .withParameter("time", data.ascendingTime())
                        .withParameter("max", (maxAscendTime + modifier));
                handleCheckViolationAndReset(player, result, from);
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
        final double modifiedCooldown = data.ascending() ? cooldown : (climbingCooldown) + (vertical * 2);
        final double max = data.ascending() ? maxClimbSpeedUp : maxClimbSpeedDown;
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
        MovingAccess.calculateMovement(temp, data.to(), player.getLocation());

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
            if (inAirTime >= maxInAirHoverTime) {
                // flag player, hovering too long.
                final CheckResult result = new CheckResult();
                result.setFailed("Hovering off the ground for too long")
                        .withParameter("inAirTime", inAirTime)
                        .withParameter("max", maxInAirHoverTime);

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
        double current = maxJumpDistance;

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
        maxJumpDistance = configuration.getDouble("max-jump-distance");
        maxClimbSpeedUp = configuration.getDouble("max-climbing-speed-up");
        maxClimbSpeedDown = configuration.getDouble("max-climbing-speed-down");
        climbingCooldown = configuration.getDouble("climbing-cooldown");
        maxAscendTime = configuration.getInt("max-ascend-time");
        ascendCooldown = configuration.getInt("ascend-cooldown");
        jumpBoostAscendAmplifier = configuration.getInt("jump-boost-ascend-amplifier");
        groundDistanceThreshold = configuration.getDouble("ground-distance-threshold");
        groundDistanceHorizontalCap = configuration.getDouble("ground-distance-horizontal-cap");
        maxInAirHoverTime = configuration.getInt("max-in-air-hover-time");
        slimeblockMaxScoreDiff = configuration.getDouble("slimeblock-max-score-difference");
    }
}
