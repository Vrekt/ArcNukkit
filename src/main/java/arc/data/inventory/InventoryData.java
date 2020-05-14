package arc.data.inventory;

import cn.nukkit.Player;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Holds inventory data about a player.
 */
public final class InventoryData {

    /**
     * Holds player data.
     */
    private static final ConcurrentMap<UUID, InventoryData> PLAYER_CACHE = new ConcurrentHashMap<>();

    /**
     * Get the data for this player
     *
     * @param player the player
     * @return their {@link InventoryData}
     */
    public static InventoryData getData(Player player) {
        return PLAYER_CACHE.get(player.getUniqueId());
    }

    /**
     * Put data for this player.
     *
     * @param player the player
     */
    public static void putData(Player player) {
        PLAYER_CACHE.put(player.getUniqueId(), new InventoryData());
    }

    /**
     * Remove the players data from the cache.
     *
     * @param player the player
     */
    public static void removeData(Player player) {
        PLAYER_CACHE.remove(player.getUniqueId());
    }

    /**
     * If we are consuming an item or not.
     */
    private boolean isConsuming;

    /**
     * When we started consuming
     */
    private long startConsumeTime;

    /**
     * The amount of eating packets.
     */
    private int eatingPackets;

    public boolean isConsuming() {
        return isConsuming;
    }

    public void isConsuming(boolean consuming) {
        isConsuming = consuming;
    }

    public long startConsumeTime() {
        return startConsumeTime;
    }

    public void startConsumeTime(long startConsumeTime) {
        this.startConsumeTime = startConsumeTime;
    }

    public int eatingPackets() {
        return eatingPackets;
    }

    public void eatingPackets(int eatingPackets) {
        this.eatingPackets = eatingPackets;
    }
}
