package me.vrekt.arc.listener.block;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import me.vrekt.arc.Arc;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.check.block.Nuker;

/**
 * Listens for block actions
 */
public final class BlockListener implements Listener {

    /**
     * The nuker check.
     */
    private final Nuker nuker;

    public BlockListener() {
        nuker = Arc.getInstance().getCheckManager().getCheck(CheckType.NUKER);
    }

    @EventHandler
    private void onBlockBreak(BlockBreakEvent event) {
        final Player player = event.getPlayer();
        if (!nuker.isPacketCheck()) event.setCancelled(nuker.check(player));
    }

}
