package me.vrekt.arc.data.packet;


import cn.nukkit.Player;
import me.vrekt.arc.data.Data;
import me.vrekt.arc.utility.math.MathUtil;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles storing packet
 */
public final class PacketData implements Data {

    /**
     * The register
     */
    private static final Map<UUID, PacketData> REGISTER = new ConcurrentHashMap<>();

    /**
     * Get data
     *
     * @param player the player
     * @return the data
     */
    public static PacketData get(Player player) {
        return REGISTER.computeIfAbsent(player.getUniqueId(), uuid -> new PacketData());
    }

    /**
     * Remove data
     *
     * @param player the player
     */
    public static void remove(Player player) {
        REGISTER.remove(player.getUniqueId());
    }

    /**
     * Break packet count
     */
    private int breakPacketCount;

    /**
     * If block dig packets should be cancelled.
     */
    private boolean cancelBreakPackets;

    /**
     * Reset
     */
    private long lastBreakPacketReset;

    public int getBreakPacketCount() {
        return breakPacketCount;
    }

    public void setBreakPacketCount(int breakPacketCount) {
        this.breakPacketCount = MathUtil.clampInt(breakPacketCount, 0, 1000);
    }

    public boolean cancelBreakPackets() {
        return cancelBreakPackets;
    }

    public void setCancelBreakPackets(boolean cancelBreakPackets) {
        this.cancelBreakPackets = cancelBreakPackets;
    }

    public long getLastBreakPacketReset() {
        return lastBreakPacketReset;
    }

    public void setLastBreakPacketReset(long lastBreakPacketReset) {
        this.lastBreakPacketReset = lastBreakPacketReset;
    }
}
