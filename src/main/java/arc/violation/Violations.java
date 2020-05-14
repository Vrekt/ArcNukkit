package arc.violation;

import arc.Arc;
import arc.check.Check;
import arc.check.permission.Permissions;
import arc.utility.ChatColor;
import cn.nukkit.Player;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Handles player violations
 */
public final class Violations {

    /**
     * Violation data
     */
    private static final ConcurrentMap<UUID, ViolationData> VIOLATIONS = new ConcurrentHashMap<>();

    /**
     * Get the players violation data.
     *
     * @param player the player
     * @return the players ViolationData.
     */
    public static ViolationData getViolationData(Player player) {
        return VIOLATIONS.get(player.getUniqueId());
    }

    /**
     * Put new violation data.
     *
     * @param player the player.
     */
    public static void putViolationData(Player player) {
        VIOLATIONS.put(player.getUniqueId(), new ViolationData());
    }

    /**
     * Removes the players violation data.
     *
     * @param player the player.
     */
    public static void removeViolationData(Player player) {
        VIOLATIONS.remove(player.getUniqueId());
    }

    /**
     * Handle a new violation
     *
     * @param player      the player
     * @param check       the check
     * @param information the debug information
     */
    public static void handleViolation(Player player, Check check, String information) {
        final var type = check.check();

        // increment our violation level.
        final var data = getViolationData(player);
        final var level = data.getViolationLevel(type) + 1;
        data.incrementViolationLevel(type);

        // check for kicking and banning.
        // TODO: Notify staff of kick/ban.
        // TODO: Ban time
        // TODO: Ban message
        // TODO: Keep violation data for awhile after leaving
        final var ban = check.ban();
        final var kick = check.kick();
        if (kick && level > check.kickLevel()) player.kick(check.kickMessage());
        if (ban && level > check.banLevel()) Arc.plugin().getServer().getNameBans().addBan(player.getName(), "Arc");
        Arc.plugin().getServer().broadcast(ChatColor.DARK_GRAY.color() + "[" + ChatColor.RED.color() + "Arc" + ChatColor.DARK_GRAY.color() + "] "
                + ChatColor.BLUE.color() + player.getName() + ChatColor.WHITE.color() + " has violated check " + ChatColor.RED.color()
                + type.checkName() + ChatColor.DARK_GRAY.color() + " (" + ChatColor.RED.color() + level + ChatColor.DARK_GRAY.color()
                + ")" + ChatColor.GRAY.color() + " [" + information + "]", Permissions.PERMISSION_NOTIFY);
    }

}
