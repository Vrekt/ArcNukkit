package arc.event.packet.listeners;

import arc.data.inventory.InventoryData;
import arc.event.packet.PacketListener;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.network.protocol.EntityEventPacket;

/**
 * Listens for the packet EntityEventPacket
 */
public final class EntityEventPacketListener implements PacketListener {

    @Override
    public void onPacketReceiving(DataPacketReceiveEvent event) {
        final var packet = (EntityEventPacket) event.getPacket();
        final var player = event.getPlayer();

        // we are eating an item.
        if (packet.event == EntityEventPacket.EATING_ITEM) {
            final var data = InventoryData.getData(player);
            final var currentPackets = data.eatingPackets();
            if (currentPackets == 0) {
                // set we are consuming.
                data.isConsuming(true);
                data.startConsumeTime(System.currentTimeMillis());
            }

            data.eatingPackets(currentPackets + 1);
        }
    }
}
