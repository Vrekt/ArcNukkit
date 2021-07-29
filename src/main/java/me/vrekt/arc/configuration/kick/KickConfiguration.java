package me.vrekt.arc.configuration.kick;

import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import me.vrekt.arc.configuration.ArcConfiguration;
import me.vrekt.arc.configuration.Configurable;
import me.vrekt.arc.configuration.types.ConfigurationString;
import me.vrekt.arc.configuration.values.ConfigurationSetting;

/**
 * The kick configuration
 */
public final class KickConfiguration extends Configurable {

    /**
     * The global kick message.
     */
    private ConfigurationString globalKickMessage;

    /**
     * The global violations kick message
     */
    private ConfigurationString globalViolationsKickMessage;

    /**
     * The global kick delay.
     */
    private int globalKickDelay;

    @Override
    public void read(Config configuration) {
        this.globalKickMessage = new ConfigurationString(TextFormat.colorize('&', getString(configuration, ConfigurationSetting.GLOBAL_KICK_MESSAGE)));
        this.globalViolationsKickMessage = new ConfigurationString(TextFormat.colorize('&', getString(configuration, ConfigurationSetting.GLOBAL_VIOLATIONS_KICK_MESSAGE)));
        this.globalKickDelay = getInteger(configuration, ConfigurationSetting.GLOBAL_KICK_DELAY);
    }

    @Override
    public void reload(ArcConfiguration configuration) {
        read(configuration.fileConfiguration());
    }

    /**
     * @return the global kick message
     */
    public ConfigurationString globalKickMessage() {
        return globalKickMessage;
    }

    /**
     * @return The global violations kick message
     */
    public ConfigurationString globalViolationsKickMessage() {
        return globalViolationsKickMessage;
    }

    /**
     * @return the global kick delay
     */
    public int globalKickDelay() {
        return globalKickDelay;
    }
}
