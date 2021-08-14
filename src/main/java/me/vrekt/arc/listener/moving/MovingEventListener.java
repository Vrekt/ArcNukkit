package me.vrekt.arc.listener.moving;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.level.Location;
import me.vrekt.arc.Arc;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.check.moving.Flight;
import me.vrekt.arc.check.moving.Speed;
import me.vrekt.arc.data.moving.MovingData;
import me.vrekt.arc.utility.MovingAccess;

/**
 * Listens for player movement.
 */
public final class MovingEventListener implements Listener {

    /**
     * The flight check
     */
    private final Flight flight;

    /**
     * The speed check
     */
    private final Speed speed;

    /**
     * The phase check
     */
    private final Phase phase;

    public MovingEventListener() {
        flight = Arc.getInstance().getCheckManager().getCheck(CheckType.FLIGHT);
        speed = Arc.getInstance().getCheckManager().getCheck(CheckType.SPEED);
        phase = Arc.getInstance().getCheckManager().getCheck(CheckType.PHASE);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        MovingData data = MovingData.get(player);

        Location from = event.getFrom();
        Location to = event.getTo();

        // check if we have moved.
        boolean hasMoved = from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ();
        if (hasMoved) {
            MovingAccess.calculateMovement(data, from, to);
            // run checks
            runChecks(player, data);
        }
    }

    /**
     * Run movement related checks
     *
     * @param player the player
     * @param data   their data
     */
    private void runChecks(Player player, MovingData data) {
        if (flight.enabled()) flight.check(player, data);
        if (speed.enabled()) speed.check(player, data);
        if (phase.enabled()) phase.check(player, data);
    }

}
