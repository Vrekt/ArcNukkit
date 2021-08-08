package me.vrekt.arc.check;

/**
 * Tells what type the check is
 */
public enum CheckType {
    /**
     * Checks if the player is using items too fast.
     */
    FAST_USE("FastUse", CheckCategory.PLAYER),

    /**
     * Checks if the player is sending too many movement packets.
     */
    MORE_PACKETS("MorePackets", CheckCategory.MOVING),

    /**
     * Checks if the player is flying.
     */
    FLIGHT("Flight", CheckCategory.MOVING),

    /**
     * Checks if the player is moving too fast.
     */
    SPEED("Speed", CheckCategory.MOVING),

    /**
     * Checks if the player is breaking an impossible amount of blocks
     */
    NUKER("Nuker", CheckCategory.BLOCK),

    /**
     * Checks if the player is attacking entities from far away
     */
    REACH("Reach", CheckCategory.COMBAT),

    /**
     * Checks if the player is breaking blocks too fast.
     */
    FAST_BREAK("FastBreak", CheckCategory.BLOCK);

    /**
     * The name
     */
    private final String name;

    /**
     * The category
     */
    private final CheckCategory category;

    CheckType(String name, CheckCategory category) {
        this.name = name;
        this.category = category;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the category
     */
    public CheckCategory category() {
        return category;
    }

    /**
     * Get a check by name
     *
     * @param name the name
     * @return the {@link CheckType} or {@code null} if not found
     */
    public static CheckType getCheckTypeByName(String name) {
        for (CheckType value : values()) {
            if (value.getName().equalsIgnoreCase(name)) {
                return value;
            }
        }
        return null;
    }

}
