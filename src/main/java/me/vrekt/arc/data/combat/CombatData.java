package me.vrekt.arc.data.combat;

import cn.nukkit.Player;
import me.vrekt.arc.data.Data;
import me.vrekt.arc.utility.math.MathUtil;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Combat data1
 */
public final class CombatData implements Data {

    /**
     * The register
     */
    private static final Map<UUID, CombatData> REGISTER = new ConcurrentHashMap<>();

    /**
     * Get data
     *
     * @param player the player
     * @return the data
     */
    public static CombatData get(Player player) {
        return REGISTER.computeIfAbsent(player.getUniqueId(), uuid -> new CombatData());
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
     * If the next attack should be cancelled.
     * If the player had an entity.
     * If first cancel should be ignored
     */
    private boolean cancelNextAttack, ignoreFirst;

    /**
     * The last time the player swung their arm
     * Last time swing data was reset.
     * Last block interact time
     * Last attack time
     */
    private long lastSwingTime, lastSwingReset, lastBlockInteract, lastAttack;

    /**
     * Total arm swings
     * Total block interacts
     * Batching threshold for ignoring batched packets
     */
    private int totalArmSwings, totalBlockInteracts, batchingThreshold;

    public long getLastSwingTime() {
        return lastSwingTime;
    }

    public void setLastSwingTime(long lastSwingTime) {
        this.lastSwingTime = lastSwingTime;
    }

    public int getTotalArmSwings() {
        return totalArmSwings;
    }

    public void setTotalArmSwings(int totalArmSwings) {
        this.totalArmSwings = MathUtil.clampInt(totalArmSwings, 0, 1000);
    }

    public boolean cancelNextAttack() {
        return cancelNextAttack;
    }

    public void setCancelNextAttack(boolean cancelNextAttack) {
        this.cancelNextAttack = cancelNextAttack;
    }

    public long getLastSwingReset() {
        return lastSwingReset;
    }

    public void setLastSwingReset(long lastSwingReset) {
        this.lastSwingReset = lastSwingReset;
    }

    public long getLastBlockInteract() {
        return lastBlockInteract;
    }

    public void setLastBlockInteract(long lastBlockInteract) {
        this.lastBlockInteract = lastBlockInteract;
    }

    public int getTotalBlockInteracts() {
        return totalBlockInteracts;
    }

    public void setTotalBlockInteracts(int totalBlockInteracts) {
        this.totalBlockInteracts = MathUtil.clampInt(totalBlockInteracts, 0, 1000);
    }

    public int getBatchingThreshold() {
        return batchingThreshold;
    }

    public void setBatchingThreshold(int batchingThreshold) {
        this.batchingThreshold = MathUtil.clampInt(batchingThreshold, 0, 1000);
    }

    public long getLastAttack() {
        return lastAttack;
    }

    public void setLastAttack(long lastAttack) {
        this.lastAttack = lastAttack;
    }

    public boolean ignoreFirst() {
        return ignoreFirst;
    }

    public void setIgnoreFirst(boolean ignoreFirst) {
        this.ignoreFirst = ignoreFirst;
    }
}
