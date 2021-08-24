package me.vrekt.arc.exemption;

import cn.nukkit.Player;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.configuration.ArcConfiguration;
import me.vrekt.arc.configuration.Configurable;
import me.vrekt.arc.exemption.type.ExemptionType;
import me.vrekt.arc.permissions.Permissions;

import java.io.Closeable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages player exemptions
 */
public final class ExemptionManager implements Configurable, Closeable {

    /**
     * ExemptionHistory by player
     */
    private final Map<UUID, ExemptionHistory> exemptions = new ConcurrentHashMap<>();

    /**
     * If OP'd players can bypass.
     */
    private boolean canOpBypass;

    @Override
    public void reloadConfiguration(ArcConfiguration configuration) {
        canOpBypass = configuration.canOpBypass();
    }

    /**
     * Invoked when a player joins
     *
     * @param player the player
     */
    public void onPlayerJoin(Player player) {
        exemptions.put(player.getUniqueId(), new ExemptionHistory());
        doJoinExemptions(player);
    }

    /**
     * Exempt players when joining.
     *
     * @param player player
     */
    private void doJoinExemptions(Player player) {
        // players will have an odd vertical upon joining, not sure why.
        addExemption(player, CheckType.FLIGHT, 1000);
        addExemption(player, CheckType.MORE_PACKETS, 1000);
    }

    /**
     * Invoked when a player leaves
     *
     * @param player the player
     */
    public void onPlayerLeave(Player player) {
        final ExemptionHistory exemptions = this.exemptions.get(player.getUniqueId());
        this.exemptions.remove(player.getUniqueId());
        if (exemptions != null) exemptions.clear();
    }

    /**
     * Check if a player is exempt
     *
     * @param player the player
     * @param check  the check
     * @return {@code true} if so
     */
    public boolean isPlayerExempt(Player player, CheckType check) {
        if (canOpBypass && player.isOp()) return true;

        final boolean exemptFromCheck = isPlayerExemptFromCheck(player, check);
        if (exemptFromCheck) return true;

        final boolean exemptFlying = isFlying(player) && isExemptWhenFlying(check);
        if (exemptFlying) return true;

        final ExemptionHistory exemptions = this.exemptions.getOrDefault(player.getUniqueId(), ExemptionHistory.EMPTY);
        return exemptions.isExempt(check);
    }

    /**
     * Check if a player is exempt from a check
     *
     * @param player the player
     * @param check  the check
     * @return {@code true} if so
     */
    public boolean isPlayerExemptFromCheck(Player player, CheckType check) {
        return Permissions.canBypassCheck(player, check);
    }

    /**
     * Check if a player is exempt from a certain type
     *
     * @param player the player
     * @param type   the check
     * @return {@code true} if so
     */
    public boolean isPlayerExempt(Player player, ExemptionType type) {
        if (player == null || !player.isOnline()) return true;
        return exemptions.get(player.getUniqueId()).isExempt(type);
    }

    /**
     * Add an exemption
     *
     * @param player   the player
     * @param check    the check
     * @param duration the duration
     */
    public void addExemption(Player player, CheckType check, long duration) {
        final ExemptionHistory exemptions = this.exemptions.get(player.getUniqueId());
        exemptions.addExemption(check, System.currentTimeMillis() + duration);
    }

    /**
     * Add an exemption permanently
     *
     * @param player the player
     * @param check  the check
     */
    public void addExemptionPermanently(Player player, CheckType... check) {
        final ExemptionHistory exemptions = this.exemptions.get(player.getUniqueId());
        for (CheckType checkType : check) {
            exemptions.addExemptionPermanently(checkType);
        }
    }

    /**
     * Add an exemption type
     *
     * @param player the player
     * @param type   the type
     */
    public void addExemption(Player player, ExemptionType type) {
        exemptions.get(player.getUniqueId()).addExemption(type);
    }

    /**
     * Add an exemption type
     *
     * @param player   the player
     * @param type     the type
     * @param duration the duration
     */
    public void addExemption(Player player, ExemptionType type, long duration) {
        exemptions.get(player.getUniqueId()).addExemption(type, System.currentTimeMillis() + duration);
    }

    /**
     * Remove an exemption type
     *
     * @param player the player
     * @param type   the type
     */
    public void removeExemption(Player player, ExemptionType type) {
        exemptions.get(player.getUniqueId()).removeExemption(type);
    }

    /**
     * Check if the player is flying
     *
     * @param player the player
     * @return {@code true}
     */
    private boolean isFlying(Player player) {
        return player.getGamemode() == 1
                || player.getGamemode() == 3;
    }

    /**
     * Check if the player from a check when flying.
     *
     * @param check the check
     * @return {@code true} if so
     */
    private boolean isExemptWhenFlying(CheckType check) {
        return check == CheckType.FLIGHT || check == CheckType.SPEED || check == CheckType.PHASE;
    }

    @Override
    public void close() {
        exemptions.values().forEach(ExemptionHistory::clear);
        exemptions.clear();
    }

}
