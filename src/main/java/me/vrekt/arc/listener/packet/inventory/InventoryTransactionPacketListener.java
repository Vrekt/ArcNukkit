package me.vrekt.arc.listener.packet.inventory;

import cn.nukkit.Player;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.inventory.transaction.data.UseItemData;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemEdible;
import cn.nukkit.item.ItemPotion;
import cn.nukkit.network.protocol.InventoryTransactionPacket;
import me.vrekt.arc.data.player.PlayerData;
import me.vrekt.arc.listener.packet.PacketListener;

/**
 * Listens for the packet InventoryTransactionPacket
 */
public final class InventoryTransactionPacketListener implements PacketListener {

    @Override
    public void onPacketReceiving(DataPacketReceiveEvent event) {
        final InventoryTransactionPacket packet = (InventoryTransactionPacket) event.getPacket();
        if (packet.transactionType == InventoryTransactionPacket.TYPE_RELEASE_ITEM) {
            final Player player = event.getPlayer();
            PlayerData.get(player).isConsuming(false);
        } else if (packet.transactionType == InventoryTransactionPacket.TYPE_USE_ITEM) {
            if (!(packet.transactionData instanceof UseItemData)) {
                event.getPlayer().kick("No"); // ?
            }
            final Item item = ((UseItemData) packet.transactionData).itemInHand;
            if (item instanceof ItemEdible || item instanceof ItemPotion) {
                final PlayerData data = PlayerData.get(event.getPlayer());
                if (!data.isConsuming()) {
                    data.isConsuming(true);
                    data.consumeStartTime(System.currentTimeMillis());
                }
            }
        }
    }
}
