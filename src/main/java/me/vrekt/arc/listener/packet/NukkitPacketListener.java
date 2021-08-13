package me.vrekt.arc.listener.packet;

import cn.nukkit.event.server.DataPacketReceiveEvent;

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
    public abstract void onPacketReceiving(DataPacketReceiveEvent event);

}
