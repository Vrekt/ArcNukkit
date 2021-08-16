package me.vrekt.arc.check.moving;

import cn.nukkit.Player;
import cn.nukkit.level.Location;
import cn.nukkit.potion.Effect;
import me.vrekt.arc.check.Check;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.check.result.CheckResult;
import me.vrekt.arc.compatibility.NukkitAccess;
import me.vrekt.arc.compatibility.block.BlockAccess;
import me.vrekt.arc.data.moving.MovingData;
import me.vrekt.arc.exemption.type.ExemptionType;
import me.vrekt.arc.timings.CheckTimings;
import me.vrekt.arc.utility.math.MathUtil;

/**
 * Checks if the player is moving too fast.
 */
public final class Speed extends Check {

    /**
     * Max distance player can move in one movement.
     * <p>
     * The max value of what can be considered a low jump.
     * The min value of what can be considered a low jump
     * Max speed allowed with low jumping.
     */
    private double maxLargeDistanceMovement, lowJumpMax, lowJumpMin;

    /**
     * The max movement speed on stairs.
     * The max speed descending slabs.
     */
    private double maxStairMovementSpeed, maxSlabMovementSpeed;

    /**
     * The max amount of times movement is allowed to be over the max.
     * Time required to be descending on slabs to check.
     */
    private int stairMovementSpeedThreshold, slabMovementSpeedThreshold;

    /**
     * Max value allowed to be considered a violation when comparing (current speed - last speed)
     */
    private double maxStairMovementSpeedDelta, maxSlabMovementSpeedDelta;

    /**
     * Max amount of delta(s) allowed. ^^
     * Max amount of batched movements allowed
     */
    private int maxStairMovementSpeedDeltaAmount, maxSlabMovementSpeedDeltaAmount, maxBatchedMovementCount;

    /**
     * Min time required to be on ground
     * <p>
     * TODO: Is workaround
     * Max number of fast movements allowed with normal checking.
     */
    private int minOnGroundTime, minOffModifierTime, maxAllowedFastMovements;

    /**
     * Max time allowed to give player temp speed increase
     */
    private long lastTeleportTimeMax;

    /**
     * Last teleport time speed increase
     */
    private double lastTeleportTimeSpeedIncrease;

    public Speed() {
        super(CheckType.SPEED);

        enabled(true)
                .cancel(true)
                .cancelLevel(0)
                .notify(true)
                .notifyEvery(1)
                .ban(false)
                .banLevel(20)
                .kick(false)
                .build();

        addConfigurationValue("max-large-distance-movement", 4);
        addConfigurationValue("max-stair-movement-speed", 0.69);
        addConfigurationValue("stair-movement-speed-threshold", 3);
        addConfigurationValue("max-slab-movement-speed", 0.32);
        addConfigurationValue("slab-movement-speed-threshold", 3);
        addConfigurationValue("max-stair-movement-speed-delta", 0.03);
        addConfigurationValue("max-stair-movement-speed-delta-amount", 3);
        addConfigurationValue("max-slab-movement-speed-delta", 0.03);
        addConfigurationValue("max-slab-movement-speed-delta-amount", 3);
        addConfigurationValue("max-batched-movement-count", 3);
        addConfigurationValue("min-on-ground-time", 5);
        addConfigurationValue("min-off-modifier-time", 3);
        addConfigurationValue("last-teleport-time-max", 1000);
        addConfigurationValue("last-teleport-time-speed-increase", 1.0);
        addConfigurationValue("max-allowed-fast-movements", 3);

        if (enabled()) load();
    }

