package arc.event.packet.listeners;

import arc.Arc;
import arc.event.nukkit.PlayerHungerUpdateEvent;
import arc.event.packet.PacketListener;
import cn.nukkit.event.server.DataPacketSendEvent;
import cn.nukkit.network.protocol.UpdateAttributesPacket;

/**
 * Listens for the packet UpdateAttributesPacket
 * TODO: Unused
 */
public final class UpdateAttributesPacketListener implements PacketListener {

    @Override
    public void onPacketSending(DataPacketSendEvent event) {
        final var packet = (UpdateAttributesPacket) event.getPacket();
        final var player = event.getPlayer();
        final var attribute = packet.entries[0];
        // check if the first attribute is related to hunger.
        if (attribute.getName().equals("minecraft:player.hunger")) {
            // hunger was updated, fire event
            final var hungerUpdateEvent = new PlayerHungerUpdateEvent(player, attribute);
            Arc.plugin().getServer().getPluginManager().callEvent(hungerUpdateEvent);
            event.setCancelled(hungerUpdateEvent.isCancelled());
        }
    }
}
