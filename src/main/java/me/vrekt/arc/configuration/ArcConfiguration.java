package me.vrekt.arc.configuration;

import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import me.vrekt.arc.Arc;
import me.vrekt.arc.configuration.ban.BanConfiguration;
import me.vrekt.arc.configuration.kick.KickConfiguration;
import me.vrekt.arc.configuration.types.ConfigurationString;
import me.vrekt.arc.configuration.values.ConfigurationValues;

/**
 * The arc configuration
 */
public final class ArcConfiguration extends Configurable {

    /**
     * Handles ban configuration values
     */
    private final BanConfiguration banConfiguration = new BanConfiguration();
    /**
     * Handles kick configuration values
     */
    private final KickConfiguration kickConfiguration = new KickConfiguration();

    /**
     * If check timings should be enabled.
     * If the TPS helper should be enabled.
     * If the event API should be enabled.
     */
    private boolean enableCheckTimings, enableTpsHelper, enableEventApi;

    /**
     * TPS helper limit
     * The time after leaving violation data times out
     */
    private int tpsHelperLimit, violationDataTimeout;

    /**
     * Violation notify message
     */
    private ConfigurationString violationNotifyMessage;

    /**
     * Command no permission message
     */
    private String commandNoPermissionMessage;

    /**
     * Prefix
     */
    private String prefix;

    @Override
    public void read(Config configuration) {
        kickConfiguration.read(configuration);
        banConfiguration.read(configuration);

        enableCheckTimings = getBoolean(configuration, ConfigurationValues.ENABLE_CHECK_TIMINGS);
        enableTpsHelper = getBoolean(configuration, ConfigurationValues.ENABLE_TPS_HELPER);
        tpsHelperLimit = getInteger(configuration, ConfigurationValues.TPS_HELPER_LIMIT);
        violationNotifyMessage = new ConfigurationString(TextFormat.colorize('&', getString(configuration, ConfigurationValues.VIOLATION_NOTIFY_MESSAGE)));
        commandNoPermissionMessage = TextFormat.colorize('&', getString(configuration, ConfigurationValues.ARC_COMMAND_NO_PERMISSION_MESSAGE));
        prefix = TextFormat.colorize('&', getString(configuration, ConfigurationValues.ARC_PREFIX));
        violationDataTimeout = getInteger(configuration, ConfigurationValues.VIOLATION_DATA_TIMEOUT);
        enableEventApi = getBoolean(configuration, ConfigurationValues.ENABLE_EVENT_API);
    }

    /**
     * @return the ban configuration
     */
    public BanConfiguration banConfiguration() {
        return banConfiguration;
    }

    /**
     * @return the kick configuration
     */
    public KickConfiguration kickConfiguration() {
        return kickConfiguration;
    }

    /**
     * @return if check timings are enabled
     */
    public boolean enableCheckTimings() {
        return enableCheckTimings;
    }

    /**
     * @return if TPS helper is enabled.
     */
    public boolean enableTpsHelper() {
        return enableTpsHelper;
    }

    /**
     * @return if event API is enabled.
     */
    public boolean enableEventApi() {
        return enableEventApi;
    }

    /**
     * @return TPS helper limit
     */
    public int tpsHelperLimit() {
        return tpsHelperLimit;
    }

    /**
     * @return violation data timeout
     */
    public int violationDataTimeout() {
        return violationDataTimeout;
    }

    /**
     * @return violation notify message
     */
    public ConfigurationString violationNotifyMessage() {
        return new ConfigurationString(violationNotifyMessage);
    }

    /**
     * @return command no permission message
     */
    public String commandNoPermissionMessage() {
        return commandNoPermissionMessage;
    }

    /**
     * @return the prefix
     */
    public String prefix() {
        return prefix;
    }

    /**
     * @return the {@link FileConfiguration} from {@link Arc}
     */
    public Config fileConfiguration() {
        return Arc.arc().getConfig();
    }

    /**
     * Reload the configuration
     */
    public void reloadConfiguration() {
        Arc.plugin().reloadConfig();

        final Config configuration = Arc.plugin().getConfig();
        read(configuration);

        Arc.arc().checks().reload(this);
        Arc.arc().violations().reload(this);
        Arc.arc().punishment().reload(this);
    }

}