    /**
     * Check
     *
     * @param player the player
     * @param data   their data
     */
    public void check(Player player, MovingData data) {
        if (exempt(player) || exempt(player, ExemptionType.TELEPORT)) return;
        startTiming(player);

        if (data.getSafeSpeedLocation() == null) {
            data.setSafeSpeedLocation(data.from());
        }

        final Location safe = data.getSafeSpeedLocation();
        final Location from = data.from();
        final Location to = data.to();

        data.setLastHorizontal(data.getHorizontal());
        final double horizontal = MathUtil.horizontal(from, to);
        data.setHorizontal(horizontal);

        final double vertical = data.vertical();
        double base = getBaseMoveSpeed(player);
        // TODO: Most likely can be abused.
        if (System.currentTimeMillis() - data.getLastTeleport()
                <= lastTeleportTimeMax) {
            base += lastTeleportTimeSpeedIncrease;
        }

        final CheckResult result = new CheckResult();

        // Start running checks.
        // Only run the checks if we are already moving faster.
        // But of-course this doesn't always mean cheating.
        if (horizontal > base) {
            if (data.onGround() && data.onGroundTime() > minOnGroundTime) {
                // player is on-ground for long enough, run checks.
                runOnGroundChecks(player, data, result, to, safe, horizontal, base, vertical);
            } else {
                if (data.ascending()) {
                    data.setLastLiftOff(System.currentTimeMillis());
                }
                // player is not on-ground
            }
        }

        // cancel large movements.
        if (horizontal >= maxLargeDistanceMovement) {
            result.setFailed("Large movement")
                    .withParameter("h", horizontal)
                    .withParameter("max", maxLargeDistanceMovement);
            handleCheckViolationAndReset(player, result, safe);
        }

        // Update players 'safe' location here, only if they are on-ground and haven't failed before.
        // This sets-back the player farther and is more effective at being annoying and stopping them.
        if (!result.hasFailedBefore() && data.onGround() && MathUtil.horizontal(to, safe) >= 5) {
            data.setSafeSpeedLocation(from);
        }

        stopTiming(player);
    }

    /**
     * Run checks that check the speed of the player while they are on-ground
     *
     * @param player     the player
     * @param data       their data
     * @param result     the result
     * @param to         the to
     * @param safe       the safe location
     * @param horizontal the horizontal speed
     * @param base       the base speed
     * @param vertical   their vertical speed
     */
    private void runOnGroundChecks(Player player, MovingData data, CheckResult result, Location to, Location safe, double horizontal, double base, double vertical) {
        final boolean hasBlockAboveHead = BlockAccess.getBlockAt(to, to.level, 0.0, 2.1, 0.0).isSolid();
        if (hasBlockAboveHead) {
            handleBlockAbovePlayersHead(player, data, result, safe, horizontal, base, vertical);
        } else {
            if (!handleSlabAndStairMovement(player, data, result, to, safe, horizontal)) {
                // player is not on slabs or stairs, normal check.
                if (data.getOffModifierCooldown() >= minOffModifierTime) {
                    handlePlayerOnGroundNoBoost(player, data, result, safe, horizontal, base, vertical);
                }
            }
        }
    }

    /**
     * Player is on the ground, but has no boost.
     *
     * @param player     the player
     * @param data       the data
     * @param result     the result
     * @param safe       the safe
     * @param horizontal the horizontal
     * @param base       the base
     */
    private void handlePlayerOnGroundNoBoost(Player player, MovingData data, CheckResult result, Location safe, double horizontal, double base, double vertical) {
        // check if player movement was batched together.
        final double mod = ((base * 2) - horizontal);
        final boolean maybeBatched = mod > 0.0 && mod < 0.08;

        if (maybeBatched) {
            final int movementCount = data.getBatchedMovementCount() + 1;
            data.setBatchedMovementCount(movementCount);
            if (movementCount >= maxBatchedMovementCount) {
                result.setFailed("Horizontal speed greater than base speed")
                        .withParameter("batched", "yes")
                        .withParameter("count", movementCount)
                        .withParameter("max", maxBatchedMovementCount)
                        .withParameter("h", horizontal)
                        .withParameter("base", base);
                handleCheckViolationAndReset(player, result, safe);
            }
        } else {
            data.setBatchedMovementCount(0);
            if (vertical > 0.0) {
                data.setLastLiftOff(System.currentTimeMillis());
                // player has lift-off
                handlePlayerLiftOff(player, data, result, safe, horizontal, base, vertical);
            } else {
                // Workaround for boost after jumping
                final boolean slowingDown = data.getLastHorizontal() > horizontal;
                if (slowingDown) {
                    final long lastLiftOffDelta = System.currentTimeMillis() - data.getLastLiftOff();
                    if (lastLiftOffDelta <= 1000) {
                        data.onGroundTime(2);
                        return;
                    }
                }

                if (horizontal > base) {
                    final int count = data.getFastMovements() + 1;
                    data.setFastMovements(count);

                    if (count > maxAllowedFastMovements) {
                        result.setFailed("Horizontal speed greater than base speed")
                                .withParameter("batched", "no")
                                .withParameter("h", horizontal)
                                .withParameter("base", base)
                                .withParameter("time", data.onGroundTime())
                                .withParameter("count", count)
                                .withParameter("max", maxAllowedFastMovements);
                        handleCheckViolationAndReset(player, result, safe);
                    }
                } else {
                    data.setFastMovements(0);
                }
            }
        }

    }

