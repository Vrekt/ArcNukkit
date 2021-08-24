package me.vrekt.arc.check;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.level.Location;
import cn.nukkit.scheduler.TaskHandler;
import me.vrekt.arc.Arc;
import me.vrekt.arc.check.result.CheckResult;
import me.vrekt.arc.configuration.ArcConfiguration;
import me.vrekt.arc.configuration.Configurable;
import me.vrekt.arc.configuration.check.CheckConfiguration;
import me.vrekt.arc.configuration.check.CheckConfigurationBuilder;
import me.vrekt.arc.exemption.ExemptionManager;
import me.vrekt.arc.exemption.type.ExemptionType;
import me.vrekt.arc.timings.CheckTimings;
import me.vrekt.arc.violation.ViolationManager;
import me.vrekt.arc.violation.result.ViolationResult;

/**
 * Represents a check.
 */
public abstract class Check implements Configurable {

    /**
     * ExemptionHistory
     */
    private static final ExemptionManager EXEMPTION_MANAGER = Arc.getInstance().getExemptionManager();

    /**
     * ViolationHistory
     */
    private static final ViolationManager VIOLATION_MANAGER = Arc.getInstance().getViolationManager();

    /**
     * The check type
     */
    protected final CheckType checkType;

    /**
     * The configuration builder;
     */
    protected final CheckConfigurationBuilder builder;

    /**
     * The check configuration
     */
    protected CheckConfiguration configuration;

    /**
     * If this check is permanently disabled.
     */
    protected boolean permanentlyDisabled;

    /**
     * The scheduled task.
     */
    protected TaskHandler task;

    /**
     * Initialize the check
     *
     * @param checkType the type
     */
    protected Check(CheckType checkType) {
        this.checkType = checkType;
        this.builder = new CheckConfigurationBuilder(checkType);

        Arc.getInstance().getConfigurationDocumentationWriter().addConfigurationSection(checkType);
    }

    /**
     * Set if this check is enabled.
     *
     * @param enabled enabled
     * @return this
     */
    public Check enabled(boolean enabled) {
        builder.enabled(enabled);
        return this;
    }

    /**
     * Set if this check should cancel
     *
     * @param cancel cancel
     * @return this
     */
    public Check cancel(boolean cancel) {
        builder.cancel(cancel);
        return this;
    }

    /**
     * Set the cancel level
     *
     * @param level level
     * @return this
     */
    public Check cancelLevel(int level) {
        builder.cancelLevel(level);
        return this;
    }

    /**
     * Set if this check should notify
     *
     * @param notify notify
     * @return this
     */
    public Check notify(boolean notify) {
        builder.notify(notify);
        return this;
    }

    /**
     * Set the notify level
     *
     * @param level level
     * @return this
     */
    public Check notifyEvery(int level) {
        builder.notifyEvery(level);
        return this;
    }

    /**
     * Set if this check should ban
     *
     * @param ban ban
     * @return this
     */
    public Check ban(boolean ban) {
        builder.ban(ban);
        if (!ban) builder.banLevel(0);
        return this;
    }

    /**
     * Set the ban level
     *
     * @param level level
     * @return this
     */
    public Check banLevel(int level) {
        builder.banLevel(level);
        return this;
    }

    /**
     * Set if this check should kick
     *
     * @param kick kick
     * @return this
     */
    public Check kick(boolean kick) {
        builder.kick(kick);
        if (!kick) builder.kickLevel(0);
        return this;
    }

    /**
     * Set the kick level
     *
     * @param level level
     * @return this
     */
    public Check kickLevel(int level) {
        builder.kickLevel(level);
        return this;
    }

    /**
     * Build the configuration
     */
    public void build() {
        configuration = builder.build();
    }

    /**
     * Add a configuration value
     *
     * @param valueName the value name
     * @param value     the value
     */
    protected void addConfigurationValue(String valueName, Object value) {
        configuration.addConfigurationValue(valueName, value);
    }

    /**
     * Add a configuration value, with a comment.
     *
     * @param valueName the valueName
     * @param value     the value
     * @param comment   the comment
     */
    protected void addConfigurationValueWithComment(String valueName, Object value, String comment) {
        configuration.addConfigurationValueWithComment(valueName, value, comment);
    }

