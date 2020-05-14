package arc.event.packet.listeners;

import arc.data.inventory.InventoryData;
import arc.event.packet.PacketListener;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.network.protocol.InventoryTransactionPacket;

/**
 * Listens for the packet InventoryTransactionPacket
 */
public final class InventoryTransactionPacketListener implements PacketListener {

    @Override
    public void onPacketReceiving(DataPacketReceiveEvent event) {
        final var packet = (InventoryTransactionPacket) event.getPacket();
        final var player = event.getPlayer();

        if (packet.transactionType == InventoryTransactionPacket.TYPE_RELEASE_ITEM) {
            // we released an item.
            final var data = InventoryData.getData(player);
            data.isConsuming(false);
            data.eatingPackets(0);
        }
    }
}
