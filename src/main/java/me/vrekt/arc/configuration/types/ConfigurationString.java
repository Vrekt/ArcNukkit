package me.vrekt.arc.configuration.types;

import cn.nukkit.Player;
import cn.nukkit.utils.TextFormat;
import me.vrekt.arc.Arc;
import me.vrekt.arc.check.Check;
import org.apache.commons.lang3.StringUtils;

/**
 * Represents a configuration string.
 * Allows easy replacement of placeholders.
 */
public final class ConfigurationString {

    /**
     * The value
     */
    private String value;

    /**
     * Initialize
     *
     * @param value the configuration value
     */
    public ConfigurationString(String value) {
        this.value = value;
    }

    /**
     * Initialize
     *
     * @param other the other
     */
    public ConfigurationString(ConfigurationString other) {
        this.value = other.value;
    }

    /**
     * Replace player value
     *
     * @param player the player
     * @return this
     */
    public ConfigurationString player(Player player) {
        value = StringUtils.replace(value, Placeholders.PLAYER.placeholder(), player.getName());
        return this;
    }

    /**
     * Replace check value
     *
     * @param check   the check
     * @param subType the sub-type
     * @return this
     */
    public ConfigurationString check(Check check, String subType) {
        if (subType != null) {
            value = StringUtils.replace(value, Placeholders.CHECK.placeholder(), check.getName() + TextFormat.GRAY + " " + subType + " ");
        } else {
            value = StringUtils.replace(value, Placeholders.CHECK.placeholder(), check.getName());
        }
        return this;
    }

    /**
     * Replace level value
     *
     * @param level the level
     * @return this
     */
    public ConfigurationString level(int level) {
        value = StringUtils.replace(value, Placeholders.LEVEL.placeholder(), level + "");
        return this;
    }

    /**
     * Replace prefix
     *
     * @return this
     */
    public ConfigurationString prefix() {
        value = StringUtils.replace(value, Placeholders.PREFIX.placeholder(), Arc.getInstance().getArcConfiguration().getPrefix());
        return this;
    }

    /**
     * Replace time
     *
     * @param time the time
     * @return this
     */
    public ConfigurationString time(int time) {
        value = StringUtils.replace(value, Placeholders.TIME.placeholder(), time + "");
        return this;
    }

    /**
     * Replace ban type
     *
     * @return this
     */
    public ConfigurationString type() {
        value = StringUtils.replace(value, Placeholders.TYPE.placeholder(), Arc.getInstance()
                .getArcConfiguration()
                .banConfiguration()
                .globalBanLengthType()
                .prettyName());
        return this;
    }

    /**
     * Replace a placeholder
     *
     * @param placeholder the placeholder
     * @param value       the value
     * @return this
     */
    public ConfigurationString replace(Placeholders placeholder, String value) {
        this.value = StringUtils.replace(this.value, placeholder.placeholder(), value);
        return this;
    }

    /**
     * @return the value
     */
    public String value() {
        return value;
    }

}
