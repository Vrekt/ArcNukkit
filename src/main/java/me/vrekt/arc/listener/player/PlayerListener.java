package me.vrekt.arc.listener.player;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerItemConsumeEvent;
import me.vrekt.arc.Arc;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.check.player.FastUse;
import me.vrekt.arc.data.player.PlayerData;

/**
 * Listens for player related checks.
 */
public final class PlayerListener implements Listener {

    /**
     * The fast use check
     */
    private final FastUse fastUse;

    public PlayerListener() {
        fastUse = (FastUse) Arc.arc().checks().getCheck(CheckType.FAST_USE);
    }

    /**
     * Invoked when the player eats something.
     * Here we can check for FastConsume
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onConsumeItem(PlayerItemConsumeEvent event) {
        final Player player = event.getPlayer();
        final PlayerData data = PlayerData.get(player);
        data.isConsuming(false);

        final boolean result = fastUse.check(player, data);
        event.setCancelled(result);
    }

}
