package me.vrekt.arc;

import cn.nukkit.command.PluginCommand;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.TextFormat;
import me.vrekt.arc.check.CheckManager;
import me.vrekt.arc.command.ArcCommand;
import me.vrekt.arc.configuration.ArcConfiguration;
import me.vrekt.arc.configuration.documentation.ConfigurationDocumentationWriter;
import me.vrekt.arc.data.Data;
import me.vrekt.arc.exemption.ExemptionManager;
import me.vrekt.arc.listener.block.BlockListener;
import me.vrekt.arc.listener.combat.CombatListener;
import me.vrekt.arc.listener.connection.PlayerConnectionListener;
import me.vrekt.arc.listener.moving.task.MovingTaskListener;
import me.vrekt.arc.listener.packet.NukkitPacketHandler;
import me.vrekt.arc.listener.player.PlayerListener;
import me.vrekt.arc.punishment.PunishmentManager;
import me.vrekt.arc.timings.CheckTimings;
import me.vrekt.arc.violation.ViolationManager;

/**
 * The Arc plugin.
 */
public final class Arc extends PluginBase {

    /**
     * IPL version
     * "X.X.X" current version of arc
     * "-XX" current year
     * "-XXabcd..." XX = month and date, letter = current revision for that day
     */
    public static final String VERSION_STRING = "1.7.2-22-617a-nukkit";

    /**
     * The instance of this class
     */
    private static Arc arc;

    /**
     * The arc configuration
     */
    private final ArcConfiguration arcConfiguration = new ArcConfiguration();

    /**
     * The violation manager.
     */
    private final ViolationManager violationManager = new ViolationManager();

    /**
     * The check manager
     */
    private final CheckManager checkManager = new CheckManager();

    /**
     * The exemption manager
     */
    private final ExemptionManager exemptionManager = new ExemptionManager();

    /**
     * Punishment manager.
     */
    private final PunishmentManager punishmentManager = new PunishmentManager();

    /**
     * The config doc/explanation writer.
     */
    private final ConfigurationDocumentationWriter configurationDocumentationWriter
            = new ConfigurationDocumentationWriter();

    @Override
    public void onEnable() {
        if (arc != null) throw new UnsupportedOperationException();
        arc = this;

        getLogger().info(TextFormat.RED + "**THIS IS AN EXPERIMENTAL VERSION OF ARC**");
        getLogger().info(TextFormat.RED + "**PLEASE REPORT ANY ISSUES TO GITHUB**");
        getLogger().info(TextFormat.DARK_GREEN + "Initializing Arc " + VERSION_STRING);
        getLogger().info(TextFormat.DARK_GREEN + "Reading main configuration...");

        saveDefaultConfig();
        arcConfiguration.readFromFile(getConfig());

        getLogger().info(TextFormat.DARK_GREEN + "Registering checks and listeners...");
        checkManager.initializeAllChecks();
        violationManager.loadConfiguration(arcConfiguration);
        punishmentManager.loadConfiguration(arcConfiguration);
        exemptionManager.loadConfiguration(arcConfiguration);
        loadOnlinePlayers();

        registerListeners();
        verifyCommand();

        getLogger().info(TextFormat.DARK_GREEN + "Saving configuration...");
        saveConfig();
        writeConfigurationDocumentation();

        getLogger().info(TextFormat.DARK_GREEN + "Ready!");
    }

    @Override
    public void onDisable() {
        getLogger().info(TextFormat.DARK_GREEN + "Saving file configuration...");
        saveConfig();

        getLogger().info(TextFormat.DARK_GREEN + "Closing resources...");
        exemptionManager.close();
        violationManager.close();
        checkManager.close();
        punishmentManager.close();
        CheckTimings.shutdown();

        getLogger().info(TextFormat.DARK_GREEN + "Removing player data...");
        getServer().getOnlinePlayers().values().forEach(Data::removeAll);
        arc = null;

        getLogger().info(TextFormat.DARK_GREEN + "Goodbye.");
    }

    /**
     * Register listeners.
     */
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerConnectionListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new NukkitPacketHandler(), this);
        getServer().getPluginManager().registerEvents(new BlockListener(), this);
        getServer().getPluginManager().registerEvents(new CombatListener(), this);
        new MovingTaskListener();
    }

    /**
     * Verify the command /arc exists.
     */
    private void verifyCommand() {
        final PluginCommand<?> command = (PluginCommand<?>) getServer().getPluginCommand("arc");
        if (command == null) {
            getLogger().critical("/arc command not found! You will not be able to use this command.");
        } else {
            command.setExecutor(new ArcCommand());
        }
    }

    /**
     * Load online players.
     */
    private void loadOnlinePlayers() {
        getServer().getOnlinePlayers()
                .values()
                .forEach(player -> {
                    violationManager.onPlayerJoin(player);
                    exemptionManager.onPlayerJoin(player);
                });
    }

    /**
     * Write config docs.
     */
    private void writeConfigurationDocumentation() {
        getServer().getScheduler().scheduleAsyncTask(this, new AsyncTask() {
            @Override
            public void onRun() {
                if (configurationDocumentationWriter.createDocumentationFile(Arc.this)) {
                    configurationDocumentationWriter.write(Arc.this);
                    configurationDocumentationWriter.cleanup();
                }
            }
        });
    }

    /**
     * @return the internal plugin
     */
    public static Plugin getPlugin() {
        return arc;
    }

    /**
     * @return arc
     */
    public static Arc getInstance() {
        return arc;
    }

    /**
     * @return the configuration
     */
    public ArcConfiguration getArcConfiguration() {
        return arcConfiguration;
    }

    /**
     * @return the violation manager
     */
    public ViolationManager getViolationManager() {
        return violationManager;
    }

    /**
     * @return the exemption manager
     */
    public ExemptionManager getExemptionManager() {
        return exemptionManager;
    }

    /**
     * @return the punishment manager
     */
    public PunishmentManager getPunishmentManager() {
        return punishmentManager;
    }

    /**
     * @return the check manager
     */
    public CheckManager getCheckManager() {
        return checkManager;
    }

    /**
     * @return the documentation writer.
     */
    public ConfigurationDocumentationWriter getConfigurationDocumentationWriter() {
        return configurationDocumentationWriter;
    }

    public static void debug(String information) {
        getInstance().getServer().getOnlinePlayers().values().forEach(player -> {
            if (getInstance().violationManager.isDebug(player)) {
                player.sendMessage(information);
            }
        });
    }

}
