package me.vrekt.arc.check.moving;

import cn.nukkit.Player;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.level.Location;
import me.vrekt.arc.check.Check;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.check.result.CancelType;
import me.vrekt.arc.check.result.CheckResult;
import me.vrekt.arc.compatibility.NukkitCompatibility;
import me.vrekt.arc.data.moving.MovingData;
import me.vrekt.arc.utility.MovingUtil;
import me.vrekt.arc.utility.block.Blocks;

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
     * The ascend cooldown
     * Max climbing speeds
     * The amount of time a player has to be on a climbable
     */
    private double maxJumpDistance, ascendCooldown, maxClimbSpeed, climbingCooldown;

    /**
     * The max ascend time
     * The amount to add to {@code maxAscendTime} when the player has jump boost.
     */
    private int maxAscendTime, jumpBoostAscendAmplifier;

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
        addConfigurationValue("ascend-cooldown", 3);
        addConfigurationValue("max-climbing-speed", 0.21);
        addConfigurationValue("climbing-cooldown", 7);
        addConfigurationValue("max-ascend-time", 7);
        addConfigurationValue("jump-boost-ascend-amplifier", 3);

        if (enabled()) load();
    }

    /**
     * Check the player
     *
     * @param player the player
     * @param data   their data
     */
    public void check(Player player, MovingData data) {
        if (!enabled() || exempt(player)) return;

        final CheckResult result = new CheckResult();
        final double vertical = data.vertical();
        final Location to = data.to();

        // check if we have a slab.
        final boolean hasSlab = MovingUtil.hasBlock(to, 0.3, -0.1, 0.3, Blocks::isSlab);
        final boolean hasStair = MovingUtil.hasBlock(to, 0, -0.5, 0, Blocks::isStair);
        final boolean hasFence = MovingUtil.hasBlock(to, 0.5, -1, 0.5, block -> (Blocks.isFence(block) || Blocks.isFenceGate(block)));

        // check if its a valid vertical move.
        final boolean hasVerticalMove = vertical > 0.0
                && player.riding == null
                && !hasSlab
                && !hasStair
                && !hasFence
                && !data.inLiquid()
                && !data.hasClimbable();

        // check vertical distance moves,
        // basically anything over 0.42
        if (hasVerticalMove) {
            if (checkVerticalMove(player, data, vertical, result)) handleCancel(player, data, result);
        }

        result.reset();
        if (data.hasClimbable()) {
            // wait for cooldown.
            // this helps in situations, for example when a player jumps onto a ladder.
            data.climbTime(data.climbTime() + 1);

            final double cooldown = climbingCooldown - (vertical * 2);
            if (data.climbTime() >= cooldown) {
                if (checkClimbingMovement(player, data, vertical, cooldown, result))
                    handleCancel(player, data, result);
            }
        } else {
            data.climbTime(0);
        }

        result.reset();
    }

    /**
     * Check vertical moves
     *
     * @param player   the player
     * @param data     the data
     * @param vertical the vertical
     * @param result   the result
     */
    private boolean checkVerticalMove(Player player, MovingData data, double vertical, CheckResult result) {
        if (data.ascending()) {
            // check jumping height.
            if (data.ascendingTime() >= ascendCooldown) {
                // ensure we didn't walk up a block that modifies your vertical
                final double maxJumpHeight = getJumpHeight(player);

                if (vertical > maxJumpHeight) {
                    result.setFailed("Vertical move greater than max jump height.");
                    result.parameter("vertical", vertical);
                    result.parameter("max", maxJumpHeight);
                    return checkViolation(player, result, data.from(), CancelType.FROM).cancel();
                }
            }

            // check ascending time.
            final int modifier = player.hasEffect(NukkitCompatibility.JUMP_BOOST_EFFECT)
                    ? player.getEffect(NukkitCompatibility.JUMP_BOOST_EFFECT).getAmplifier() + jumpBoostAscendAmplifier : 0;
            if (data.ascendingTime() > (maxAscendTime + modifier)) {
                result.setFailed("Ascending for too long");
                result.parameter("vertical", vertical);
                result.parameter("time", data.ascendingTime());
                result.parameter("max", (maxAscendTime + modifier));
                return checkViolation(player, result, data.ground(), CancelType.GROUND).cancel();
            }
        }
        return false;
    }

    /**
     * Check climbing movement.
     *
     * @param player   the player
     * @param data     the data
     * @param vertical the vertical
     * @param result   the result
     */
    private boolean checkClimbingMovement(Player player, MovingData data, double vertical, double cooldown, CheckResult result) {
        if (vertical > maxClimbSpeed && ((data.ascending() && data.ascendingTime() >= cooldown) || (data.descending() && data.descendingTime() >= cooldown))) {
            result.setFailed("Climbing a ladder too fast");
            result.parameter("vertical", vertical);
            result.parameter("max", maxClimbSpeed);
            result.parameter("cooldown", cooldown);
            return checkViolation(player, result, data.from(), CancelType.FROM).cancel();
        }
        return false;
    }

    /**
     * Handle cancel
     *
     * @param player the player
     * @param result the result
     */
    private void handleCancel(Player player, MovingData data, CheckResult result) {
        switch (result.cancelType()) {
            case FROM:
                player.teleport(data.from(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                break;
            case GROUND:
                player.teleport(result.cancel(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                break;
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

        if (player.hasEffect(NukkitCompatibility.JUMP_BOOST_EFFECT)) {
            current += (0.4) * player.getEffect(NukkitCompatibility.JUMP_BOOST_EFFECT).getAmplifier();
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
        ascendCooldown = configuration.getInt("ascend-cooldown");
        maxClimbSpeed = configuration.getDouble("max-climbing-speed");
        climbingCooldown = configuration.getDouble("climbing-cooldown");
        maxAscendTime = configuration.getInt("max-ascend-time");
        jumpBoostAscendAmplifier = configuration.getInt("jump-boost-ascend-amplifier");
    }
}
