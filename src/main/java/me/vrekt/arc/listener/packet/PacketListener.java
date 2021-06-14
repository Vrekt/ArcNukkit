package me.vrekt.arc.listener.packet;

import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.event.server.DataPacketSendEvent;

/**
 * Represents a Nukkit packet listener.
 */
public interface PacketListener {

    /**
     * Invoked when a packet is receiving.
     *
     * @param event the event
     */
    default void onPacketReceiving(DataPacketReceiveEvent event) {
        //
    }

    /**
     * Invoked when a packet is sending.
     *
     * @param event the eevnt
     */
    default void onPacketSending(DataPacketSendEvent event) {

    }

}
