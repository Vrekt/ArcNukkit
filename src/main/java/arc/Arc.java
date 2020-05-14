package arc;

import arc.check.permission.Permissions;
import arc.data.inventory.InventoryData;
import arc.event.PlayerConnectionListener;
import arc.event.inventory.PlayerInventoryListener;
import arc.event.packet.NukkitPacketListener;
import arc.utility.ChatColor;
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

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {
        getLogger().info(ChatColor.RED.color() + "*THIS IS AN EXPERIMENTAL AND VERY EARLY VERSION OF ARC.**");
        getLogger().info(ChatColor.RED.color() + "*EXPECT BUGS, CRASHES, WIP CHECKS, RANDOM LOG MESSAGES**");
        plugin = this;

        saveDefaultConfig();
        configuration = getConfig();

        populatePlayers();

        getServer().getPluginManager().registerEvents(new PlayerConnectionListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerInventoryListener(), this);
        getServer().getPluginManager().registerEvents(new NukkitPacketListener(), this);

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
}
