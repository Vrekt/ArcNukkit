package me.vrekt.arc.configuration.check;

/**
 * Represents a basic check configuration.
 * Only for retrieving values, and not related to default check configuration stuff.
 */
public abstract class BasicCheckConfiguration {

    /**
     * Write the configuration
     *
     * @param configuration the config
     */
    public abstract void write(CheckConfiguration configuration);

    /**
     * Load or reload the configuration
     *
     * @param configuration the configuration section for the check.
     */
    public abstract void load(CheckConfiguration configuration);

}
