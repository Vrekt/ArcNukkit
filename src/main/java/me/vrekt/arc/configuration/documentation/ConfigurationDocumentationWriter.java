package me.vrekt.arc.configuration.documentation;

import cn.nukkit.plugin.Plugin;
import me.vrekt.arc.check.CheckType;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A class that handles writing the documentation for configuration settings
 */
public final class ConfigurationDocumentationWriter {

    /**
     * Map of check sections
     */
    private final Map<CheckType, List<String>> sections = new HashMap<>();

    /**
     * Create the documentation file.
     *
     * @param plugin the plugin
     * @return the result
     */
    public boolean createDocumentationFile(Plugin plugin) {
        final Path path = Paths.get(plugin.getDataFolder() + "\\configuration-explained.txt");
        if (Files.exists(path)) return true;

        try {
            Files.createFile(path);
            return true;
        } catch (Exception any) {
            plugin.getLogger().warning("Failed to create the configuration documentation file.");
        }
        return false;
    }

    /**
     * Write all given lines and check sections
     *
     * @param plugin the plugin
     */
    public void write(Plugin plugin) {
        final Path path = Paths.get(plugin.getDataFolder() + "\\configuration-explained.txt");
        final StringBuilder builder = new StringBuilder();
        final AtomicBoolean first = new AtomicBoolean();

        sections.forEach((check, lines) -> {
            if (first.get()) {
                builder.append("\n\n");
            }

            first.compareAndSet(false, true);
            builder.append("=========================================\n");
            builder.append(check.getName()).append("\n");
            builder.append("=========================================")
                    .append("\n")
                    .append("\n")
                    .append("\n")
                    .append("\n");

            for (int i = 0; i < lines.size(); i++) {
                if (i >= lines.size() - 1) {
                    builder.append(lines.get(i));
                    builder.append("\n\n");
                } else {
                    builder.append(lines.get(i)).append("\n\n");
                }
            }
        });

        try {
            Files.write(path, builder.toString().getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (Exception any) {
            plugin.getLogger().warning("Failed to write configuration documentation file.");
        }
    }


    /**
     * Add a configuration check section
     *
     * @param check the check
     */
    public void addConfigurationSection(CheckType check) {
        sections.put(check, new ArrayList<>());
    }

    /**
     * Add a configuration value
     *
     * @param check   the check from
     * @param name    the name of the config option
     * @param value   the default value
     * @param comment the comment
     */
    public void addConfigurationValue(CheckType check, String name, Object value, String comment) {
        sections.get(check).add(name + "=" + value.toString() + " (default value)\n" + comment);
    }

    /**
     * Cleanup.
     */
    public void cleanup() {
        sections.clear();
    }

}
