package me.vrekt.arc.configuration.types;

/**
 * Placeholders
 */
public enum Placeholders {

    /**
     * Player
     */
    PLAYER("%player%"),
    /**
     * Check
     */
    CHECK("%check%"),
    /**
     * Level
     */
    LEVEL("%level%"),

    /**
     * Prefix
     */
    PREFIX("%prefix%"),

    /**
     * Time
     */
    TIME("%time%"),

    /**
     * Ban type
     */
    TYPE("%type%");

    /**
     * The placeholder
     */
    private final String placeholder;

    Placeholders(String placeholder) {
        this.placeholder = placeholder;
    }

    /**
     * @return the placeholder
     */
    public String placeholder() {
        return placeholder;
    }
}
