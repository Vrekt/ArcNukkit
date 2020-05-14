package arc.violation;

import arc.Arc;
import arc.check.Check;
import arc.check.permission.Permissions;
import arc.utility.ChatColor;
import cn.nukkit.Player;
import cn.nukkit.scheduler.PluginTask;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Handles player violations
 */
public final class PlayerViolationManager extends PluginTask<Arc> {

    /**
     * Violation data
     */
    private final ConcurrentMap<UUID, ViolationData> violationData = new ConcurrentHashMap<>();

    /**
     * Pending players waiting for their violation data to be removed.
     */
    private final ConcurrentMap<UUID, Long> pendingViolationRemovals = new ConcurrentHashMap<>();

    /**
     * A set of players who can be notified of violations.
     */
    private final Set<Player> broadcastablePlayers = new HashSet<>();

    public PlayerViolationManager(Arc owner) {
        super(owner);
    }

    /**
     * Get the players violation data.
     *
     * @param player the player
     * @return the players ViolationData.
     */
    public ViolationData getViolationData(Player player) {
        return violationData.get(player.getUniqueId());
    }

    /**
     * Put new violation data.
     *
     * @param player the player.
     */
    private void putViolationData(Player player) {
        violationData.put(player.getUniqueId(), new ViolationData());
    }

    /**
     * Removes the players violation data.
     *
     * @param player the player.
     */
    private void removeViolationData(Player player) {
        final var data = violationData.get(player.getUniqueId());
        if (!data.hasAnyViolations()) {
            // since we have no violations remove their history straight away.
            violationData.remove(player.getUniqueId());
            return;
        }
        pendingViolationRemovals.put(player.getUniqueId(), System.currentTimeMillis());
    }

    /**
     * When a player connects add their profile.
     *
     * @param player the player
     */
    public void onPlayerConnect(Player player) {
        if (!violationData.containsKey(player.getUniqueId())) {
            putViolationData(player);
        }

        if (player.hasPermission(Permissions.PERMISSION_NOTIFY)) {
            broadcastablePlayers.add(player);
        }
    }

    /**
     * Remove their profile when they disconnect.
     *
     * @param player the player
     */
    public void onPlayerDisconnect(Player player) {
        removeViolationData(player);
        broadcastablePlayers.remove(player);
    }

    /**
     * Handle a new violation
     *
     * @param player      the player
     * @param check       the check
     * @param information the debug information
     */
    public void handlePlayerViolation(Player player, Check check, String information) {
        final var type = check.check();

        // increment our violation level.
        final var data = getViolationData(player);
        final var level = data.getViolationLevel(type) + 1;
        data.incrementViolationLevel(type);

        // Kick or ban the player if needed.
        // TODO: Ban time
        // TODO: Ban message
        // TODO: Better configuration later in regards to violation stuff
        final var ban = check.ban();
        final var kick = check.kick();

        if (kick && level == check.kickLevel()) {
            player.kick(check.kickMessage());
            // broadcast the kick
            broadcastablePlayers.forEach(notifier -> notifier.sendMessage(playerKickedInformation(player.getName(), type.checkName(), level)));
            return;
        }

        if (ban && level == check.banLevel()) {
            Arc.plugin().getServer().getNameBans().addBan(player.getName(), "Arc");
            player.kick(check.kickMessage());
            // broadcast the ban
            broadcastablePlayers.forEach(notifier -> notifier.sendMessage(playerBanInformation(player.getName(), type.checkName(), level)));
            return;
        }

        broadcastablePlayers.forEach(notifier -> notifier.sendMessage(violationInformation(player.getName(), type.checkName(), level, information)));
    }

    /**
     * Return the violation information string.
     *
     * @param playerName     the player name
     * @param checkName      the check name
     * @param violationLevel the VL level
     * @param information    the information
     * @return the string
     */
    private String violationInformation(String playerName, String checkName, int violationLevel, String information) {
        return ChatColor.DARK_GRAY + "[" + ChatColor.RED + "Arc" + ChatColor.DARK_GRAY + "] "
                + ChatColor.BLUE + playerName + ChatColor.WHITE + " has violated check " + ChatColor.RED
                + checkName + ChatColor.DARK_GRAY + " (" + ChatColor.RED + violationLevel + ChatColor.DARK_GRAY
                + ")" + ChatColor.GRAY + " [" + information + "]";
    }

    /**
     * Return the kicked information string
     *
     * @param playerName     the player name
     * @param checkName      the check name
     * @param violationLevel the VL level
     * @return the string
     */
    private String playerKickedInformation(String playerName, String checkName, int violationLevel) {
        return ChatColor.DARK_GRAY + "[" + ChatColor.RED + "Arc" + ChatColor.DARK_GRAY + "] "
                + ChatColor.BLUE + playerName + ChatColor.WHITE + " was kicked for " + ChatColor.RED
                + checkName + ChatColor.DARK_GRAY + " (" + ChatColor.RED + violationLevel + ChatColor.DARK_GRAY
                + ")";
    }

    /**
     * Return the ban information string
     *
     * @param playerName     the player name
     * @param checkName      the check name
     * @param violationLevel the VL level
     * @return the string
     */
    private String playerBanInformation(String playerName, String checkName, int violationLevel) {
        return ChatColor.DARK_GRAY + "[" + ChatColor.RED + "Arc" + ChatColor.DARK_GRAY + "] "
                + ChatColor.BLUE + playerName + ChatColor.WHITE + " was banned for " + ChatColor.RED
                + checkName + ChatColor.DARK_GRAY + " (" + ChatColor.RED + violationLevel + ChatColor.DARK_GRAY
                + ")";
    }

    /**
     * Manage pending violation removals.
     * Violation data is cleared after 60 seconds.
     * TODO: Configurable?
     *
     * @param currentTick tick
     */
    @Override
    public void onRun(int currentTick) {
        pendingViolationRemovals.entrySet().removeIf(entry -> {
            final var player = entry.getKey();
            final var time = entry.getValue();
            final var remove = (System.currentTimeMillis() - time) / 1000 >= 60;
            if (remove) violationData.remove(player);
            return remove;
        });
    }
}
