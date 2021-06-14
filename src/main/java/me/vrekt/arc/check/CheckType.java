package me.vrekt.arc.check;

/**
 * Tells what type the check is
 */
public enum CheckType {
    /**
     * Checks if the player is using items too fast.
     */
    FAST_USE("FastUse", CheckCategory.PLAYER);

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
