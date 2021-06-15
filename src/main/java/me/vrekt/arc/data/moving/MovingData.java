package me.vrekt.arc.data.moving;

import cn.nukkit.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A players moving data
 */
public final class MovingData {

    /**
     * The register
     */
    private static final Map<UUID, MovingData> REGISTER = new ConcurrentHashMap<>();

    /**
     * Get data
     *
     * @param player the player
     * @return the data
     */
    public static MovingData get(Player player) {
        return REGISTER.computeIfAbsent(player.getUniqueId(), uuid -> new MovingData());
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
     * Total amount of move player packets
     */
    private int movePlayerPackets;

    /**
     * If the move player packet should be cancelled.
     */
    private boolean cancelMovePlayerPacket;

    public int movePlayerPackets() {
        return movePlayerPackets;
    }

    public void movePlayerPackets(int movePlayerPackets) {
        this.movePlayerPackets = movePlayerPackets;
    }

    /**
     * Increment the total {@code movePlayerPackets} and return if we should cancel.
     *
     * @return {@code true} if to cancel.
     */
    public boolean incrementMovePlayerPacketsAndCheckCancel() {
        this.movePlayerPackets = movePlayerPackets + 1;
        return cancelMovePlayerPacket;
    }

    public void cancelMovePlayerPacket(boolean cancelMovePlayerPacket) {
        this.cancelMovePlayerPacket = cancelMovePlayerPacket;
    }
}
