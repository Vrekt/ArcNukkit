package arc.check;

/**
 * Represents the various checks as an enum.
 */
public enum CheckType {

    FLIGHT("Flight", "flight"),
    FAST_USE("FastUse", "fastuse");
    /**
     * The fancy check name.
     * The configuration name
     */
    private final String name, configuration;

    CheckType(String name, String configuration) {
        this.name = name;
        this.configuration = configuration;
    }

    /**
     * @return the fancy name of the check
     */
    public String checkName() {
        return name;
    }

    /**
     * @return the configuration name of the check.
     */
    public String configurationName() {
        return configuration;
    }
}
