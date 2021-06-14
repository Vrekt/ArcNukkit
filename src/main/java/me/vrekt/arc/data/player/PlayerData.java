package me.vrekt.arc.data.player;


import cn.nukkit.Player;
import me.vrekt.arc.data.Data;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stores player related check data
 */
public final class PlayerData implements Data {

    /**
     * The register
     */
    private static final Map<UUID, PlayerData> REGISTER = new ConcurrentHashMap<>();

    /**
     * Get data
     *
     * @param player the player
     * @return the data
     */
    public static PlayerData get(Player player) {
        return REGISTER.computeIfAbsent(player.getUniqueId(), uuid -> new PlayerData());
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
     * When the player last regained health.
     */
    private long lastHealthRegain;

    /**
     * The last time the bow was shot.
     */
    private long lastBowUse;

    /**
     * Last bow shoot
     */
    private long lastBowShoot;

    /**
     * The consume start time
     */
    private long consumeStartTime;

    /**
     * If the player is consuming
     */
    private boolean isConsuming;

    public long lastHealthRegain() {
        return lastHealthRegain;
    }

    public void lastHealthRegain(long lastHealthRegain) {
        this.lastHealthRegain = lastHealthRegain;
    }

    public long lastBowUse() {
        return lastBowUse;
    }

    public void lastBowUse(long lastBowUse) {
        this.lastBowUse = lastBowUse;
    }

    public long lastBowShoot() {
        return lastBowShoot;
    }

    public void lastBowShoot(long lastBowShoot) {
        this.lastBowShoot = lastBowShoot;
    }

    public long consumeStartTime() {
        return consumeStartTime;
    }

    public void consumeStartTime(long consumeStartTime) {
        this.consumeStartTime = consumeStartTime;
    }

    public boolean isConsuming() {
        return isConsuming;
    }

    public void isConsuming(boolean consuming) {
        isConsuming = consuming;
    }
}