    /**
     * Process the check result.
     *
     * @param result the result
     */
    protected ViolationResult checkViolationWithResult(Player player, CheckResult result) {
        if (result.failed()) return VIOLATION_MANAGER.violation(player, this, result);
        return ViolationResult.EMPTY;
    }

    /**
     * Process the provided {@code result}
     *
     * @param player the player
     * @param result the result
     * @return {@code true} if the check should cancel.
     */
    protected boolean checkViolation(Player player, CheckResult result) {
        return checkViolationWithResult(player, result).cancel();
    }

    /**
     * Handle a check violation and reset the result after.
     *
     * @param player   the player
     * @param result   the result
     * @param cancelTo the cancel To
     * @return the result
     */
    protected boolean handleCheckViolationAndReset(Player player, CheckResult result, Location cancelTo) {
        if (result.failed()) {
            final ViolationResult vr = VIOLATION_MANAGER.violation(player, this, result);
            if (vr.cancel() && cancelTo != null) {
                player.teleport(cancelTo, PlayerTeleportEvent.TeleportCause.PLUGIN);
            }
            result.reset();
            return vr.cancel();
        }

        result.reset();
        return false;
    }

    /**
     * Handle a check violation.
     *
     * @param player   the player
     * @param result   the result
     * @param cancelTo the cancel To
     * @return the result
     */
    protected boolean handleCheckViolation(Player player, CheckResult result, Location cancelTo) {
        if (result.failed()) {
            final ViolationResult vr = VIOLATION_MANAGER.violation(player, this, result);
            if (vr.cancel() && cancelTo != null) {
                player.teleport(cancelTo, PlayerTeleportEvent.TeleportCause.PLUGIN);
            }
            return vr.cancel();
        }

        return false;
    }

    /**
     * Get the last violation time
     *
     * @param player the player
     * @return the time
     */
    protected long getLastViolation(Player player) {
        return VIOLATION_MANAGER.getLastViolation(player, checkType);
    }

    /**
     * Check if the player is exempt
     *
     * @param player the player
     * @return {@code true} if so
     */
    protected boolean exempt(Player player) {
        return EXEMPTION_MANAGER.isPlayerExempt(player, checkType);
    }

    /**
     * Check if a player is exempt
     *
     * @param player the player
     * @param type   the type
     * @return {@code true} if so
     */
    protected boolean exempt(Player player, ExemptionType type) {
        return EXEMPTION_MANAGER.isPlayerExempt(player, type);
    }

    /**
     * Schedule
     *
     * @param runnable the runnable
     * @param every    timer
     */
    protected void schedule(Runnable runnable, int every) {
        task = Server.getInstance().getScheduler().scheduleRepeatingTask(Arc.getPlugin(), runnable, every);
    }

    /**
     * Cancel the scheduled task.
     */
    protected void cancelScheduled() {
        if (task != null && !task.isCancelled()) {
            task.cancel();
            task = null;
        }
    }

    /**
     * Start timing the player
     *
     * @param player the player
     */
    protected void startTiming(Player player) {
        CheckTimings.startTiming(checkType, player.getUniqueId());
    }

    /**
     * Stop timing the player
     *
     * @param player the player
     */
    protected void stopTiming(Player player) {
        CheckTimings.stopTiming(checkType, player.getUniqueId());
    }

    @Override
    public void reloadConfiguration(ArcConfiguration configuration) {
        if (permanentlyDisabled) return;
        unload();

        this.configuration.reloadConfiguration(configuration);
        if (this.configuration.enabled()) {
            reloadConfig();
        }
    }

    /**
     * Reload the check implementation config.
     */
    public abstract void reloadConfig();

    /**
     * Load the check
     */
    public abstract void load();

    /**
     * Unload the check if needed.
     */
    public void unload() {
    }

    /**
     * @return {@code true} if the check is enabled.
     */
    public boolean enabled() {
        return !permanentlyDisabled && configuration.enabled();
    }

    /**
     * @return the check name.
     */
    public String getName() {
        return checkType.getName();
    }

    /**
     * @return the check type
     */
    public CheckType type() {
        return checkType;
    }

    /**
     * @return the check configuration
     */
    public CheckConfiguration configuration() {
        return configuration;
    }

}
