package me.vrekt.arc.check.moving;

import cn.nukkit.Player;
import cn.nukkit.block.BlockCobweb;
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
     * The base move speeds.
     * Max initial jump boost given to the player.
     */
    private double baseMoveSpeedWalk, baseMoveSpeedSprint, maxInitialJumpBoost;

    /**
     * Max move speed in webs
     * <p>
     * Low jump min + max with block above head.
     * Max speed on ice + block above head.
     * Min delta to flag when moving too similar.
     */
    private double maxMoveSpeedWeb, blockLowJumpMin, blockLowJumpMax, blockIceMaxSpeed, blockIceDeltaMin;

    /**
     * Max move speed on stairs
     * In air min delta
     * <p>
     * Various max speed(s)
     */
    private double maxMoveSpeedStairs, inAirMinDelta, maxInAirSpeed, maxInAirIceSpeed, maxInAirSlimeblockSpeed;

    /**
     * The minimum time required to be on ground.
     * Max amount of times delta can be flagged when moving on ice + block above head
     * Max delta amount
     */
    private int minimumOnGroundTime, blockIceDeltaAmountMax, inAirMaxDeltaAmount;

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

        addConfigurationValue("base-move-speed-sprint", 0.289);
        addConfigurationValue("base-move-speed-walk", 0.289);
        addConfigurationValue("minimum-on-ground-time", 10);
        addConfigurationValue("max-initial-jump-boost", 0.5);
        addConfigurationValue("max-move-speed-web", 0.099);
        addConfigurationValue("block-low-jump-min", 0.07);
        addConfigurationValue("block-low-jump-max", 0.2);
        addConfigurationValue("block-ice-speed-max", 1.2);
        addConfigurationValue("block-ice-delta-min", 0.02);
        addConfigurationValue("block-ice-delta-amount-max", 5);
        addConfigurationValue("max-move-speed-stairs", 0.6);
        addConfigurationValue("in-air-min-delta", 0.01);
        addConfigurationValue("in-air-max-delta-amount", 10);
        addConfigurationValue("max-in-air-speed", 0.62);
        addConfigurationValue("max-in-air-ice-speed", 0.56);
        addConfigurationValue("max-in-air-slimeblock-speed", 0.45);

        if (enabled()) load();
    }

    /**
     * Check
     *
     * @param player the player
     * @param data   their data
     */
    public void check(Player player, MovingData data) {
        if (exempt(player) || exempt(player, ExemptionType.TELEPORT) || player.riding != null) return;
        startTiming(player);

        if (data.getSafeSpeedLocation() == null) {
            data.setSafeSpeedLocation(data.from());
        }

        final Location setback = data.getSafeSpeedLocation();
        final Location from = data.from();
        final Location to = data.to();

        final CheckResult result = new CheckResult();

        final double lastHorizontal = data.getHorizontal();
        final double vertical = data.vertical();
        final double horizontal = MathUtil.horizontal(from, to);
        data.setHorizontal(horizontal);

        final double base = getBaseMoveSpeed(player);
        data.setLastHorizontal(lastHorizontal);

        // Player is on-ground check normal speeds and stairs and stuff.
        if (data.onGround() && data.onGroundTime() >= minimumOnGroundTime) {
            runGroundChecks(player, data, result, from, to, setback, horizontal, base, vertical);
        }

        // Player is not on ground, mostly fix b-hop type speeds.
        if (!data.onGround()) {
            runAirChecks(player, data, result, setback, horizontal);
        }

        stopTiming(player);
    }

    /**
     * First implementation of a basic in-air check.
     * <p>
     * This will cover most b-hop aspects, as-well as ignore ice and slimeblock modifiers.
     * TODO
     *
     * @param player     the player
     * @param data       their data
     * @param result     the result
     * @param setback    their setback
     * @param horizontal the horizontal speed
     */
    private void runAirChecks(Player player, MovingData data, CheckResult result, Location setback, double horizontal) {
        // check if the player is moving too similar over time
        final double delta = Math.abs(data.getLastHorizontal() - horizontal);
        if (delta <= inAirMinDelta) {
            // ignore cases where we are super far from ground.
            // TODO: This can cause bypasses.
            final double distance = MathUtil.distance(data.ground(), data.to());
            if (distance <= 2.8) {
                final int count = data.getInAirDeltaAmount() + 1;
                data.setInAirDeltaAmount(count);

                if (count > inAirMaxDeltaAmount) {
                    result.setFailed("Moving too similar in-air")
                            .withParameter("delta", delta)
                            .withParameter("min", inAirMinDelta)
                            .withParameter("count", count)
                            .withParameter("max", inAirMaxDeltaAmount);
                    handleCheckViolationAndReset(player, result, setback);
                }
            }
        } else {
            data.setInAirDeltaAmount(0);
        }

        if (horizontal >= maxInAirSpeed) {
            result.setFailed("Moving too fast in-air")
                    .withParameter("h", horizontal)
                    .withParameter("max", maxInAirSpeed);
            handleCheckViolationAndReset(player, result, setback);
        }

        if (data.onIce() && horizontal >= maxInAirIceSpeed) {
            result.setFailed("Moving too fast in-air on-ice")
                    .withParameter("h", horizontal)
                    .withParameter("max", maxInAirIceSpeed);
            handleCheckViolationAndReset(player, result, setback);
        }

        if (data.hasSlimeblock() && horizontal >= maxInAirSlimeblockSpeed) {
            result.setFailed("Moving too fast in-air on-slimeblock")
                    .withParameter("h", horizontal)
                    .withParameter("max", maxInAirSlimeblockSpeed);
            handleCheckViolationAndReset(player, result, setback);
        }
    }

    /**
     * Run checks while the player is on the ground
     * TODO
     *
     * @param player     the player
     * @param data       their data
     * @param result     the result
     * @param from       the from
     * @param to         the to
     * @param setback    their setback
     * @param horizontal the horizontal speed
     * @param base       the base speed
     * @param vertical   their vertical speed
     */
    private void runGroundChecks(Player player, MovingData data, CheckResult result, Location from, Location to, Location setback, double horizontal, double base, double vertical) {
        final boolean hasModifier = BlockAccess.hasVerticalModifierAt(to, to.level, 0.3, -0.1, 0.3);
        if (!hasModifier) {
            data.setOffModifierTime(data.getOffModifierTime() + 1);
        } else {
            data.setOffModifierTime(0);
        }

        // player could be lifting off, which gives them a speed boost.
        if (vertical >= 0.40) {
            // player has lift-off phase
            if (horizontal > maxInitialJumpBoost && data.offIceTime() >= 10 && data.getOffModifierTime() >= 10) {
                result.setFailed("Moving too fast on initial lift-off")
                        .withParameter("h", horizontal)
                        .withParameter("max", maxInitialJumpBoost)
                        .withParameter("offIceTime", data.offIceTime())
                        .withParameter("modTime", data.getOffModifierTime());
                handleCheckViolationAndReset(player, result, from);
            }
        } else {
            // player has no significant lift off.
            if (horizontal > base) {
                // check low jump ice movement
                if (data.onIce() && vertical > blockLowJumpMin && vertical < blockLowJumpMax) {
                    checkLowJumpMovement(player, data, result, to, setback, horizontal);
                } else {
                    // check for stairs.
                    if (BlockAccess.hasStairAt(to, to.level, 0.3, -0.1, 0.3) && vertical > 0.0) {
                        // player has boost when jumping up stairs.
                        if (horizontal > maxMoveSpeedStairs) {
                            result.setFailed("Moving too fast on stairs")
                                    .withParameter("h", horizontal)
                                    .withParameter("max", maxMoveSpeedStairs);
                            handleCheckViolationAndReset(player, result, from);
                        }
                    } else {
                        // regular flag.
                        // ensure player has had no ice recently.
                        if (data.offIceTime() >= 10 && data.getOffModifierTime() >= 10) {
                            result.setFailed("Moving too fast")
                                    .withParameter("h", horizontal)
                                    .withParameter("max", base)
                                    .withParameter("offIceTime", data.offIceTime())
                                    .withParameter("offModTime", data.getOffModifierTime());
                            handleCheckViolationAndReset(player, result, from);
                        }
                    }
                }
            } else {
                // check for web-movement.
                final boolean inWeb = to.getLevelBlock() instanceof BlockCobweb;
                if (inWeb) {
                    if (horizontal > maxMoveSpeedWeb) {
                        result.setFailed("Moving too fast in cob-web")
                                .withParameter("h", horizontal)
                                .withParameter("max", maxMoveSpeedWeb);
                        handleCheckViolationAndReset(player, result, from);
                    }
                }
            }
        }
    }

    /**
     * Checks low jump movement when a player has a block above their head.
     * TODO
     *
     * @param player     the player
     * @param data       their data
     * @param result     the result
     * @param to         the to
     * @param setback    their setback
     * @param horizontal the horizontal speed
     */
    private void checkLowJumpMovement(Player player, MovingData data, CheckResult result, Location to, Location setback, double horizontal) {
        // check if player has block above head.
        final boolean hasBlockAboveHead = BlockAccess.getBlockAt(to, to.level, 0.3, 2, 0.3).isSolid();
        if (hasBlockAboveHead) {
            // player would have a boost here.
            final double delta = Math.abs(data.getLastHorizontal() - horizontal);
            if (delta < blockIceDeltaMin) {
                final int count = data.getBlockIceDeltaAmount() + 1;
                data.setBlockIceDeltaAmount(count);

                // this checks if the player is moving too similar.
                if (count > blockIceDeltaAmountMax) {
                    result.setFailed("Moving too similar on ice")
                            .withParameter("delta", delta)
                            .withParameter("min", blockIceDeltaMin)
                            .withParameter("count", count)
                            .withParameter("max", blockIceDeltaAmountMax);
                    handleCheckViolationAndReset(player, result, setback);
                }

                if (horizontal > blockIceMaxSpeed) {
                    result.setFailed("Moving too fast on ice")
                            .withParameter("h", horizontal)
                            .withParameter("max", blockIceMaxSpeed);
                    handleCheckViolationAndReset(player, result, setback);
                }

            } else {
                data.setBlockIceDeltaAmount(0);
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
        double baseSpeed = player.isSprinting() ? baseMoveSpeedSprint : baseMoveSpeedWalk;

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
        baseMoveSpeedSprint = configuration.getDouble("base-move-speed-sprint");
        baseMoveSpeedWalk = configuration.getDouble("base-move-speed-walk");
        minimumOnGroundTime = configuration.getInt("minimum-on-ground-time");
        maxInitialJumpBoost = configuration.getDouble("max-initial-jump-boost");
        maxMoveSpeedWeb = configuration.getDouble("max-move-speed-web");
        blockLowJumpMin = configuration.getDouble("block-low-jump-min");
        blockLowJumpMax = configuration.getDouble("block-low-jump-max");
        blockIceDeltaAmountMax = configuration.getInt("block-ice-delta-amount-max");
        blockIceDeltaMin = configuration.getDouble("block-ice-delta-min");
        blockIceMaxSpeed = configuration.getDouble("block-ice-speed-max");
        maxMoveSpeedStairs = configuration.getDouble("max-move-speed-stairs");
        inAirMinDelta = configuration.getDouble("in-air-min-delta");
        inAirMaxDeltaAmount = configuration.getInt("in-air-max-delta-amount");
        maxInAirSpeed = configuration.getDouble("max-in-air-speed");
        maxInAirIceSpeed = configuration.getDouble("max-in-air-ice-speed");
        maxInAirSlimeblockSpeed = configuration.getDouble("max-in-air-slimeblock-speed");

        CheckTimings.registerTiming(checkType);
    }
}
