package me.vrekt.arc.listener.block;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import me.vrekt.arc.Arc;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.check.block.FastBreak;
import me.vrekt.arc.check.block.Nuker;
import me.vrekt.arc.data.block.BlockData;

/**
 * Listens for block actions
 */
public final class BlockListener implements Listener {

    /**
     * The nuker check.
     */
    private final Nuker nuker;

    /**
     * the fast break check.
     */
    private final FastBreak fastBreak;

    public BlockListener() {
        nuker = Arc.getInstance().getCheckManager().getCheck(CheckType.NUKER);
        fastBreak = Arc.getInstance().getCheckManager().getCheck(CheckType.FAST_BREAK);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    private void onBlockBreak(BlockBreakEvent event) {
        final Player player = event.getPlayer();
        if (!nuker.isPacketCheck()) event.setCancelled(nuker.check(player));
        event.setCancelled(fastBreak.check(player, BlockData.get(player), event.getBlock()));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK
                && event.getPlayer().getGamemode() != 1) {
            final Player player = event.getPlayer();
            final BlockData data = BlockData.get(player);

            final Block current = event.getBlock();

            data.setItemInHand(player.getInventory().getItemInHand());
            data.addBlockInteractedWith(current);
        }
    }

}
