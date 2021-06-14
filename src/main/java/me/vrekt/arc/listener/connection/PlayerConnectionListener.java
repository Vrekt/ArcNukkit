package me.vrekt.arc.listener.connection;


import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import me.vrekt.arc.Arc;
import me.vrekt.arc.data.Data;

/**
 * Listens for player disconnects/connects
 */
public final class PlayerConnectionListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        Arc.arc().violations().onPlayerJoin(player);
        Arc.arc().exemptions().onPlayerJoin(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerLeave(PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        // TODO: Nukkit bug
        if (player.isBanned()) {
            return;
        }

        Arc.arc().violations().onPlayerLeave(player);
        Arc.arc().exemptions().onPlayerLeave(player);
        Data.removeAll(player);
    }

}
