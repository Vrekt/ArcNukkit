package me.vrekt.arc.listener.packet;


import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.network.protocol.ProtocolInfo;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.exemption.ExemptionManager;
import me.vrekt.arc.listener.packet.inventory.InventoryTransactionPacketListener;
import me.vrekt.arc.listener.packet.player.MovePlayerPacketListener;

/**
 * Listens for packets.
 * TODO: Performance issues
 */
public final class NukkitPacketHandler implements Listener {

    /**
     * For (maybe) performance reasons, just keep everything outside a map.
     * This way we don't have to call containsKey literally every packet sent by players.
     */
    private final InventoryTransactionPacketListener inventoryTransactionPacketListener
            = new InventoryTransactionPacketListener(CheckType.FAST_USE);

    private final MovePlayerPacketListener movePlayerPacketListener
            = new MovePlayerPacketListener(CheckType.MORE_PACKETS, this);

    /**
     * The exemption manager.
     */
    private final ExemptionManager manager;

    public NukkitPacketHandler(ExemptionManager manager) {
        this.manager = manager;
    }

    /**
     * Check if a player is exempt
     *
     * @param check  the check
     * @param player the player
     * @return {@code true} if the player is exempt.
     */
    public boolean isExempt(CheckType check, Player player) {
        return manager.isPlayerExempt(player, check);
    }

    /**
     * Listen for packets going to the server.
     * <p>
     * LOWEST: Hopefully we go first?
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPacketReceiving(DataPacketReceiveEvent event) {

        switch (event.getPacket().pid()) {
            case ProtocolInfo.MOVE_PLAYER_PACKET:
                movePlayerPacketListener.onPacketReceiving(event);
                break;
            case ProtocolInfo.INVENTORY_TRANSACTION_PACKET:
                inventoryTransactionPacketListener.onPacketReceiving(event);
                break;
        }

    }

}
