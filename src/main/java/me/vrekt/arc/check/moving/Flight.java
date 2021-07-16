package me.vrekt.arc.check.moving;

import cn.nukkit.Player;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import me.vrekt.arc.check.Check;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.check.result.CheckResult;
import me.vrekt.arc.compatibility.NukkitAccess;
import me.vrekt.arc.compatibility.block.BlockAccess;
import me.vrekt.arc.data.moving.MovingData;
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
     *
     * Ground distance threshold is lenient here to account for bedrock movement.
     */
    private double maxJumpDistance, maxClimbSpeedUp, maxClimbSpeedDown, climbingCooldown, groundDistanceThreshold, groundDistanceHorizontalCap;

    /**
     * The minimum distance required to move to check vclip.
     * The minimum distance required to update the players safe location.
     */
    private double verticalClipMinimum, safeDistanceUpdateThreshold;

    /**
     * The max ascend time
     * The amount to add to {@code maxAscendTime} when the player has jump boost.
     * Ascend cooldown work around.
     */
    private int maxAscendTime, jumpBoostAscendAmplifier, ascendCooldown;

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
        addConfigurationValue("max-climbing-speed-up", 0.12);
        addConfigurationValue("max-climbing-speed-down", 0.151);
        addConfigurationValue("climbing-cooldown", 5);
        addConfigurationValue("max-ascend-time", 7);
        addConfigurationValue("ascend-cooldown", 3);
        addConfigurationValue("jump-boost-ascend-amplifier", 3);
        addConfigurationValue("ground-distance-threshold", 2.0);
        addConfigurationValue("ground-distance-horizontal-cap", 0.50);
        addConfigurationValue("slime-block-distance-fallen-threshold", 0);
        addConfigurationValue("vertical-clip-vertical-minimum", 0.99);
        addConfigurationValue("safe-location-update-distance-threshold", 1.99);

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

        final CheckResult result = new CheckResult();

        // from, to, ground and safe location(s)
        final Location from = data.from();
        final Location to = data.to();
        final Location ground = data.ground();
        final Location safe = data.getSafeLocation();

        final long now = System.currentTimeMillis();
        final double vertical = data.vertical();

        // initially, update any moving data we may need.
        updateMovingData(data, safe, to, now);

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
            if (distance >= groundDistanceThreshold) {
                // high off ground (hopefully) check.
                // make sure we are within the limits of the ground.
                // we don't want a flag when the player is wildly jumping around.
                final double hDist = MathUtil.horizontal(ground, to);
                if (ascendingTime >= 5 && hDist < groundDistanceHorizontalCap) {
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

            // go back to where we were.
            // maybe ground later.

            // TODO: Workaround here with the ascend cooldown.
            // TODO: Bedrock movement is weird!
            // If vertical is greater than 1.44, ignore the cooldown.
            // Vertical is too high for the player so its sketchy.
            if (vertical > maxJumpHeight && (vertical >= 1.44 || ascendingTime >= ascendCooldown)) {
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

            if (ascendingTime > (maxAscendTime + modifier) && !data.hadClimbable()) {
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
     * Check if the player moved vertically through a solid block.
     * <p>
     * TODO: Does not work properly with Nukkit yet.
     *
     * @param player   the player
     * @param result   the result
     * @param safe     the safe location
     * @param from     the from
     * @param to       the to
     * @param vertical the vertical
     * @return {@code true} if the player moved through a solid block.
     */
    private boolean checkIfMovedThroughSolidBlock(Player player, CheckResult result, Location safe, Location from, Location to, double vertical) {
        if (vertical >= verticalClipMinimum) {
            // safe
            final double min1 = Math.min(safe.getY(), to.getY()) - 1;
            final double max1 = Math.max(safe.getY(), to.getY()) + 1;

            // from
            final double min2 = Math.min(from.getY(), to.getY()) - 1;
            final double max2 = Math.max(from.getY(), to.getY()) + 1;

            if (hasSolidBlockBetween(min1, max1, player.getLevel(), safe)) {
                result.setFailed("Attempted to move through a block")
                        .withParameter("vertical", vertical)
                        .withParameter("min", verticalClipMinimum)
                        .withParameter("safe", true);
                return handleCheckViolationAndReset(player, result, safe);
            } else if (hasSolidBlockBetween(min2, max2, player.getLevel(), from)) {
                result.setFailed("Attempted to move through a block")
                        .withParameter("vertical", vertical)
                        .withParameter("min", verticalClipMinimum)
                        .withParameter("from", true);
                return handleCheckViolationAndReset(player, result, from);
            }
        }

        return false;
    }

    /**
     * Check if there is a solid block between the coordinates.
     *
     * @param min    the min
     * @param max    the max
     * @param level  the level
     * @param origin the origin
     * @return the result
     */
    private boolean hasSolidBlockBetween(double min, double max, Level level, Location origin) {
        for (double y = min; y <= max + 1; y++) {
            if (BlockAccess.isConsideredGround(level.getBlock(
                    MathUtil.floor(origin.getX()),
                    MathUtil.floor(y),
                    MathUtil.floor(origin.getZ())))) return true;
        }
        return false;
    }

    /**
     * Update moving data before checking flight.
     * <p>
     * TODO: WIP.
     * <p>
     *
     * @param data the data
     * @param safe the safe location
     * @param to   the to location
     * @param time the current time
     */
    private void updateMovingData(MovingData data, Location safe, Location to, long time) {
        data.climbTime(data.hasClimbable() ? data.climbTime() + 1 : 0);
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
        verticalClipMinimum = configuration.getDouble("vertical-clip-vertical-minimum");
        safeDistanceUpdateThreshold = configuration.getDouble("safe-location-update-distance-threshold");
    }
}