    private void handlePlayerLiftOff(Player player, MovingData data, CheckResult result, Location safe, double horizontal, double base, double vertical) {

    }

    /**
     * Handle when a player has a block above their head.
     *
     * @param player     the player
     * @param data       their data
     * @param result     the result
     * @param safe       the safe location
     * @param horizontal the horizontal speed
     * @param base       the base speed
     * @param vertical   their vertical speed
     */
    private void handleBlockAbovePlayersHead(Player player, MovingData data, CheckResult result, Location safe, double horizontal, double base, double vertical) {
        if (vertical == 0.0 || vertical <= lowJumpMin) {
            // player is not moving vertically at-all, which means no speed boost.
            handlePlayerOnGroundNoBoost(player, data, result, safe, horizontal, base, vertical);
        } else if (vertical < lowJumpMax) {
            // reset batched movements
            data.setBatchedMovementCount(0);
            // player is moving vertically.
        }
    }

    /**
     * Handle player movement checking when they are on a slab or stair.
     *
     * @param player     the player
     * @param data       their data
     * @param result     the result
     * @param to         the to
     * @param safe       the safe setback
     * @param horizontal the hDist
     */
    private boolean handleSlabAndStairMovement(Player player, MovingData data, CheckResult result, Location to, Location safe, double horizontal) {
        final boolean hasSlab = BlockAccess.hasSlabAt(to, to.level, 0.3, -0.5, 0.3);
        final boolean hasStair = BlockAccess.hasStairAt(to, to.level, 0.3, -0.5, 0.3);
        if (!hasSlab && !hasStair) {
            data.setOffModifierCooldown(data.getOffModifierCooldown() + 1);
            return false;
        } else {
            data.setOffModifierCooldown(0);
        }

        final double last = data.getLastHorizontal();
        if (hasStair) handleStairMovement(player, data, result, safe, horizontal, last);
        if (hasSlab) handleSlabMovement(player, data, result, safe, horizontal, last);
        return false;
    }

    /**
     * Handle stair related movement.
     * Check if players are moving too fast while going upstairs.
     *
     * @param player     the player
     * @param data       their data
     * @param result     the result
     * @param safe       the safe setback
     * @param horizontal the hDist
     * @param last       the last hDist
     */
    private void handleStairMovement(Player player, MovingData data, CheckResult result, Location safe, double horizontal, double last) {
        // Check if player has had a consistent move speed the whole time up the stairs.
        final double delta = Math.abs(horizontal - last);
        if (delta <= maxStairMovementSpeedDelta) {
            final int deltaAmount = data.getStairMovementDeltaAmount() + 1;
            data.setStairMovementDeltaAmount(deltaAmount);
            if (deltaAmount > maxStairMovementSpeedDeltaAmount) {
                result.setFailed("Movement too similar while moving on stairs.")
                        .withParameter("delta", delta)
                        .withParameter("max", maxStairMovementSpeedDelta)
                        .withParameter("amt", deltaAmount)
                        .withParameter("max", maxStairMovementSpeedDeltaAmount);
                handleCheckViolationAndReset(player, result, safe);
            }
        } else {
            data.setStairMovementDeltaAmount(0);
        }

        // Otherwise, check if player has passed the hard limit set.
        if (horizontal > maxStairMovementSpeed) {
            final int count = data.getStairMovementOverThresholdCount() + 1;
            data.setStairMovementOverThresholdCount(count);
            if (count > stairMovementSpeedThreshold) {
                result.setFailed("Moving too fast on stairs")
                        .withParameter("h", horizontal)
                        .withParameter("max", maxStairMovementSpeed)
                        .withParameter("count", count)
                        .withParameter("threshold", stairMovementSpeedThreshold);
                handleCheckViolationAndReset(player, result, safe);
            }
        } else {
            data.setStairMovementOverThresholdCount(0);
        }
    }

