package me.vrekt.arc.listener.packet.player;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.network.protocol.AnimatePacket;
import me.vrekt.arc.Arc;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.check.combat.FastClick;
import me.vrekt.arc.data.combat.CombatData;
import me.vrekt.arc.listener.packet.NukkitPacketListener;
import me.vrekt.arc.timings.CheckTimings;

/**
 * Listen for player arm animation packets.
 */
public final class PlayerAnimatePacketListener extends NukkitPacketListener {

    /**
     * The fast click check.
     */
    private final FastClick fastClick;

    public PlayerAnimatePacketListener() {
        super();

        fastClick = Arc.getInstance().getCheckManager().getCheck(CheckType.FAST_CLICK);
    }

    @Override
    public void onPacketReceiving(DataPacketReceiveEvent event) {
        final AnimatePacket packet = (AnimatePacket) event.getPacket();
        if (packet.action == AnimatePacket.Action.SWING_ARM) {
            final Player player = event.getPlayer();
            if (player.getGamemode() == 1) return;

            CheckTimings.startTiming(CheckType.FAST_CLICK, player.getUniqueId());
            final CombatData data = CombatData.get(player);

            // Workaround: check if player is possibly interacting with a block.
            final Block b = player.getTargetBlock(6);
            final boolean hasBlock = (b != null && !(b instanceof BlockAir));
            final long now = System.currentTimeMillis();
            final long delta = now - data.getLastAttack();

            // Workaround: client sends (2) packets when attacking.
            if ((now - data.getLastSwingTime() == 0)
                    && !hasBlock) {
                data.setBatchingThreshold(data.getBatchingThreshold() + 1);
                // Check fast attacks (0 ms since), and we want to check if
                // The player has had a recent attack within the bounds of 200-1000 ms.
                // If so, and we have a threshold of 1,2 then return since
                // This possibly indicates an attack took place.
                if (((delta == 0) || (delta >= 200) && delta <= 1000)
                        && (data.getBatchingThreshold() == 1
                        || data.getBatchingThreshold() == 2)) {
                    CheckTimings.stopTiming(CheckType.FAST_CLICK, player.getUniqueId());
                    return;
                }
            } else {
                data.setBatchingThreshold(0);
            }
            event.setCancelled(fastClick.check(player, data, hasBlock, delta));
        }
    }
}
