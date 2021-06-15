package me.vrekt.arc;

import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import me.vrekt.arc.check.CheckManager;
import me.vrekt.arc.configuration.ArcConfiguration;
import me.vrekt.arc.exemption.ExemptionManager;
import me.vrekt.arc.listener.connection.PlayerConnectionListener;
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
    private static final String IPL_VERSION = "1.0";

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

        getLogger().info(TextFormat.DARK_GREEN + "Saving configuration...");
        saveConfig();

        getLogger().info(TextFormat.DARK_GREEN + "Ready!");
    }

    @Override
    public void onDisable() {

    }

    /**
     * Load online players.
     */
    private void loadOnlinePlayers() {
        getServer().getOnlinePlayers()
                .values()
                .forEach(player -> {
                    Arc.arc().violations().onPlayerJoin(player);
                    Arc.arc().exemptions().onPlayerJoin(player);
                });
    }

    /**
     * @return this instance.
     */
    public static Arc arc() {
        return Arc.arc;
    }

    /**
     * @return this plugin
     */
    public static Plugin plugin() {
        return Arc.arc;
    }

    /**
     * @return the arc configuration
     */
    public ArcConfiguration configuration() {
        return arcConfiguration;
    }

    /**
     * @return the violation manager
     */
    public ViolationManager violations() {
        return violationManager;
    }

    /**
     * @return the exemptions manager
     */
    public ExemptionManager exemptions() {
        return exemptionManager;
    }

    /**
     * @return the check manager
     */
    public CheckManager checks() {
        return checkManager;
    }

    /**
     * @return the punishment manager
     */
    public PunishmentManager punishment() {
        return punishmentManager;
    }
}
