package me.vrekt.arc.configuration.check;

import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import me.vrekt.arc.Arc;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.configuration.ArcConfiguration;
import me.vrekt.arc.configuration.Configurable;

import java.util.List;

/**
 * A check configuration format.
 */
public final class CheckConfiguration extends Configurable {

    /**
     * The check for this configuration.
     */
    private final CheckType check;

    /**
     * The section for this check
     */
    private ConfigSection section;

    /**
     * Boolean check values
     */
    private boolean enabled, cancel, notify, ban, kick;

    /**
     * Violation levels
     */
    private int cancelLevel, notifyLevel, banLevel, kickLevel;

    /**
     * Initialize this check configuration
     *
     * @param check   the check type
     * @param section the section
     */
    public CheckConfiguration(CheckType check, ConfigSection section) {
        this.check = check;
        this.section = section;
        read(null);
    }

    @Override
    public void read(Config configuration) {
        // retrieve booleans
        enabled = section.getBoolean("enabled");
        cancel = section.getBoolean("cancel");
        notify = section.getBoolean("notify");
        ban = section.getBoolean("ban");
        kick = section.getBoolean("kick");

        // retrieve levels
        cancelLevel = section.getInt("cancel-level");
        notifyLevel = section.getInt("notify-every");
        banLevel = section.getInt("ban-level");
        kickLevel = section.getInt("kick-level");
    }

    @Override
    public void reload(ArcConfiguration configuration) {
        this.section = Arc.arc().getConfig().getSection(check.getName());
        read(null);
    }

    /**
     * Add a configuration value
     *
     * @param valueName the value name
     * @param value     the value
     */
    public void addConfigurationValue(String valueName, Object value) {
        if (containsValue(valueName)) return;
        section.set(valueName, value);
    }

    /**
     * Get a long
     *
     * @param name the name
     * @return a long
     */
    public long getLong(String name) {
        return section.getLong(name);
    }

    /**
     * Get a double
     *
     * @param name the name
     * @return the double
     */
    public double getDouble(String name) {
        return section.getDouble(name);
    }

    /**
     * Get a boolean
     *
     * @param name the name
     * @return the boolean
     */
    public boolean getBoolean(String name) {
        return section.getBoolean(name);
    }

    /**
     * Get a int
     *
     * @param name the name
     * @return the int
     */
    public int getInt(String name) {
        return section.getInt(name);
    }

    /**
     * Get a list
     *
     * @param name the name
     * @return the list
     */
    public List<String> getList(String name) {
        return section.getStringList(name);
    }

    /**
     * @return if the check is enabled
     */
    public boolean enabled() {
        return enabled;
    }

    /**
     * @return if the check should cancel
     */
    public boolean cancel() {
        return cancel;
    }

    /**
     * @return if the check should notify
     */
    public boolean notifyViolation() {
        return notify;
    }

    /**
     * @return if the check should ban
     */
    public boolean ban() {
        return ban;
    }

    /**
     * @return if the check should kick.
     */
    public boolean kick() {
        return kick;
    }

    /**
     * If the check should be cancelled.
     *
     * @param violationLevel the players violation level
     * @return {@code true} if so
     */
    public boolean shouldCancel(int violationLevel) {
        return cancel() && violationLevel >= cancelLevel;
    }

    /**
     * Check if this check should notify right now
     *
     * @param violationLevel the players violation level
     * @return {@code true} if so
     */
    public boolean shouldNotify(int violationLevel) {
        return notifyViolation() && (notifyLevel == 1 || violationLevel % notifyLevel == 0);
    }

    /**
     * If the player should be banned.
     *
     * @param violationLevel the violation level
     * @return {@code true} if so
     */
    public boolean shouldBan(int violationLevel) {
        return ban() && violationLevel >= banLevel;
    }

    /**
     * If the player should be kicked
     *
     * @param violationLevel the violation level
     * @return {@code true} if so
     */
    public boolean shouldKick(int violationLevel) {
        return kick() && violationLevel >= kickLevel;
    }

    /**
     * Check if a value exists.
     *
     * @param valueName the value name
     * @return {@code true} if so.
     */
    private boolean containsValue(String valueName) {
        return section.containsKey(valueName);
    }

}
