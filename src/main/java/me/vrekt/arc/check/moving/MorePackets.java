package me.vrekt.arc.check.moving;

import cn.nukkit.Player;
import cn.nukkit.Server;
import me.vrekt.arc.Arc;
import me.vrekt.arc.check.Check;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.check.result.CheckResult;
import me.vrekt.arc.data.moving.MovingData;

/**
 * Checks if the player is sending too many movement related packets.
 * {@link cn.nukkit.network.protocol.MovePlayerPacket}
 * {@link cn.nukkit.network.protocol.MoveEntityAbsolutePacket}
 * {@link cn.nukkit.network.protocol.MoveEntityDeltaPacket}
 */
public final class MorePackets extends Check {

    /**
     * The max moves allowed per second.
     * The max threshold allowed before kicking
     */
    private int maxMovesPerSecond, packetKickThreshold;

    /**
     * If this check should kick for reaching the threshold
     */
    private boolean kickIfThresholdReached;

    public MorePackets() {
        super(CheckType.MORE_PACKETS);
        enabled(true)
                .cancel(true)
                .cancelLevel(0)
                .notify(true)
                .notifyEvery(1)
                .ban(true)
                .banLevel(20)
                .kick(false)
                .build();

        addConfigurationValue("max-moves-per-second", 25);
        addConfigurationValue("kick-if-threshold-reached", true);
        addConfigurationValue("packet-kick-threshold", 50);

        if (enabled()) load();
    }

    /**
     * Check this player
     *
     * @param player the player
     * @param data   their data
     */
    private void check(Player player, MovingData data) {
        if (exempt(player)) return;

        final int moveCount = data.movePlayerPackets();
        final CheckResult result = new CheckResult();
        if (moveCount > maxMovesPerSecond) {
            populateResult(result, "Too many move packets per second", moveCount, maxMovesPerSecond);
            data.cancelMovePlayerPacket(checkViolation(player, result));
            kickPlayerIfThresholdReached(player, moveCount);
        } else {
            data.cancelMovePlayerPacket(false);
        }

        data.movePlayerPackets(0);
    }

    /**
     * Populate the check result with information
     *
     * @param result      the result
     * @param information the information
     * @param count       the count
     * @param max         the max
     */
    private void populateResult(CheckResult result, String information, int count, int max) {
        result.setFailed(information)
                .withParameter("count", count)
                .withParameter("max", max);
    }

    /**
     * Kick the player if the threshold is reached
     *
     * @param player the player
     * @param count  the count
     */
    private void kickPlayerIfThresholdReached(Player player, int count) {
        if (kickIfThresholdReached && count >= packetKickThreshold
                && Arc.getInstance().getPunishmentManager().hasPendingKick(player)) {
            Arc.getInstance().getPunishmentManager().kickPlayer(player, this);
        }
    }

    @Override
    public void reloadConfig() {
        load();
    }

    @Override
    public void unload() {
        cancelScheduled();
    }

    @Override
    public void load() {
        maxMovesPerSecond = configuration.getInt("max-moves-per-second");
        kickIfThresholdReached = configuration.getBoolean("kick-if-threshold-reached");
        packetKickThreshold = configuration.getInt("packet-kick-threshold");

        schedule(() -> {
            for (Player player : Server.getInstance().getOnlinePlayers().values()) {
                check(player, MovingData.get(player));
            }
        }, 20);
    }
}
