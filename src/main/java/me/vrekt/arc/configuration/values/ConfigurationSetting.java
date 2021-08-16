package me.vrekt.arc.configuration.values;

import me.vrekt.arc.configuration.types.BanLengthType;
import me.vrekt.arc.configuration.types.BanListType;

/**
 * A map of configuration values
 */
public enum ConfigurationSetting {
    GLOBAL_KICK_MESSAGE(new ConfigValue<>("global-kick-message", "&cYou have been kicked for %check%")),
    GLOBAL_KICK_DELAY(new ConfigValue<>("global-kick-delay", 0)),
    GLOBAL_VIOLATIONS_KICK_MESSAGE(new ConfigValue<>("global-violations-kick-message", "%prefix% &9%player%&f will be kicked for &c%check%&f in &c%time%&f seconds.")),
    GLOBAL_BAN_MESSAGE(new ConfigValue<>("global-ban-message", "&cYou have been banned for %check%")),
    GLOBAL_BAN_DELAY(new ConfigValue<>("global-ban-delay", 0)),
    GLOBAL_BAN_TYPE(new ConfigValue<>("global-ban-type", BanListType.NAME)),
    GLOBAL_BAN_LENGTH_TYPE(new ConfigValue<>("global-ban-length-type", BanLengthType.DAYS)),
    GLOBAL_BAN_LENGTH(new ConfigValue<>("global-ban-length", 30)),
    GLOBAL_BROADCAST_BAN(new ConfigValue<>("global-broadcast-ban", false)),
    GLOBAL_BROADCAST_BAN_MESSAGE(new ConfigValue<>("global-broadcast-ban-message", "&c%player% was banned for %check% for %time% %type%")),
    GLOBAL_VIOLATIONS_BAN_MESSAGE(new ConfigValue<>("global-violations-ban-message", "%prefix% &9%player%&f will be banned for &c%check%&f in &c%time%&f seconds.")),
    VIOLATION_NOTIFY_MESSAGE(new ConfigValue<>("violation-notify-message", "%prefix% &9%player%&f has violated check &c%check%&8(&c%level%&8)&7")),
    ARC_COMMAND_NO_PERMISSION_MESSAGE(new ConfigValue<>("arc-command-no-permission-message", "Unknown command. Type /help for help.")),
    ARC_PREFIX(new ConfigValue<>("arc-prefix", "&8[&cArc&8]")),
    VIOLATION_DATA_TIMEOUT(new ConfigValue<>("violation-data-timeout", 30)),
    ENABLE_EVENT_API(new ConfigValue<>("enable-event-api", true)),
    ENABLE_CHECK_TIMINGS(new ConfigValue<>("enable-check-timings", true)),
    RESET_VIOLATION_DATA_AFTER(new ConfigValue<>("reset-violation-data-after", 10)),
    OP_CAN_BYPASS(new ConfigValue<>("op-can-bypass", true)),
    OP_CAN_VIEW_VIOLATIONS(new ConfigValue<>("op-can-view-violations", true));

    /**
     * The value
     */
    private final ConfigValue<?> value;

    ConfigurationSetting(ConfigValue<?> type) {
        this.value = type;
    }

    /**
     * @return raw value
     */
    public ConfigValue<?> raw() {
        return value;
    }

    /**
     * @return the value name
     */
    public String valueName() {
        return value.name();
    }

    /**
     * @return the string value
     */
    public String stringValue() {
        return value.stringValue();
    }

    /**
     * @return the boolean value
     */
    public boolean booleanValue() {
        return value.booleanValue();
    }

    /**
     * @return the int value
     */
    public int intValue() {
        return value.intValue();
    }

    /**
     * @return the ban list type value
     */
    public BanListType banListTypeValue() {
        return value.banListTypeValue();
    }

    /**
     * @return the ban length type value
     */
    public BanLengthType banLengthTypeValue() {
        return value.banLengthTypeValue();
    }

}
