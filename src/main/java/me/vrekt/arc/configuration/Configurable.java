package me.vrekt.arc.configuration;

import cn.nukkit.utils.Config;

/**
 * Basic class for configuration validation
 */
public abstract class Configurable extends ConfigurableReader {

    /**
     * Read the configuration
     *
     * @param configuration the configuration
     */
    public void read(Config configuration) {

    }

    /**
     * Reload the configuration
     *
     * @param configuration the configuration
     */
    public void reload(ArcConfiguration configuration) {

    }

}
