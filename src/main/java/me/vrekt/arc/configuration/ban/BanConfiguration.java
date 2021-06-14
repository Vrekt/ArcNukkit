package me.vrekt.arc.configuration.ban;

import cn.nukkit.permission.BanList;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import me.vrekt.arc.configuration.ArcConfiguration;
import me.vrekt.arc.configuration.Configurable;
import me.vrekt.arc.configuration.types.BanLengthType;
import me.vrekt.arc.configuration.types.BanListType;
import me.vrekt.arc.configuration.types.ConfigurationString;
import me.vrekt.arc.configuration.values.ConfigurationValues;

/**
 * The ban configuration
 */
public final class BanConfiguration extends Configurable {

    /**
     * The global ban message
     */
    private ConfigurationString globalBanMessage;

    /**
     * The ban delay
     */
    private int globalBanDelay;

    /**
     * The ban type
     */
    private BanListType globalBanType;

    /**
     * The length type
     */
    private BanLengthType globalBanLengthType;

    /**
     * The length
     */
    private int globalBanLength;

    /**
     * If the ban should be broadcasted
     */
    private boolean globalBroadcastBan;

    /**
     * The message to broadcast
     */
    private ConfigurationString globalBroadcastBanMessage;

    /**
     * The message to send to violations
     */
    private ConfigurationString globalViolationsBanMessage;

    @Override
    public void read(Config configuration) {
        globalBanMessage = new ConfigurationString(TextFormat.colorize('&', getString(configuration, ConfigurationValues.GLOBAL_BAN_MESSAGE)));
        globalBanDelay = getInteger(configuration, ConfigurationValues.GLOBAL_BAN_DELAY);
        globalBanType = getBanListType(configuration, ConfigurationValues.GLOBAL_BAN_TYPE);
        globalBanLengthType = getBanLengthType(configuration, ConfigurationValues.GLOBAL_BAN_LENGTH_TYPE);
        globalBanLength = getInteger(configuration, ConfigurationValues.GLOBAL_BAN_LENGTH);
        globalBroadcastBan = getBoolean(configuration, ConfigurationValues.GLOBAL_BROADCAST_BAN);
        globalBroadcastBanMessage = new ConfigurationString(TextFormat.colorize('&', getString(configuration, ConfigurationValues.GLOBAL_BROADCAST_BAN_MESSAGE)));
        globalViolationsBanMessage = new ConfigurationString(TextFormat.colorize('&', getString(configuration, ConfigurationValues.GLOBAL_VIOLATIONS_BAN_MESSAGE)));
    }

    @Override
    public void reload(ArcConfiguration configuration) {
        read(configuration.fileConfiguration());
    }

    /**
     * @return a new {@link ConfigurationString}
     */
    public ConfigurationString globalBanMessage() {
        return new ConfigurationString(globalBanMessage);
    }

    /**
     * @return the delay
     */
    public int globalBanDelay() {
        return globalBanDelay;
    }

    /**
     * @return the type
     */
    public BanListType globalBanType() {
        return globalBanType;
    }

    /**
     * @return the ban length type
     */
    public BanLengthType globalBanLengthType() {
        return globalBanLengthType;
    }

    /**
     * @return the length
     */
    public int globalBanLength() {
        return globalBanLength;
    }

    /**
     * @return if bans should be broadcasted
     */
    public boolean globalBroadcastBan() {
        return globalBroadcastBan;
    }

    /**
     * @return a new {@link ConfigurationString}
     */
    public ConfigurationString globalBroadcastBanMessage() {
        return new ConfigurationString(globalBroadcastBanMessage);
    }

    /**
     * @return a new {@link ConfigurationString}
     */
    public ConfigurationString globalViolationsBanMessage() {
        return new ConfigurationString(globalViolationsBanMessage);
    }
}
