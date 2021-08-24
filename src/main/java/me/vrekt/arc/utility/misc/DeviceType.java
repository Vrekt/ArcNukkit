package me.vrekt.arc.utility.misc;

/**
 * Represents a device type a player is playing on.
 */
public enum DeviceType {

    ANDROID("Android", 1),
    IOS("iOS", 2),
    MAC_OS("MacOS", 3),
    FIRE_OS("FireOS", 4),
    GEAR_VR("GearVR", 5),
    HOLO_LENS("HoloLens", 6),
    WINDOWS_10("Windows", 7),
    WINDOWS("Windows", 8),
    DEDICATED("Dedicated", 9),
    PS4("PS4", 10),
    SWITCH("Switch", 11),
    UNKNOWN("Unknown", 0);

    /**
     * The name
     */
    private final String prettyName;

    /**
     * The Code
     */
    private final int code;

    DeviceType(String prettyName, int code) {
        this.prettyName = prettyName;
        this.code = code;
    }

    public String getPrettyName() {
        return prettyName;
    }

    public int getCode() {
        return code;
    }

    /**
     * Get the device type from the provided code.
     *
     * @param code the code
     * @return the device type or {@code UNKNOWN} if not found.
     */
    public static DeviceType getByCode(int code) {
        for (DeviceType value : values()) {
            if (value.code == code) return value;
        }
        return UNKNOWN;
    }

}
