package arc.event.packet.listeners;

import arc.data.inventory.InventoryData;
import arc.event.packet.PacketListener;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.inventory.transaction.data.UseItemData;
import cn.nukkit.item.ItemEdible;
import cn.nukkit.item.ItemPotion;
import cn.nukkit.network.protocol.InventoryTransactionPacket;

/**
 * Listens for the packet InventoryTransactionPacket
 */
public final class InventoryTransactionPacketListener implements PacketListener {

    @Override
    public void onPacketReceiving(DataPacketReceiveEvent event) {
        final var packet = (InventoryTransactionPacket) event.getPacket();
        final var player = event.getPlayer();

        if (packet.transactionType
                == InventoryTransactionPacket.TYPE_RELEASE_ITEM) {
            // since we stopped using an item set we are not consuming anymore.
            final var data = InventoryData.getData(player);
            data.isConsuming(false);
            data.eatingPackets(0);
        } else if (packet.transactionType == InventoryTransactionPacket.TYPE_USE_ITEM) {
            // we are using an item, check it.
            final var itemUseData = (UseItemData) packet.transactionData;
            final var item = itemUseData.itemInHand;
            if (item instanceof ItemEdible
                    || item instanceof ItemPotion) {
                // we are consuming an edible item.
                final var data = InventoryData.getData(player);
                if (!data.isConsuming()) {
                    data.startConsumeTime(System.currentTimeMillis());
                }
                data.isConsuming(true);
            }
        }


    }
}
