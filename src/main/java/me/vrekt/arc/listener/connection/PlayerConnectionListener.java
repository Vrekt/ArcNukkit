package me.vrekt.arc.listener.connection;


import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import me.vrekt.arc.Arc;
import me.vrekt.arc.data.Data;
import me.vrekt.arc.data.moving.MovingData;
import me.vrekt.arc.utility.MovingAccess;

/**
 * Listens for player disconnects/connects
 */
public final class PlayerConnectionListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        Arc.getInstance().getViolationManager().onPlayerJoin(player);
        Arc.getInstance().getExemptionManager().onPlayerJoin(player);

        final MovingData data = MovingData.get(player);
        data.setPlayerJoinTime(System.currentTimeMillis());
        MovingAccess.calculateMovement(player, data, player.getLocation(), player.getLocation());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerLeave(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        if (player == null) return;

        // Players will still invoke this event,
        // Even if they are banned.
        // As of 7-15-2021
        if (player.isBanned()) {
            return;
        }

        Arc.getInstance().getViolationManager().onPlayerLeave(player);
        Arc.getInstance().getExemptionManager().onPlayerLeave(player);
        Data.removeAll(player);
    }

}
