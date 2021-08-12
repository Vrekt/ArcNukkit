package me.vrekt.arc.configuration;

import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import me.vrekt.arc.Arc;
import me.vrekt.arc.configuration.ban.BanConfiguration;
import me.vrekt.arc.configuration.kick.KickConfiguration;
import me.vrekt.arc.configuration.types.ConfigurationString;
import me.vrekt.arc.configuration.values.ConfigurationSetting;

/**
 * The arc configuration
 */
public final class ArcConfiguration extends ConfigurationSettingReader implements Configurable {

    /**
     * Handles ban configuration values
     */
    private final BanConfiguration banConfiguration = new BanConfiguration();
    /**
     * Handles kick configuration values
     */
    private final KickConfiguration kickConfiguration = new KickConfiguration();

    /**
     * If the event API should be enabled.
     * If check timings should be enabled.
     */
    private boolean enableEventApi, enableCheckTimings;

    /**
     * TPS helper limit
     * The time after leaving violation data times out
     */
    private int violationDataTimeout;

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
    public void readFromFile(Config configuration) {
        kickConfiguration.readFromFile(configuration);
        banConfiguration.readFromFile(configuration);

        violationNotifyMessage = new ConfigurationString(TextFormat.colorize('&', getString(configuration, ConfigurationSetting.VIOLATION_NOTIFY_MESSAGE)));
        commandNoPermissionMessage = TextFormat.colorize('&', getString(configuration, ConfigurationSetting.ARC_COMMAND_NO_PERMISSION_MESSAGE));
        prefix = TextFormat.colorize('&', getString(configuration, ConfigurationSetting.ARC_PREFIX));
        violationDataTimeout = getInteger(configuration, ConfigurationSetting.VIOLATION_DATA_TIMEOUT);
        enableEventApi = getBoolean(configuration, ConfigurationSetting.ENABLE_EVENT_API);
        enableCheckTimings = getBoolean(configuration, ConfigurationSetting.ENABLE_CHECK_TIMINGS);
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
     * @return if event API is enabled.
     */
    public boolean enableEventApi() {
        return enableEventApi;
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
    public String getNoPermissionMessage() {
        return commandNoPermissionMessage;
    }

    /**
     * @return the prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * @return the {@link Config} from {@link Arc}
     */
    public Config fileConfiguration() {
        return Arc.getInstance().getConfig();
    }

    /**
     * @return if check timings are enabled.
     */
    public boolean enableCheckTimings() {
        return enableCheckTimings;
    }

    /**
     * Reload the configuration and all configurable components.
     */
    public void reloadConfigurationAndComponents() {
        Arc.getPlugin().reloadConfig();

        this.readFromFile(Arc.getPlugin().getConfig());
        Arc.getInstance().getCheckManager().reloadConfiguration(this);
        Arc.getInstance().getViolationManager().reloadConfiguration(this);
        Arc.getInstance().getExemptionManager().reloadConfiguration(this);
        Arc.getInstance().getPunishmentManager().reloadConfiguration(this);
    }

}