    /**
     * Handle slab related movement.
     * Check if players are moving too fast while going up slabs.
     *
     * @param player     the player
     * @param data       their data
     * @param result     the result
     * @param safe       the safe setback
     * @param horizontal the hDist
     * @param last       the last hDist
     */
    private void handleSlabMovement(Player player, MovingData data, CheckResult result, Location safe, double horizontal, double last) {
        if (data.ascending()) {
            final double delta = Math.abs(horizontal - last);
            if (delta <= maxSlabMovementSpeedDelta) {
                final int deltaAmount = data.getSlabMovementDeltaAmount() + 1;
                data.setSlabMovementDeltaAmount(deltaAmount);
                if (deltaAmount > maxSlabMovementSpeedDeltaAmount) {
                    result.setFailed("Movement too similar while moving on slabs.")
                            .withParameter("delta", delta)
                            .withParameter("max", maxSlabMovementSpeedDelta)
                            .withParameter("amt", deltaAmount)
                            .withParameter("max", maxSlabMovementSpeedDeltaAmount);
                    handleCheckViolationAndReset(player, result, safe);
                }
            } else {
                data.setSlabMovementDeltaAmount(0);
            }

            if (horizontal > maxSlabMovementSpeed) {
                final int count = data.getSlabMovementOverThresholdCount() + 1;
                data.setSlabMovementOverThresholdCount(count);
                if (count > slabMovementSpeedThreshold) {
                    result.setFailed("Moving too fast on slabs")
                            .withParameter("h", horizontal)
                            .withParameter("max", maxSlabMovementSpeed)
                            .withParameter("count", count)
                            .withParameter("threshold", slabMovementSpeedThreshold);
                    handleCheckViolationAndReset(player, result, safe);
                }
            } else {
                data.setSlabMovementOverThresholdCount(0);
            }
        }
    }

    /**
     * Return our base move speed.
     *
     * @param player the player
     * @return players move speed
     */
    private double getBaseMoveSpeed(Player player) {
        double baseSpeed = 0.289;

        for (Effect effect : player.getEffects().values()) {
            if (effect.getName().equalsIgnoreCase(NukkitAccess.MOVE_SPEED_POTION)) {
                baseSpeed *= 1.0 + 0.2 * effect.getAmplifier() + 1;
            }
        }
        return baseSpeed;
    }

    @Override
    public void reloadConfig() {
        load();
    }

    @Override
    public void load() {
        maxLargeDistanceMovement = configuration.getDouble("max-large-distance-movement");
        lowJumpMax = configuration.getDouble("low-jump-max");
        lowJumpMin = configuration.getDouble("low-jump-min");
        maxStairMovementSpeed = configuration.getDouble("max-stair-movement-speed");
        maxStairMovementSpeedDelta = configuration.getDouble("max-stair-movement-speed-delta");
        maxStairMovementSpeedDeltaAmount = configuration.getInt("max-stair-movement-speed-delta-amount");
        maxSlabMovementSpeed = configuration.getDouble("max-slab-movement-speed");
        maxSlabMovementSpeedDelta = configuration.getDouble("max-slab-movement-speed-delta");
        maxSlabMovementSpeedDeltaAmount = configuration.getInt("max-slab-movement-speed-delta-amount");
        maxBatchedMovementCount = configuration.getInt("max-batched-movement-count");
        minOnGroundTime = configuration.getInt("min-on-ground-time");
        minOffModifierTime = configuration.getInt("min-off-modifier-time");
        lastTeleportTimeMax = configuration.getLong("last-teleport-time-max");
        lastTeleportTimeSpeedIncrease = configuration.getDouble("last-teleport-time-speed-increase");
        maxAllowedFastMovements = configuration.getInt("max-allowed-fast-movements");
        stairMovementSpeedThreshold = configuration.getInt("stair-movement-speed-threshold");
        slabMovementSpeedThreshold = configuration.getInt("slab-movement-speed-threshold");

        CheckTimings.registerTiming(checkType);
    }
}
