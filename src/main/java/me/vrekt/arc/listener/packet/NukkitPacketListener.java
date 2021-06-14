package me.vrekt.arc.listener.packet;


import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.network.protocol.ProtocolInfo;
import me.vrekt.arc.Arc;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.listener.packet.inventory.InventoryTransactionPacketListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Listens for packets.
 * TODO: Performance issues
 */
public final class NukkitPacketListener implements Listener {

    /**
     * List of packet listeners.
     */
    private final Map<Byte, PacketListener> packetListeners = new HashMap<>();

    public NukkitPacketListener() {
        registerInventoryListeners();
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
