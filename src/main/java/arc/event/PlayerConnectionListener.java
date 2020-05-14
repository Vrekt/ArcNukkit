package arc.event;

import arc.Arc;
import arc.check.permission.Permissions;
import arc.data.inventory.InventoryData;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;

/**
 * Listens for player connection changes.
 */
public final class PlayerConnectionListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        final var player = event.getPlayer();
        InventoryData.putData(player);
        Arc.violationManager().onPlayerConnect(player);

        // TODO: Debug
        player.addAttachment(Arc.plugin(), Permissions.PERMISSION_NOTIFY);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        final var player = event.getPlayer();
        InventoryData.removeData(player);
        Arc.violationManager().onPlayerDisconnect(player);
    }

}
