package me.vrekt.arc.listener.packet.player;

import cn.nukkit.Player;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.level.Location;
import cn.nukkit.network.protocol.MovePlayerPacket;
import me.vrekt.arc.Arc;
import me.vrekt.arc.check.CheckCategory;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.check.moving.Flight;
import me.vrekt.arc.check.moving.LiquidWalk;
import me.vrekt.arc.check.moving.Phase;
import me.vrekt.arc.check.moving.Speed;
import me.vrekt.arc.data.moving.MovingData;
import me.vrekt.arc.listener.packet.NukkitPacketHandler;
import me.vrekt.arc.listener.packet.NukkitPacketListener;
import me.vrekt.arc.permissions.Permissions;
import me.vrekt.arc.utility.MovingAccess;

/**
 * Listens for {@link cn.nukkit.network.protocol.MovePlayerPacket}
 */
public final class MovePlayerPacketListener extends NukkitPacketListener {

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

    /**
     * The water walk check.
     */
    private final LiquidWalk liquidWalk;

    public MovePlayerPacketListener(NukkitPacketHandler handler) {
        super(handler);
        flight = Arc.getInstance().getCheckManager().getCheck(CheckType.FLIGHT);
        speed = Arc.getInstance().getCheckManager().getCheck(CheckType.SPEED);
        phase = Arc.getInstance().getCheckManager().getCheck(CheckType.PHASE);
        liquidWalk = Arc.getInstance().getCheckManager().getCheck(CheckType.LIQUID_WALK);
    }

    @Override
    public void onPacketReceiving(DataPacketReceiveEvent event) {
        final Player player = event.getPlayer();
        if (Permissions.canBypassCategory(player, CheckCategory.MOVING)) return;

        final MovePlayerPacket packet = (MovePlayerPacket) event.getPacket();
        if (!Double.isFinite(packet.x)
                || !Double.isFinite(packet.y)
                || !Double.isFinite(packet.z)) return;

        final MovingData data = MovingData.get(player);
        final Location from = data.to() == null ? player.getLocation() : data.to();
        final Location to = new Location(packet.x, packet.y - player.getEyeHeight(), packet.z, player.level);
        final double delta = from.distanceSquared(to);

        if (delta > 100) {
            // large movement, ignore.
            player.teleport(from);
            return;
        }

        if (player.getHealth() > 0 && (delta > 1f / 256)) {
            final boolean hasMoved = from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ();
            if (hasMoved) {
                MovingAccess.calculateMovement(player, data, from, to);
                if (flight.enabled()) flight.check(player, data);
                if (speed.enabled()) speed.check(player, data);
                if (liquidWalk.enabled()) liquidWalk.check(player, data);
                // if (phase.enabled()) phase.check(player, data);
            }
        }

        if (!handler.isExempt(CheckType.MORE_PACKETS, player)) {
            if (MovingData.get(player).incrementMovePlayerPacketsAndCheckCancel()) {
                event.setCancelled();
            }
        }
    }
}
