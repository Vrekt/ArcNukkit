package me.vrekt.arc.listener.packet;

import cn.nukkit.event.server.DataPacketReceiveEvent;
import me.vrekt.arc.Arc;
import me.vrekt.arc.check.CheckType;

/**
 * Represents a Nukkit packet listener.
 */
public abstract class NukkitPacketListener {

    /**
     * The packet handler.
     */
    protected NukkitPacketHandler handler;

    /**
     * If this packet listener is enabled.
     */
    protected boolean enabled;

    public NukkitPacketListener(CheckType check, NukkitPacketHandler handler) {
        this.handler = handler;
        this.enabled = Arc.getInstance().getCheckManager().isCheckEnabled(check);
    }

    public NukkitPacketListener(CheckType check) {
        this.enabled = Arc.getInstance().getCheckManager().isCheckEnabled(check);
    }

    /**
     * Invoked when a packet is receiving.
     *
     * @param event the event
     */
    public void onPacketReceiving(DataPacketReceiveEvent event) {
        if (enabled) onPacketReceiving0(event);
    }

    protected abstract void onPacketReceiving0(DataPacketReceiveEvent event);

}
