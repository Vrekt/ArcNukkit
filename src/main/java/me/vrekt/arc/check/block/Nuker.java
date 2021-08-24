package me.vrekt.arc.check.block;


import cn.nukkit.Player;
import me.vrekt.arc.Arc;
import me.vrekt.arc.check.Check;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.check.result.CheckResult;
import me.vrekt.arc.data.block.BlockData;
import me.vrekt.arc.data.packet.PacketData;

/**
 * Checks if the player is using nuker - destroying an improbable amount of blocks around them.
 * This check will also utilize {@code maxPacketsPerSecond} to ensure not too many events are sent.
 * <p>
 * packet-check: the check will use packets
 * creative-only: Player must be in creative to check.
 * max-breaks-per-second: The max amount of block breaks per second.
 * min-delta-between-breaks: The minimum time allowed between the last break.
 * min-delta-between-breaks-threshold: the amount of times the above ^^ has to be triggered to flag.
 */
public final class Nuker extends Check {

    /**
     * packet-check: the check will use packets
     * creative-only: Player must be in creative to check.
     * If packets should be checked.
     */
    private boolean packetCheck, creativeOnly, checkPackets;

    /**
     * max-breaks-per-second: The max amount of block breaks per second.
     * min-delta-between-breaks: The minimum time allowed between the last break.
     * min-delta-between-breaks-threshold: the amount of times the above ^^ has to be triggered to flag.
     */
    private int maxBreaksPerSecond, minDeltaBetweenBreaks, minDeltaBetweenBreaksThreshold;

    /**
     * Max packets and packet kick threshold
     */
    private int maxPacketsPerSecond, packetKickThreshold;

    /**
     * Kick if the threshold is reached
     */
    private boolean kickIfThresholdReached;

    public Nuker() {
        super(CheckType.NUKER);

        enabled(true)
                .cancel(true)
                .cancelLevel(0)
                .notify(true)
                .notifyEvery(4)
                .ban(true)
                .banLevel(1000)
                .kick(true)
                .kickLevel(500)
                .build();

        addConfigurationValueWithComment("packet-check", true,
                "If the check should use packets instead of events.");
        addConfigurationValueWithComment("creative-only", true,
                "If the check is only active when players are in creative.");
        addConfigurationValueWithComment("max-breaks-per-second", 15,
                "The max amount of block breaks allowed per second.");
        addConfigurationValueWithComment("min-delta-between-breaks", 50,
                "The minimum time allowed between (theLastBreakTime - currentTime)");
        addConfigurationValueWithComment("min-delta-between-breaks-threshold", 5,
                "The amount of times a player has to break a block too fast to actually flag.");

        addConfigurationValueWithComment("check-packets", true,
                "If packets should be checked.");
        addConfigurationValueWithComment("max-break-packets-per-second", 50,
                "The max amount of break packets allowed per second.");
        addConfigurationValueWithComment("kick-if-threshold-reached", false,
                "If the player should be kicked if they send too many break packets.");
        addConfigurationValueWithComment("packet-kick-threshold", 100,
                "The max amount of packets allowed before a player is kicked.");

        if (enabled()) load();
    }

    /**
     * Check this player for Nuker.
     *
     * @param player the player
     * @return the result
     */
    public boolean check(Player player) {
        if (!enabled() || exempt(player) || creativeOnly && player.getGamemode() != 1) return false;

        final CheckResult result = new CheckResult();
        final long now = System.currentTimeMillis();

        // first, just check packet-stuff.
        // Don't use a scheduled check here, because meh.
        if (checkPackets) {
            final PacketData packetData = PacketData.get(player);

            final int count = packetData.getBreakPacketCount() + 1;
            packetData.setBreakPacketCount(count);
            final long packetDelta = now - packetData.getLastBreakPacketReset();

            // check packet counts and deltas.
            if (packetDelta >= 1000) {
                packetData.setLastBreakPacketReset(now);
                if (count >= maxPacketsPerSecond) {
                    result.setFailed("Too many break packets (or events) per second.")
                            .withParameter("amount", count)
                            .withParameter("max", maxPacketsPerSecond);

                    if (count >= packetKickThreshold && kickIfThresholdReached
                            && !Arc.getInstance().getPunishmentManager().hasPendingKick(player)) {
                        Arc.getInstance().getPunishmentManager().kickPlayer(player, this);
                    }
                } else {
                    packetData.setCancelBreakPackets(false);
                }

                packetData.setBreakPacketCount(0);
            }

            // cancel if we should.
            if (packetData.cancelBreakPackets()) {
                result.setFailed("Cancelling break packets because there were too many.");
                checkViolation(player, result);
                return true;
            }

            final boolean violation = checkViolation(player, result);
            packetData.setCancelBreakPackets(violation);
            if (violation) return true;
        }

        // now, block checking.
        final BlockData data = BlockData.get(player);
        final long delta = now - data.getLastBreak();
        data.setTotalBroke(data.getTotalBroke() + 1);
        data.setLastBreak(now);

        if (now - data.getTotalBrokeReset() >= 1000) {
            data.setTotalBrokeReset(now);

            if (data.getTotalBroke() > maxBreaksPerSecond) {
                result.setFailed("Breaking too many blocks per second.")
                        .withParameter("total", data.getTotalBroke())
                        .withParameter("max", maxBreaksPerSecond);
            }

            data.setTotalBroke(0);
        }

        // check delta times
        if (delta < minDeltaBetweenBreaks) {
            // "failed", increment one count.
            data.setLastBreakDeltaCount(data.getLastBreakDeltaCount() + 1);

            // make sure we haven't already failed, and then check against min.
            if (data.getLastBreakDeltaCount() >= minDeltaBetweenBreaksThreshold
                    && !result.failed()) {
                result.setFailed("Breaking blocks too fast")
                        .withParameter("count", data.getLastBreakDeltaCount())
                        .withParameter("min", minDeltaBetweenBreaksThreshold)
                        .withParameter("delta", delta)
                        .withParameter("min-delta", minDeltaBetweenBreaks);
            }
        } else {
            // lower since we're okay.
            data.setLastBreakDeltaCount(data.getLastBreakDeltaCount() - 1);
        }

        return checkViolation(player, result);
    }

    /**
     * @return if this check uses packets.
     */
    public boolean isPacketCheck() {
        return packetCheck;
    }

    @Override
    public void reloadConfig() {
        load();
    }

    @Override
    public void load() {
        packetCheck = configuration.getBoolean("packet-check");
        creativeOnly = configuration.getBoolean("creative-only");
        checkPackets = configuration.getBoolean("check-packets");
        maxBreaksPerSecond = configuration.getInt("max-breaks-per-second");
        minDeltaBetweenBreaks = configuration.getInt("min-delta-between-breaks");
        minDeltaBetweenBreaksThreshold = configuration.getInt("min-delta-between-breaks-threshold");

        maxPacketsPerSecond = configuration.getInt("max-break-packets-per-second");
        kickIfThresholdReached = configuration.getBoolean("kick-if-threshold-reached");
        packetKickThreshold = configuration.getInt("packet-kick-threshold");
    }
}
