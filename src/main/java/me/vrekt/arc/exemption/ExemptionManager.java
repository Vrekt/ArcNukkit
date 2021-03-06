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
public final class ExemptionManager extends Configurable implements Closeable {

    /**
     * Exemptions by player
     */
    private final Map<UUID, Exemptions> exemptions = new ConcurrentHashMap<>();

    /**
     * Initialize
     *
     * @param configuration the config
     */
    public void initialize(ArcConfiguration configuration) {
        //
    }

    @Override
    public void reload(ArcConfiguration configuration) {
        //
    }

    /**
     * Invoked when a player joins
     *
     * @param player the player
     */
    public void onPlayerJoin(Player player) {
        exemptions.put(player.getUniqueId(), new Exemptions());
        doJoinExemptions(player);
    }

    /**
     * Exempt players when joining.
     *
     * @param player player
     */
    private void doJoinExemptions(Player player) {
        //
    }

    /**
     * Invoked when a player leaves
     *
     * @param player the player
     */
    public void onPlayerLeave(Player player) {
        final Exemptions exemptions = this.exemptions.get(player.getUniqueId());
        this.exemptions.remove(player.getUniqueId());
        exemptions.clear();
    }

    /**
     * Check if a player is exempt
     *
     * @param player the player
     * @param check  the check
     * @return {@code true} if so
     */
    public boolean isPlayerExempt(Player player, CheckType check) {
        final boolean exemptFromCheck = isPlayerExemptFromCheck(player, check);
        final boolean exemptFlying = isFlying(player) && isExemptWhenFlying(check);
        boolean exemptionsMapped = false;

        final Exemptions exemptions = this.exemptions.get(player.getUniqueId());
        if (exemptions != null) {
            exemptionsMapped = exemptions.isExempt(check);
        }

        return (exemptFromCheck || exemptFlying || exemptionsMapped);
    }

    /**
     * Check if a player is exempt from a check
     *
     * @param player the player
     * @param check  the check
     * @return {@code true} if so
     */
    public boolean isPlayerExemptFromCheck(Player player, CheckType check) {
        return Permissions.canBypassChecks(player, check);
    }

    /**
     * Check if a player is exempt from a certain type
     *
     * @param player the player
     * @param type   the check
     * @return {@code true} if so
     */
    public boolean isPlayerExempt(Player player, ExemptionType type) {
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
        final Exemptions exemptions = this.exemptions.get(player.getUniqueId());
        exemptions.addExemption(check, System.currentTimeMillis() + duration);
    }

    /**
     * Add an exemption permanently
     *
     * @param player the player
     * @param check  the check
     */
    public void addExemptionPermanently(Player player, CheckType... check) {
        final Exemptions exemptions = this.exemptions.get(player.getUniqueId());
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
        return check == CheckType.FLIGHT;
    }

    @Override
    public void close() {
        exemptions.values().forEach(Exemptions::clear);
        exemptions.clear();
    }

}
