package me.vrekt.arc;

import cn.nukkit.command.PluginCommand;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import me.vrekt.arc.check.CheckManager;
import me.vrekt.arc.command.ArcCommand;
import me.vrekt.arc.configuration.ArcConfiguration;
import me.vrekt.arc.data.Data;
import me.vrekt.arc.exemption.ExemptionManager;
import me.vrekt.arc.listener.block.BlockListener;
import me.vrekt.arc.listener.combat.CombatListener;
import me.vrekt.arc.listener.connection.PlayerConnectionListener;
import me.vrekt.arc.listener.moving.MovingEventListener;
import me.vrekt.arc.listener.moving.task.MovingTaskListener;
import me.vrekt.arc.listener.packet.NukkitPacketHandler;
import me.vrekt.arc.listener.player.PlayerListener;
import me.vrekt.arc.punishment.PunishmentManager;
import me.vrekt.arc.violation.ViolationManager;

/**
 * The Arc plugin.
 */
public final class Arc extends PluginBase {

    /**
     * IPL version
     */
    private static final String IPL_VERSION = "1.6-8921b-nukkit";

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

    @Override
    public void onEnable() {
        if (arc != null) throw new UnsupportedOperationException();
        arc = this;

        getLogger().info(TextFormat.RED + "**THIS IS AN EXPERIMENTAL VERSION OF ARC**");
        getLogger().info(TextFormat.RED + "**PLEASE REPORT ANY ISSUES TO GITHUB**");
        getLogger().info(TextFormat.DARK_GREEN + "Initializing Arc " + IPL_VERSION);
        getLogger().info(TextFormat.DARK_GREEN + "Reading main configuration...");

        saveDefaultConfig();
        arcConfiguration.read(getConfig());

        getLogger().info(TextFormat.DARK_GREEN + "Registering checks and listeners...");
        checkManager.initialize();
        violationManager.initialize(arcConfiguration);
        punishmentManager.initialize(arcConfiguration);
        exemptionManager.initialize(arcConfiguration);
        loadOnlinePlayers();

        getServer().getPluginManager().registerEvents(new PlayerConnectionListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new NukkitPacketHandler(exemptionManager), this);
        getServer().getPluginManager().registerEvents(new MovingEventListener(), this);
        getServer().getPluginManager().registerEvents(new BlockListener(), this);
        getServer().getPluginManager().registerEvents(new CombatListener(), this);
        new MovingTaskListener();

        verifyCommand();
        getLogger().info(TextFormat.DARK_GREEN + "Saving configuration...");
        saveConfig();

        getLogger().info(TextFormat.DARK_GREEN + "Ready!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Saving file configuration...");
        saveConfig();

        getLogger().info("Closing resources...");
        exemptionManager.close();
        violationManager.close();
        checkManager.close();
        punishmentManager.close();

        getLogger().info("Removing player data...");
        getServer().getOnlinePlayers().values().forEach(Data::removeAll);
        arc = null;

        getLogger().info("Goodbye.");
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
}
