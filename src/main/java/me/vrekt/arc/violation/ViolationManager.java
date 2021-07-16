package me.vrekt.arc.violation;

import cn.nukkit.Player;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import me.vrekt.arc.Arc;
import me.vrekt.arc.check.Check;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.check.result.CheckResult;
import me.vrekt.arc.configuration.ArcConfiguration;
import me.vrekt.arc.configuration.Configurable;
import me.vrekt.arc.permissions.Permissions;
import me.vrekt.arc.punishment.PunishmentManager;
import me.vrekt.arc.violation.result.ViolationResult;

import java.io.Closeable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * Manages violations
 */
public final class ViolationManager extends Configurable implements Closeable {

    /**
     * Violation history
     */
    private final Map<UUID, Violations> history = new ConcurrentHashMap<>();

    /**
     * A list of players who can view violations/debug information
     */
    private final ConcurrentMap<Player, Boolean> violationViewers = new ConcurrentHashMap<>();

    /**
     * Keeps track of when to expire history
     */
    private Cache<UUID, Violations> historyCache;

    /**
     * The arc configuration.
     */
    private ArcConfiguration configuration;

    /**
     * The punishment manager
     */
    private PunishmentManager punishmentManager;

    /**
     * Initialize
     *
     * @param configuration the configuration
     */
    public void initialize(ArcConfiguration configuration) {
        this.configuration = configuration;
        this.punishmentManager = Arc.getInstance().getPunishmentManager();

        historyCache = CacheBuilder.newBuilder()
                .expireAfterWrite(configuration.violationDataTimeout(), TimeUnit.MINUTES)
                .build();
    }

    /**
     * Invoked when a player joins
     *
     * @param player the player
     */
    public void onPlayerJoin(Player player) {
        final Violations cached = historyCache.getIfPresent(player.getUniqueId());
        if (cached != null) {
            historyCache.invalidate(player.getUniqueId());
            history.put(player.getUniqueId(), cached);
        } else {
            history.put(player.getUniqueId(), new Violations());
        }
        if (Permissions.canViewViolations(player)) violationViewers.put(player, false);
    }

    /**
     * Invoked when a player leaves
     *
     * @param player the player
     */
    public void onPlayerLeave(Player player) {
        historyCache.put(player.getUniqueId(), history.get(player.getUniqueId()));
        history.remove(player.getUniqueId());
        violationViewers.remove(player);
    }

    /**
     * Process a violation
     *
     * @param player the player
     * @param result the result
     * @return the result
     */
    public ViolationResult violation(Player player, Check check, CheckResult result) {
        final Violations violations = history.get(player.getUniqueId());
        final int level = violations.incrementViolationLevel(check.type());
        // create a new result if we haven't cancelled.
        final ViolationResult violationResult = new ViolationResult();

        // handle violation
        if (check.configuration().shouldNotify(level)) {
            // add that we are going to notify.
            violationResult.addResult(ViolationResult.Result.NOTIFY);
            // replace the place holders within the message

            final String violationMessage = configuration.violationNotifyMessage()
                    .player(player)
                    .check(check, null)
                    .level(level)
                    .prefix()
                    .value();

            final String violationMessageWithDebug = violationMessage + "\n(" + result.information() + ")";

            // build the text component and then send to all viewers.
            // TODO: Unfortunately no hover event
            violationViewers.forEach((viewer, isDebug) -> viewer.sendMessage(isDebug ? violationMessageWithDebug : violationMessage));
        }

        // add a cancel result if this check should cancel.
        if (check.configuration().shouldCancel(level)) violationResult.addResult(ViolationResult.Result.CANCEL);

        // ban the player if this check should ban
        if (check.configuration().shouldBan(level) && !punishmentManager.hasPendingBan(player)) {
            violationResult.addResult(ViolationResult.Result.BAN);
            punishmentManager.banPlayer(player, check);
        }

        // kick the player if this check should kick.
        if (check.configuration().shouldKick(level) && !punishmentManager.hasPendingKick(player)) {
            violationResult.addResult(ViolationResult.Result.KICK);
            punishmentManager.kickPlayer(player, check);
        }

        return violationResult;
    }

    /**
     * Check if the player can view violations
     *
     * @param player the player
     * @return {@code true} if so
     */
    public boolean isViolationViewer(Player player) {
        return violationViewers.containsKey(player);
    }

    /**
     * Toggle violations viewer
     *
     * @param player the player
     * @return {@code true} if the player is now a viewer.
     */
    public boolean toggleViolationsViewer(Player player) {
        if (isViolationViewer(player)) {
            violationViewers.remove(player);
            return false;
        } else {
            violationViewers.put(player, false);
            return true;
        }
    }

    /**
     * Toggle debug viewer
     *
     * @param player the player
     * @return {@code true} if the player is now a debug viewer.
     */
    public boolean toggleDebugViewer(Player player) {
        if (violationViewers.containsKey(player)) {
            final boolean state = violationViewers.get(player);
            violationViewers.put(player, !state);
            return !state;
        }
        return false;
    }

    /**
     * Get the violation level
     *
     * @param player the player
     * @param check  the check
     * @return the level
     */
    public int getViolationLevel(Player player, CheckType check) {
        return history.get(player.getUniqueId()).getViolationLevel(check);
    }

    @Override
    public void reload(ArcConfiguration configuration) {
        this.configuration = configuration;

        historyCache.invalidateAll();
        historyCache = CacheBuilder.newBuilder()
                .expireAfterWrite(configuration.violationDataTimeout(), TimeUnit.MINUTES)
                .build();
    }

    @Override
    public void close() {
        history.clear();
        violationViewers.clear();
        historyCache.invalidateAll();
    }
}