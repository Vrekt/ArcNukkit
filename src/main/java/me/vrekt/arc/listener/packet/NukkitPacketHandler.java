package me.vrekt.arc.listener.packet;


import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.network.protocol.ProtocolInfo;
import me.vrekt.arc.Arc;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.exemption.ExemptionManager;
import me.vrekt.arc.listener.packet.inventory.InventoryTransactionPacketListener;
import me.vrekt.arc.listener.packet.player.MovePlayerPacketListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Listens for packets.
 * TODO: Performance issues
 */
public final class NukkitPacketHandler implements Listener {

    /**
     * List of packet listeners.
     */
    private final Map<Byte, NukkitPacketListener> packetListeners = new HashMap<>();

    /**
     * The exemption manager.
     */
    private final ExemptionManager manager;

    public NukkitPacketHandler(ExemptionManager manager) {
        this.manager = manager;

        registerInventoryListeners();
        registerPlayerListeners();
    }

    /**
     * Register inventory type listeners.
     * This method will not enable listeners for checks that are not enabled, like fast-use.
     */
    private void registerInventoryListeners() {
        if (Arc.arc().checks().getCheck(CheckType.FAST_USE).enabled()) {
            packetListeners.put(ProtocolInfo.INVENTORY_TRANSACTION_PACKET, new InventoryTransactionPacketListener());
        }
    }

    /**
     * Register player type listeners.
     */
    private void registerPlayerListeners() {
        if (Arc.arc().checks().getCheck(CheckType.MORE_PACKETS).enabled()) {
            packetListeners.put(ProtocolInfo.MOVE_PLAYER_PACKET, new MovePlayerPacketListener(this));
        }
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
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPacketReceiving(DataPacketReceiveEvent event) {
        if (packetListeners.containsKey(event.getPacket().pid()))
            packetListeners.get(event.getPacket().pid()).onPacketReceiving(event);
    }

}
