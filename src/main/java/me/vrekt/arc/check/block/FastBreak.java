package me.vrekt.arc.check.block;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import me.vrekt.arc.check.Check;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.check.result.CheckResult;
import me.vrekt.arc.data.block.BlockData;

/**
 * Check if the player is breaking blocks too fast.
 */
public final class FastBreak extends Check {

    /**
     * The max difference allowed between (how long it took to break the block) - (how long it should have taken)
     */
    private long maxBreakDelta;

    public FastBreak() {
        super(CheckType.FAST_BREAK);

        enabled(true)
                .cancel(true)
                .cancelLevel(0)
                .notify(true)
                .notifyEvery(1)
                .ban(false)
                .kick(false)
                .build();

        addConfigurationValue("max-break-delta", 100);

        if (enabled()) load();
    }

    /**
     * Check the player
     *
     * @param player the player
     * @param data   their data
     * @param block  the block they broke.
     * @return the result
     */
    public boolean check(Player player, BlockData data, Block block) {
        if (exempt(player) || player.getGamemode() == 1) return false;

        // the result.
        final CheckResult result = new CheckResult();

        // Player just instantly broke the block without interacting first, possible flag.
        if (!data.wasInteractedWith(block)) {
            result.setFailed("No interact before breaking a block.");
            return checkViolation(player, result);
        }

        // ensure we actually broke the correct block we have data for I guess?
        final long delta = System.currentTimeMillis() - data.getBlockInteractedTime(block);
        final Item now = player.getInventory().getItemInHand();
        final Item compare = now.getId() == 0 ? data.getItemInHand() : now;

        // retrieve the base time.
        final double baseTime = block.getBreakTime(compare, player) * 1000;
        if (baseTime == 0.0) return false; // return, no check needed.

        // find difference.
        final long baseTimeDelta = (long) (baseTime - delta);

        // Difference too large, flag.
        if (baseTimeDelta >= maxBreakDelta) {
            result.setFailed("Broke a block too fast.")
                    .withParameter("baseTime", baseTime)
                    .withParameter("btDelta", baseTimeDelta)
                    .withParameter("breakDelta", delta)
                    .withParameter("limit", maxBreakDelta);
        }
        return checkViolation(player, result);
    }

    @Override
    public void reloadConfig() {
        load();
    }

    @Override
    public void load() {
        maxBreakDelta = configuration.getLong("max-break-delta");
    }
}
