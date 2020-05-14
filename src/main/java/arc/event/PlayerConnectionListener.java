package arc.event;

import arc.Arc;
import arc.check.permission.Permissions;
import arc.data.inventory.InventoryData;
import arc.violation.Violations;
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

        // populate our data.
        final var player = event.getPlayer();
        InventoryData.putData(player);
        Violations.putViolationData(player);
        player.addAttachment(Arc.plugin(), Permissions.PERMISSION_NOTIFY);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {

        // remove our data.
        final var player = event.getPlayer();
        InventoryData.removeData(player);
        Violations.removeViolationData(player);
    }

}
