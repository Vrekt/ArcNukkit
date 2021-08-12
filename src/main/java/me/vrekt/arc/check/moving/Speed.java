package me.vrekt.arc.check.moving;

import cn.nukkit.Player;
import cn.nukkit.level.Location;
import cn.nukkit.potion.Effect;
import me.vrekt.arc.check.Check;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.check.result.CheckResult;
import me.vrekt.arc.compatibility.NukkitAccess;
import me.vrekt.arc.data.moving.MovingData;
import me.vrekt.arc.timings.CheckTimings;
import me.vrekt.arc.utility.math.MathUtil;

/**
 * Checks if the player is moving too fast.
 */
public final class Speed extends Check {

    /**
     * Max distance player can move in one movement.
     */
    private double maxLargeDistanceMovement;

    public Speed() {
        super(CheckType.SPEED);

        enabled(true)
                .cancel(true)
                .cancelLevel(0)
                .notify(true)
                .notifyEvery(1)
                .ban(false)
                .banLevel(20)
                .kick(false)
                .build();

        addConfigurationValue("max-large-distance-movement", 4);

        if (enabled()) load();
    }

    /**
     * Check
     *
     * @param player the player
     * @param data   their data
     */
    public void check(Player player, MovingData data) {
        if (exempt(player)) return;
        startTiming(player);

        final Location from = data.from();
        final Location to = data.to();

        final double horizontal = MathUtil.horizontal(from, to);
        final double base = getBaseMoveSpeed(player);
        final CheckResult result = new CheckResult();

        // cancel large movements.
        if (horizontal >= maxLargeDistanceMovement) {
            result.setFailed("Large movement")
                    .withParameter("h", horizontal)
                    .withParameter("max", maxLargeDistanceMovement);
            handleCheckViolationAndReset(player, result, from);
        }

        stopTiming(player);
    }

    /**
     * Return our base move speed.
     *
     * @param player the player
     * @return players move speed
     */
    private double getBaseMoveSpeed(Player player) {
        double baseSpeed = 0.289;

        for (Effect effect : player.getEffects().values()) {
            if (effect.getName().equalsIgnoreCase(NukkitAccess.MOVE_SPEED_POTION)) {
                baseSpeed *= 1.0 + 0.2 * effect.getAmplifier() + 1;
            }
        }
        return baseSpeed;
    }

    @Override
    public void reloadConfig() {
        load();
    }

    @Override
    public void load() {
        maxLargeDistanceMovement = configuration.getDouble("max-large-distance-movement");
        CheckTimings.registerTiming(checkType);
    }
}
