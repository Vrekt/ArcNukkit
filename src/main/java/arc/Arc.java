package arc;

import arc.check.permission.Permissions;
import arc.data.inventory.InventoryData;
import arc.event.PlayerConnectionListener;
import arc.event.inventory.PlayerInventoryListener;
import arc.event.packet.NukkitPacketListener;
import arc.utility.ChatColor;
import arc.violation.PlayerViolationManager;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;

/**
 * The base
 */
public final class Arc extends PluginBase {

    /**
     * The plugin
     */
    private static PluginBase plugin;
    /**
     * The configuration
     */
    private static Config configuration;

    /**
     * The player violation manager.
     */
    private static PlayerViolationManager violationManager;

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {
        getLogger().info(ChatColor.RED + "*THIS IS AN EXPERIMENTAL AND VERY EARLY VERSION OF ARC.**");
        getLogger().info(ChatColor.RED + "*EXPECT BUGS, CRASHES, WIP CHECKS, RANDOM LOG MESSAGES**");
        plugin = this;

        getLogger().info(ChatColor.GREEN + "Saving default configuration....");
        saveDefaultConfig();
        configuration = getConfig();

        getLogger().info(ChatColor.GREEN + "Loading current online players...");

        getLogger().info(ChatColor.GREEN + "Registering events");
        violationManager = new PlayerViolationManager(this);
        getServer().getScheduler().scheduleRepeatingTask(violationManager, 20, false);

        getServer().getPluginManager().registerEvents(new PlayerConnectionListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerInventoryListener(), this);
        getServer().getPluginManager().registerEvents(new NukkitPacketListener(), this);

        populatePlayers();

        getLogger().info(ChatColor.GREEN + "Done!");
    }

    @Override
    public void onDisable() {

    }

    /**
     * TODO: Hopefully support reloading all the way through if possible.
     * Populate player data in-case of a reload.
     */
    private void populatePlayers() {
        for (final var player : getServer().getOnlinePlayers().values()) {
            InventoryData.putData(player);
            violationManager.onPlayerConnect(player);

            // TODO: For now just add this for debug purposes.
            player.addAttachment(Arc.plugin(), Permissions.PERMISSION_NOTIFY);
        }
    }

    /**
     * @return this plugin
     */
    public static PluginBase plugin() {
        return Arc.plugin;
    }

    /**
     * @return the configuration
     */
    public static Config configuration() {
        return Arc.configuration;
    }

    /**
     * @return the violation manager.
     */
    public static PlayerViolationManager violationManager() {
        return Arc.violationManager;
    }
}
