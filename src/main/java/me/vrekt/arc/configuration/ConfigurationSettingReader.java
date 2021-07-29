package me.vrekt.arc.configuration;

import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import me.vrekt.arc.configuration.types.BanLengthType;
import me.vrekt.arc.configuration.types.BanListType;
import me.vrekt.arc.configuration.values.ConfigurationSetting;

/**
 * Allows easy reading and setting missing values
 */
public abstract class ConfigurationSettingReader {

    /**
     * Get a string
     *
     * @param configuration the configuration
     * @param value         the value
     * @return the string
     */
    protected String getString(ConfigSection configuration, ConfigurationSetting value) {
        final String str = configuration.getString(value.valueName(), value.stringValue());
        if (!configuration.exists(value.valueName())) {
            configuration.set(value.valueName(), value.stringValue());
        }
        return str;
    }

    /**
     * Get an integer
     *
     * @param configuration the configuration
     * @param value         the value
     * @return the int
     */
    protected int getInteger(ConfigSection configuration, ConfigurationSetting value) {
        final int number = configuration.getInt(value.valueName(), value.intValue());
        if (!configuration.exists(value.valueName())) {
            configuration.set(value.valueName(), value.intValue());
        }
        return number;
    }

    /**
     * Get a boolean
     *
     * @param configuration the configuration
     * @param value         the value
     * @return the boolean
     */
    protected boolean getBoolean(ConfigSection configuration, ConfigurationSetting value) {
        final boolean b = configuration.getBoolean(value.valueName(), value.booleanValue());
        if (!configuration.exists(value.valueName())) {
            configuration.set(value.valueName(), value.booleanValue());
        }
        return b;
    }

    /**
     * Get a string
     *
     * @param configuration the configuration
     * @param value         the value
     * @return the string
     */
    protected String getString(Config configuration, ConfigurationSetting value) {
        final String str = configuration.getString(value.valueName(), value.stringValue());
        if (!configuration.exists(value.valueName())) {
            configuration.set(value.valueName(), value.stringValue());
        }
        return str;
    }

    /**
     * Get an integer
     *
     * @param configuration the configuration
     * @param value         the value
     * @return the int
     */
    protected int getInteger(Config configuration, ConfigurationSetting value) {
        final int number = configuration.getInt(value.valueName(), value.intValue());
        if (!configuration.exists(value.valueName())) {
            configuration.set(value.valueName(), value.intValue());
        }
        return number;
    }

    /**
     * Get a boolean
     *
     * @param configuration the configuration
     * @param value         the value
     * @return the boolean
     */
    protected boolean getBoolean(Config configuration, ConfigurationSetting value) {
        final boolean b = configuration.getBoolean(value.valueName(), value.booleanValue());
        if (!configuration.exists(value.valueName())) {
            configuration.set(value.valueName(), value.booleanValue());
        }
        return b;
    }

    /**
     * Get ban list type
     *
     * @param configuration the configuration
     * @param value         the value
     * @return the {@link me.vrekt.arc.configuration.types.BanListType}
     */
    protected BanListType getBanListType(Config configuration, ConfigurationSetting value) {
        final String raw = configuration.getString(value.valueName());
        if (raw == null || (!raw.equalsIgnoreCase("IP")
                && !raw.equalsIgnoreCase("NAME"))) {
            configuration.set(value.valueName(), value.banListTypeValue().name());
            return value.banListTypeValue();
        }

        return BanListType.valueOf(raw);
    }

    /**
     * Get ban length type
     *
     * @param configuration the configuration
     * @param value         the value
     * @return the {@link BanLengthType}
     */
    protected BanLengthType getBanLengthType(Config configuration, ConfigurationSetting value) {
        final String raw = configuration.getString(value.valueName());
        if (raw == null) {
            configuration.set(value.valueName(), value.banLengthTypeValue().name());
            return value.banLengthTypeValue();
        }
        return BanLengthType.parse(raw);
    }

}
