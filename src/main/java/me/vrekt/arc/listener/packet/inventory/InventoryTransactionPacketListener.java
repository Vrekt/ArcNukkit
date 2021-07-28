package me.vrekt.arc.listener.packet.inventory;

import cn.nukkit.Player;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.inventory.transaction.data.UseItemData;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemEdible;
import cn.nukkit.item.ItemPotion;
import cn.nukkit.network.protocol.InventoryTransactionPacket;
import me.vrekt.arc.Arc;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.check.block.Nuker;
import me.vrekt.arc.data.player.PlayerData;
import me.vrekt.arc.listener.packet.NukkitPacketListener;

/**
 * Listens for the packet InventoryTransactionPacket
 */
public final class InventoryTransactionPacketListener extends NukkitPacketListener {

    /**
     * The nuker check.
     */
    private final Nuker nuker;

    public InventoryTransactionPacketListener() {
        super();

        nuker = Arc.getInstance().getCheckManager().getCheck(CheckType.NUKER);
    }

    @Override
    protected void onPacketReceiving0(DataPacketReceiveEvent event) {
        final InventoryTransactionPacket packet = (InventoryTransactionPacket) event.getPacket();

        if (packet.transactionType == InventoryTransactionPacket.TYPE_RELEASE_ITEM) {
            final Player player = event.getPlayer();
            PlayerData.get(player).isConsuming(false);
        } else if (packet.transactionType == InventoryTransactionPacket.TYPE_USE_ITEM) {
            if (packet.transactionData instanceof UseItemData) {
                final UseItemData useItemData = ((UseItemData) packet.transactionData);
                final Item item = useItemData.itemInHand;

                if (item instanceof ItemEdible || item instanceof ItemPotion) {
                    final PlayerData data = PlayerData.get(event.getPlayer());
                    if (!data.isConsuming()) {
                        data.isConsuming(true);
                        data.consumeStartTime(System.currentTimeMillis());
                    }
                }

                if (useItemData.actionType == InventoryTransactionPacket.USE_ITEM_ACTION_BREAK_BLOCK) {
                    // player sent break block packet.
                    final boolean cancel = nuker.check(event.getPlayer());
                    event.setCancelled(cancel);
                }
            }
        }
    }
}
