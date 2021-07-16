package me.vrekt.arc.check.player;

import cn.nukkit.Player;
import me.vrekt.arc.check.Check;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.check.result.CheckResult;
import me.vrekt.arc.data.player.PlayerData;

/**
 * A FastUse check.
 * Checks if players are using or consuming items too fast.
 * <p>
 * TODO: Maybe bypass:
 * TODO: Player sends x2 INVENTORY_TRANSACTION
 * TODO: 1400 is generous
 */
public final class FastUse extends Check {

    /**
     * The configuration consume time.
     */
    private long consumeTime;

    public FastUse() {
        super(CheckType.FAST_USE);
        enabled(true)
                .cancel(true)
                .cancelLevel(0)
                .notify(true)
                .notifyEvery(1)
                .ban(false)
                .kick(false)
                .build();

        addConfigurationValue("consume-time-ms", 1400);
        if (enabled()) load();
    }

    /**
     * Check a player for fast-use
     *
     * @param player the player
     * @param data   their data
     * @return the result
     */
    public boolean check(Player player, PlayerData data) {
        if (!enabled() || exempt(player)) return false;
        final long time = System.currentTimeMillis() - data.consumeStartTime();
        if (time <= consumeTime) {
            final CheckResult result = new CheckResult();
            result.setFailed("Consumed an item too fast.")
                    .withParameter("delta", time)
                    .withParameter("min", consumeTime);
            return checkViolation(player, result);
        }
        return false;
    }

    @Override
    public void reloadConfig() {
        load();
    }

    @Override
    public void load() {
        consumeTime = configuration.getLong("consume-time-ms");
    }
}
