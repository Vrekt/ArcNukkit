package me.vrekt.arc.check.combat;

import cn.nukkit.Player;
import me.vrekt.arc.check.Check;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.check.result.CheckResult;
import me.vrekt.arc.data.combat.CombatData;
import me.vrekt.arc.timings.CheckTimings;

/**
 * Checks if the player is clicking too fast.
 * <p>
 * Adopted from ArcBukkit KillAura AttackSpeed check.
 * <p>
 * TODO: Very experimental
 * <p>
 * TODO: YSK: Various clients will flag this, even if it seems they are not modifying behavior.
 */
public final class FastClick extends Check {

    /**
     * The max packets per second.
     * The min time between current swing and last swing.
     */
    private int maxPacketsPerSecond, maxBlockSwingPackets, minSwingDelta;

    /**
     * The time required since the player last interacted with a block to check.
     */
    private long minLastBlockInteractRequired;

    /**
     * If attacks should be cancelled.
     */
    private boolean cancelAttacks;

    public FastClick() {
        super(CheckType.FAST_CLICK);

        enabled(true)
                .cancel(false)
                .cancelLevel(0)
                .notify(true)
                .notifyEvery(2)
                .ban(false)
                .kick(false)
                .build();

        addConfigurationValueWithComment("max-swing-packets-per-second", 12,
                "The max amount of swing packets allowed per second.");
        addConfigurationValueWithComment("max-block-swing-packets", 30,
                "The max amount of swing packets allowed when breaking a block.");
        addConfigurationValueWithComment("min-swing-delta", 50,
                "The minimum time allowed between (currentTime - lastSwingTime)");
        addConfigurationValueWithComment("min-last-block-interact-required", 500,
                "The minimum time between the current time and the last time a player interacted with a block to check FastClick.");
        addConfigurationValueWithComment("cancel-attacks", true,
                "If attacks should be cancelled if the player violates this check.");
        if (enabled()) load();
    }

    /**
     * Check
     *
     * @param player           the player
     * @param data             their data
     * @param wasBlockInteract if a block was interacted with.
     * @param lastAttack       last attack delta
     * @return the result
     */
    public boolean check(Player player, CombatData data, boolean wasBlockInteract, long lastAttack) {
        if (exempt(player)) return false;

        final int swings = data.getTotalArmSwings() + 1;
        data.setTotalArmSwings(swings);

        final long now = System.currentTimeMillis();
        final long delta = now - data.getLastSwingTime();
        final long interactDelta = now - data.getLastBlockInteract();
        data.setLastSwingTime(System.currentTimeMillis());
        final CheckResult result = new CheckResult();

        if (delta <= minSwingDelta
                && interactDelta >= minLastBlockInteractRequired
                && !wasBlockInteract) {

            if (lastAttack >= 1000 && !data.ignoreFirst()) {
                stopTiming(player);
                data.setIgnoreFirst(true);
                return false;
            }

            result.setFailed("Swing delta below min")
                    .withParameter("delta", delta)
                    .withParameter("min", minSwingDelta);
            data.setCancelNextAttack(cancelAttacks);
        }

        // check if we have reached 1 or more seconds.
        if (now - data.getLastSwingReset() >= 1000) {
            // reset data
            data.setLastSwingReset(System.currentTimeMillis());
            data.setTotalArmSwings(0);

            // check swings against max.
            final boolean maxSwings = swings >= maxPacketsPerSecond;
            if (maxSwings && !result.failed()
                    && interactDelta >= minLastBlockInteractRequired && !wasBlockInteract) {
                result.setFailed("Too many swings per second")
                        .withParameter("swings", swings)
                        .withParameter("max", maxPacketsPerSecond);
                data.setCancelNextAttack(cancelAttacks);
            }

            // check how many times a block was clicked.
            if (data.getTotalBlockInteracts() >= maxBlockSwingPackets
                    && !result.failed()) {
                result.setFailed("Too many block swings per second")
                        .withParameter("swings", data.getTotalBlockInteracts())
                        .withParameter("max", maxBlockSwingPackets);
                // we do not need to cancel attacks here.
            }

            data.setTotalBlockInteracts(0);
            data.setIgnoreFirst(false);
            // reset our data if we have not reached max.
            if (!maxSwings) data.setCancelNextAttack(false);
        }

        stopTiming(player);
        return checkViolation(player, result);
    }

    @Override
    public void reloadConfig() {
        load();
    }

    @Override
    public void load() {
        maxPacketsPerSecond = configuration.getInt("max-swing-packets-per-second");
        minSwingDelta = configuration.getInt("min-swing-delta");
        maxBlockSwingPackets = configuration.getInt("max-block-swing-packets");
        minLastBlockInteractRequired = configuration.getLong("min-last-block-interact-required");
        cancelAttacks = configuration.getBoolean("cancel-attacks");

        CheckTimings.registerTiming(checkType);
    }
}
