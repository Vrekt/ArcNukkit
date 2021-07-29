package me.vrekt.arc.configuration.values;

import me.vrekt.arc.configuration.types.BanLengthType;
import me.vrekt.arc.configuration.types.BanListType;

/**
 * A basic configuration value
 */
public final class ConfigValue<T> {

    /**
     * The value name
     */
    private final String name;

    /**
     * The type
     */
    private final T type;

    /**
     * Initialize
     *
     * @param name the name
     * @param type the type
     */
    public ConfigValue(String name, T type) {
        this.name = name;
        this.type = type;
    }

    /**
     * @return the name
     */
    public String name() {
        return name;
    }

    /**
     * @return the type
     */
    public T type() {
        return type;
    }

    /**
     * @return the string value
     */
    public String stringValue() {
        return (String) type;
    }

    /**
     * @return the boolean value
     */
    public boolean booleanValue() {
        return (Boolean) type;
    }

    /**
     * @return the int value
     */
    public int intValue() {
        return (Integer) type;
    }

    /**
     * @return the ban list type value
     */
    public BanListType banListTypeValue() {
        return (BanListType) type;
    }

    /**
     * @return the ban length type value
     */
    public BanLengthType banLengthTypeValue() {
        return (BanLengthType) type;
    }

}
