package me.vrekt.arc.data.block;

import cn.nukkit.Player;
import me.vrekt.arc.data.Data;
import me.vrekt.arc.utility.math.MathUtil;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class BlockData implements Data {

    /**
     * The register
     */
    private static final Map<UUID, BlockData> REGISTER = new ConcurrentHashMap<>();

    /**
     * Get data
     *
     * @param player the player
     * @return the data
     */
    public static BlockData get(Player player) {
        return REGISTER.computeIfAbsent(player.getUniqueId(), uuid -> new BlockData());
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
     * Last break time in creative.
     */
    private long lastBreak;

    /**
     * Reset
     */
    private long totalBrokeReset;

    /**
     * Total amount broke in creative
     * The amount of times delta was violated.
     */
    private int totalBroke, lastBreakDeltaCount;

    public long getLastBreak() {
        return lastBreak;
    }

    public void setLastBreak(long lastBreak) {
        this.lastBreak = lastBreak;
    }

    public int getTotalBroke() {
        return totalBroke;
    }

    public void setTotalBroke(int totalBroke) {
        this.totalBroke = MathUtil.clampInt(totalBroke, 0, 1000);
    }

    public long getTotalBrokeReset() {
        return totalBrokeReset;
    }

    public void setTotalBrokeReset(long totalBrokeReset) {
        this.totalBrokeReset = totalBrokeReset;
    }

    public int getLastBreakDeltaCount() {
        return lastBreakDeltaCount;
    }

    public void setLastBreakDeltaCount(int lastBreakDeltaCount) {
        this.lastBreakDeltaCount = MathUtil.clampInt(lastBreakDeltaCount, 0, 1000);
    }
}
