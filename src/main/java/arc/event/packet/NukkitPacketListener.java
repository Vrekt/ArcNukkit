package arc.event.packet;

import arc.check.CheckType;
import arc.check.management.Checks;
import arc.event.packet.listeners.EntityEventPacketListener;
import arc.event.packet.listeners.InventoryTransactionPacketListener;
import arc.event.packet.listeners.UpdateAttributesPacketListener;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.event.server.DataPacketSendEvent;
import cn.nukkit.network.protocol.ProtocolInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Listens for packets.
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
        if (Checks.isEnabled(CheckType.FAST_USE)) {
            packetListeners.put(ProtocolInfo.INVENTORY_TRANSACTION_PACKET, new InventoryTransactionPacketListener());
            packetListeners.put(ProtocolInfo.ENTITY_EVENT_PACKET, new EntityEventPacketListener());
            packetListeners.put(ProtocolInfo.UPDATE_ATTRIBUTES_PACKET, new UpdateAttributesPacketListener());
        }
    }

    /**
     * Listen for packets being sent to the client.
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPacketSending(DataPacketSendEvent event) {
        final var pid = event.getPacket().pid();
        final var listener = packetListeners.get(pid);
        if (listener != null) listener.onPacketSending(event);
    }

    /**
     * Listen for packets going to the server.
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPacketReceiving(DataPacketReceiveEvent event) {
        final var pid = event.getPacket().pid();
        final var listener = packetListeners.get(pid);
        if (listener != null) listener.onPacketReceiving(event);
    }

}
