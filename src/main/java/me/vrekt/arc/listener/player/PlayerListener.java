package me.vrekt.arc.listener.player;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.*;
import me.vrekt.arc.Arc;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.check.player.FastUse;
import me.vrekt.arc.data.combat.CombatData;
import me.vrekt.arc.data.moving.MovingData;
import me.vrekt.arc.data.player.PlayerData;
import me.vrekt.arc.exemption.type.ExemptionType;

/**
 * Listens for player related checks.
 */
public final class PlayerListener implements Listener {

    /**
     * The fast use check
     */
    private final FastUse fastUse;

    public PlayerListener() {
        fastUse = Arc.getInstance().getCheckManager().getCheck(CheckType.FAST_USE);
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

    /**
     * Handle interaction events
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    private void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) {
            final CombatData data = CombatData.get(event.getPlayer());

            // Workaround: more swing packets are sent when interacting with blocks.
            data.setLastBlockInteract(System.currentTimeMillis());
            data.setTotalBlockInteracts(data.getTotalBlockInteracts() + 1);
        }
    }

    /**
     * Handle changing from creative -> survival for movement checks.
     *
     * @param event the event
     */
    @EventHandler
    private void onGameModeChange(PlayerGameModeChangeEvent event) {
        final Player player = event.getPlayer();

        if (event.getNewGamemode() == 0
                && player.getGamemode() == 1 || player.getGamemode() == 3) {
            final MovingData data = MovingData.get(player);
            data.setInAirTime(0);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onTeleport(PlayerTeleportEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.UNKNOWN) {
            Arc.getInstance().getExemptionManager().addExemption(event.getPlayer(), ExemptionType.TELEPORT, 500);
        }
    }

}
