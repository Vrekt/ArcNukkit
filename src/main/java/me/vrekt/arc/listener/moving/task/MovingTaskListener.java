package me.vrekt.arc.listener.moving.task;


import cn.nukkit.Player;
import cn.nukkit.Server;
import me.vrekt.arc.Arc;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.check.moving.Flight;
import me.vrekt.arc.data.moving.MovingData;

/**
 * A task ran every 10 ticks to update the player status and check things.
 */
public final class MovingTaskListener implements Runnable {

    /**
     * Flight check
     */
    private final Flight flight;

    public MovingTaskListener() {
        flight = Arc.getInstance().getCheckManager().getCheck(CheckType.FLIGHT);
        if (flight.enabled()) Server.getInstance().getScheduler().scheduleRepeatingTask(Arc.getPlugin(), this, 10);
    }

    @Override
    public void run() {
        final long now = System.currentTimeMillis();
        for (Player player : Server.getInstance().getOnlinePlayers().values()) {
            // make sure we are in a safe world.
            final MovingData data = MovingData.get(player);
            if (now - data.lastMovingUpdate() >= 500) {
                // player hasn't moved in a while, check-in.
                checkIn(player, data);
            }
        }
    }

    /**
     * Check in
     *
     * @param player the player
     * @param data   their data
     */
    private void checkIn(Player player, MovingData data) {
        if (!data.onGround()) flight.checkNoMovement(player, data);
    }

}
