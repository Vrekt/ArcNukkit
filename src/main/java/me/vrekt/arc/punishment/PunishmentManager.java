package me.vrekt.arc.punishment;

import cn.nukkit.Player;
import cn.nukkit.Server;
import me.vrekt.arc.Arc;
import me.vrekt.arc.check.Check;
import me.vrekt.arc.configuration.ArcConfiguration;
import me.vrekt.arc.configuration.Configurable;
import me.vrekt.arc.configuration.ban.BanConfiguration;
import me.vrekt.arc.configuration.kick.KickConfiguration;
import me.vrekt.arc.configuration.types.BanLengthType;
import me.vrekt.arc.configuration.types.BanListType;
import me.vrekt.arc.configuration.types.ConfigurationString;
import me.vrekt.arc.configuration.types.Placeholders;
import me.vrekt.arc.permissions.Permissions;
import org.apache.commons.lang3.time.DateUtils;

import java.io.Closeable;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages kicking and banning of players.
 */
public final class PunishmentManager extends Configurable implements Closeable {

    /**
     * A set of players who have pending bans.
     */
    private final Set<Player> pendingPlayerBans = ConcurrentHashMap.newKeySet();

    /**
     * A set of player who have pending kicks
     */
    private final Set<Player> pendingPlayerKicks = ConcurrentHashMap.newKeySet();

    /**
     * The ban configuration
     */
    private BanConfiguration banConfiguration;

    /**
     * The kick configuration
     */
    private KickConfiguration kickConfiguration;

    /**
     * Event related
     */
    private boolean enableEventApi;

    /**
     * Initialize
     *
     * @param configuration the arc config
     */
    public void initialize(ArcConfiguration configuration) {
        read(configuration);
    }

    @Override
    public void reload(ArcConfiguration configuration) {
        read(configuration);
    }

    /**
     * Read
     *
     * @param configuration the arc config
     */
    private void read(ArcConfiguration configuration) {
        this.banConfiguration = configuration.banConfiguration();
        this.kickConfiguration = configuration.kickConfiguration();
        enableEventApi = Arc.arc().configuration().enableEventApi();
    }

    /**
     * Check if the player has a pending ban
     *
     * @param player the player
     * @return {@code true} if so
     */
    public boolean hasPendingBan(Player player) {
        return pendingPlayerBans.contains(player);
    }

    /**
     * Check if a player name has a pending ban
     *
     * @param name the name
     * @return {@code true} if so
     */
    public boolean hasPendingBan(String name) {
        return pendingPlayerBans.stream().anyMatch(player -> player.getName().equals(name));
    }

    /**
     * Check if the player has a pending kick
     *
     * @param player the player
     * @return {@code true} if so
     */
    public boolean hasPendingKick(Player player) {
        return pendingPlayerKicks.contains(player);
    }

    /**
     * Cancel a ban
     *
     * @param name the player name
     */
    public void cancelBan(String name) {
        pendingPlayerBans.removeIf(player -> player.getName().equals(name));
    }

    /**
     * Ban a player
     *
     * @param player the player
     * @param check  the check
     */
    public void banPlayer(Player player, Check check) {
        pendingPlayerBans.add(player);

        // get the date needed to ban the player.
        final int length = banConfiguration.globalBanLength();
        final BanLengthType lengthType = banConfiguration.globalBanLengthType();
        final Date date = lengthType == BanLengthType.DAYS ? DateUtils.addDays(new Date(), length) : DateUtils.addYears(new Date(), length);

        // build the message to send to violation viewers, then send it.
        final String violation = banConfiguration.globalViolationsBanMessage()
                .player(player)
                .check(check, null)
                .prefix()
                .time(banConfiguration.globalBanDelay())
                .value();
        Server.getInstance().broadcast(violation, Permissions.ARC_VIOLATIONS);
        Server.getInstance().getScheduler().scheduleDelayedTask(Arc.plugin(), () -> ban(player, check, date, length), banConfiguration.globalBanDelay() * 20);
    }

    /**
     * Ban the player
     *
     * @param player the player
     * @param check  the check
     * @param date   the ban date
     * @param time   the ban time, days, years, etc
     */
    private void ban(Player player, Check check, Date date, int time) {
        if (!hasPendingBan(player.getName())) return;
        final BanListType type = banConfiguration.globalBanType();
        if (type == BanListType.IP && player.getAddress() == null) {
            Arc.arc().getLogger().warning("Failed to ban player " + player.getName());
            pendingPlayerBans.remove(player);
            return;
        }

        final String playerBan = type == BanListType.IP ? player.getAddress() : player.getName();
        final String message = banConfiguration.globalBanMessage()
                .check(check, null)
                .value();

        // ban the player and kick them
        if (type == BanListType.IP) {
            Server.getInstance().getIPBans().addBan(playerBan, message, date);
        } else {
            Server.getInstance().getNameBans().addBan(playerBan, message, date);
        }

        player.kick(message);
        pendingPlayerBans.remove(player);

        // broadcast the ban
        if (banConfiguration.globalBroadcastBan()) {
            final boolean hasTime = date != null;
            ConfigurationString configMessage = banConfiguration.globalBroadcastBanMessage()
                    .player(player)
                    .check(check, null)
                    .prefix();

            // replace the time placeholder
            if (hasTime) {
                configMessage.time(time);
            } else {
                configMessage.replace(Placeholders.TIME, "");
            }

            // broadcast
            final String broadcast = configMessage.type().value();
            Server.getInstance().broadcastMessage(broadcast);
        }

    }

    /**
     * Kick a player
     *
     * @param player the player
     * @param check  the check
     */
    public void kickPlayer(Player player, Check check) {
        pendingPlayerKicks.add(player);
        final String violationsMessage = kickConfiguration.globalViolationsKickMessage()
                .check(check, null)
                .player(player)
                .prefix()
                .time(kickConfiguration.globalKickDelay())
                .value();
        Server.getInstance().broadcast(violationsMessage, Permissions.ARC_VIOLATIONS);
        Server.getInstance().getScheduler().scheduleDelayedTask(Arc.plugin(), () -> {
            final String message = kickConfiguration.globalKickMessage()
                    .check(check, null)
                    .value();
            player.kick(message);
            pendingPlayerKicks.remove(player);
        }, kickConfiguration.globalKickDelay() * 20);
    }

    @Override
    public void close() {
        pendingPlayerBans.clear();
        pendingPlayerKicks.clear();
    }

}