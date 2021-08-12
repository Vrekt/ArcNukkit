package me.vrekt.arc.configuration;

import cn.nukkit.utils.Config;

/**
 * Basic class for configuration validation
 */
public interface Configurable {

    /**
     * /**
     * Load initial configuration
     *
     * @param configuration the config
     */
    default void loadConfiguration(ArcConfiguration configuration) {

    }

    /**
     * Read from file configuration
     *
     * @param configuration the config
     */
    default void readFromFile(Config configuration) {

    }

    /**
     * Read from arc configuration
     *
     * @param configuration configuration
     */
    default void readFromArc(ArcConfiguration configuration) {

    }

    /**
     * Reload the configuration
     *
     * @param configuration the configuration
     */
    default void reloadConfiguration(ArcConfiguration configuration) {

    }

}
