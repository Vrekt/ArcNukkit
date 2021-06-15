package me.vrekt.arc.listener.packet;

import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.event.server.DataPacketSendEvent;

/**
 * Represents a Nukkit packet listener.
 */
public abstract class NukkitPacketListener {

    /**
     * The packet handler.
     */
    protected NukkitPacketHandler handler;

    public NukkitPacketListener(NukkitPacketHandler handler) {
        this.handler = handler;
    }

    public NukkitPacketListener() {

    }

    /**
     * Invoked when a packet is receiving.
     *
     * @param event the event
     */
    public void onPacketReceiving(DataPacketReceiveEvent event) {

    }

    /**
     * Invoked when a packet is sending.
     *
     * @param event the event
     */
    public void onPacketSending(DataPacketSendEvent event) {

    }

}
