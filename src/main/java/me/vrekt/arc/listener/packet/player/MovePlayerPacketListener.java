package me.vrekt.arc.listener.packet.player;

import cn.nukkit.Player;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.data.moving.MovingData;
import me.vrekt.arc.listener.packet.NukkitPacketHandler;
import me.vrekt.arc.listener.packet.NukkitPacketListener;

/**
 * Listens for {@link cn.nukkit.network.protocol.MovePlayerPacket}
 */
public final class MovePlayerPacketListener extends NukkitPacketListener {

    public MovePlayerPacketListener(NukkitPacketHandler handler) {
        super(handler);
    }

    @Override
    protected void onPacketReceiving0(DataPacketReceiveEvent event) {
        final Player player = event.getPlayer();
        if (!handler.isExempt(CheckType.MORE_PACKETS, player)) {
            if (MovingData.get(player).incrementMovePlayerPacketsAndCheckCancel()) {
                event.setCancelled();
            }
        }
    }
}
