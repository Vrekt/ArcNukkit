package me.vrekt.arc.check.moving;

import cn.nukkit.Player;
import cn.nukkit.level.Location;
import cn.nukkit.potion.Effect;
import me.vrekt.arc.check.Check;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.check.moving.configuration.MovingSpeedConfig;
import me.vrekt.arc.check.result.CheckResult;
import me.vrekt.arc.check.result.Parameter;
import me.vrekt.arc.compatibility.NukkitAccess;
import me.vrekt.arc.data.moving.MovingData;
import me.vrekt.arc.exemption.type.ExemptionType;
import me.vrekt.arc.timings.CheckTimings;
import me.vrekt.arc.utility.block.BlockAccess;
import me.vrekt.arc.utility.math.MathUtil;

/**
 * Checks if the player is moving too fast.
 */
public final class Speed extends Check {

    /**
     * The speed configuration.
     */
    private final MovingSpeedConfig cc;

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

        cc = new MovingSpeedConfig();
        cc.write(configuration);

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

        if (data.getSafeSpeedLocation() == null) data.setSpeedSetback(data.from());

        final Location setback = data.getSafeSpeedLocation();
        final Location from = data.from();
        final Location to = data.to();

        final CheckResult result = new CheckResult();
        final double vertical = data.vertical();
        final double horizontal = data.getHorizontal();
        final double base = getBaseMoveSpeed(player);

        // collect pre-data needed.
        final boolean hasStair = data.onGround() && BlockAccess.hasStairAt(to, to.level, 0.3, -0.1, 0.3);
        final boolean hasSlab = data.onGround() && !hasStair && BlockAccess.hasSlabAt(to, to.level, 0.3, -0.1, 0.3);

        // Workaround: don't flag when on slabs
        if (hasStair || hasSlab) {
            data.setOffModifierTime(0);
        } else {
            data.setOffModifierTime(data.getOffModifierTime() + 1);
        }

        // Player is on-ground check normal speeds and stairs and stuff.
        if (data.onGround() && data.onGroundTime() >= cc.minimumOnGroundTime && !data.inLiquid()) {
            runOnGroundChecks(player, data, result, setback, to, horizontal, vertical, base, hasStair);
        }

        // If player has not failed before and meets configuration criteria, update.
        // TODO: Could possibly be abused, so maybe implement a hard limit where it HAS to update.
        if (!result.hasFailedBefore()
                && (System.currentTimeMillis() - getLastViolation(player)) >= cc.timeRequiredSinceLastViolation
                && MathUtil.distance(setback, to) >= cc.minSetbackDistance) {
            data.setSpeedSetback(from);
        }

        stopTiming(player);
    }

    /**
     * Run ground checks depending on blocks and current state.
     *
     * @param player     the player
     * @param data       their data
     * @param result     the result
     * @param setback    the setback location
     * @param to         to the current location
     * @param horizontal horizontal speed
     * @param vertical   vertical speed
     * @param base       base speed
     * @param hasStair   if the player has stair below them
     */
    private void runOnGroundChecks(Player player, MovingData data, CheckResult result, Location setback,
                                   Location to, double horizontal, double vertical, double base, boolean hasStair) {
        final boolean isOnIce = data.onIce() || data.offIceTime() <= cc.minimumOffIceTime;
        final boolean hasBlockAboveHead = BlockAccess.hasSolidBlockAt(to, to.level, 0.3, 2, 0.3);

        // update vertical boost timer here before running player checks.
        data.setNoVerticalBoost(vertical > cc.minimumVertical ? 0 : data.getNoVerticalBoost() + 1);
        if (isOnIce) {
            runGroundIceCheck(player, data, result, setback, horizontal, vertical, base, hasBlockAboveHead);
        } else {
            runNormalGroundCheck(player, data, result, setback, horizontal, vertical, base, hasBlockAboveHead, hasStair);
        }
    }

    /**
     * Run checks for when the player is on ground with ice below them.
     *
     * @param player            the player
     * @param data              their data
     * @param result            the result
     * @param setback           the setback location
     * @param horizontal        horizontal speed
     * @param vertical          vertical speed
     * @param base              base speed
     * @param hasBlockAboveHead if there is a block above the players head
     */
    private void runGroundIceCheck(Player player, MovingData data, CheckResult result, Location setback,
                                   double horizontal, double vertical, double base, boolean hasBlockAboveHead) {
        if (hasBlockAboveHead) {
            // Give a proper cooldown for players who accumulate lots of speed.
            // This prevents false flags when there is still left over velocity.
            // TODO: We want a better system implementing some type of "ice slipperiness"
            final double maxCooldown = data.getMaxIceSpeedReached() >= cc.maxIceSpeedReachedMin
                    ? (data.getMaxIceSpeedReached() + 1) * cc.maxIceSpeedReachedCooldownModifier
                    : data.getMaxIceSpeedReached() == 0 ? 2 : cc.maxIceSpeedReachedCooldown;

            if (vertical < cc.minimumVertical && data.getNoVerticalBoost() >= maxCooldown) {
                // reset max speed reached.
                data.setMaxIceSpeedReached(0);

                // player is not jump boosting, so by default, ice does not give any speed boost.
                if (checkPossibleViolation(horizontal, base))
                    populateViolationResult(player, result, setback, horizontal, base,
                            Parameter.of("tags", "ground_ice_block_no_boost"),
                            Parameter.of("vertical", vertical),
                            Parameter.of("max", cc.minimumVertical),
                            Parameter.of("boost", data.getNoVerticalBoost()),
                            Parameter.of("min", maxCooldown));
            } else {
                // keep track of how fast the player reached.
                if (horizontal > data.getMaxIceSpeedReached())
                    data.setMaxIceSpeedReached(Math.min(horizontal, cc.blockIceMaxSpeed));

                if (checkPossibleViolation(horizontal, cc.blockIceMaxSpeed))
                    populateViolationResult(player, result, setback, horizontal, cc.blockIceMaxSpeed,
                            Parameter.of("tags", "ground_ice_block_boost"),
                            Parameter.of("vertical", vertical),
                            Parameter.of("min", cc.minimumVertical),
                            Parameter.of("boost", data.getNoVerticalBoost()),
                            Parameter.of("min", maxCooldown));
            }
        } else {
            // reset max speed reached.
            data.setMaxIceSpeedReached(0);
        }
    }

    /**
     * Run normal ground checks, as in player is not on ice, slime-blocks, or anything considered special.
     *
     * @param player            the player
     * @param data              their data
     * @param result            the result
     * @param setback           the setback location
     * @param horizontal        horizontal speed
     * @param vertical          vertical speed
     * @param base              base speed
     * @param hasBlockAboveHead if there is a block above the players head
     * @param hasStair          if the player has a stair below them.
     */
    private void runNormalGroundCheck(Player player, MovingData data, CheckResult result, Location setback,
                                      double horizontal, double vertical, double base, boolean hasBlockAboveHead, boolean hasStair) {

        if (hasBlockAboveHead) {
            // Give a proper cooldown for players who accumulate lots of speed, while low jumping with a block above their head.
            // This prevents false flags when there is still left over velocity.
            final double maxCooldown = data.getMaxLowJumpSpeedReached() >= cc.maxLowJumpSpeedReachedMin
                    ? (data.getMaxLowJumpSpeedReached() + 1) * cc.maxLowJumpSpeedReachedCooldownModifier
                    : data.getMaxLowJumpSpeedReached() == 0 ? 2 : cc.maxLowJumpSpeedReachedCooldown;

            // again, account for jump boosting.
            if (vertical < cc.minimumVertical
                    && data.getNoVerticalBoost() >= maxCooldown) {
                data.setMaxLowJumpSpeedReached(0);

                if (checkPossibleViolation(horizontal, base))
                    populateViolationResult(player, result, setback, horizontal, base,
                            Parameter.of("tags", "ground_block"),
                            Parameter.of("vertical", vertical),
                            Parameter.of("max", cc.minimumVertical),
                            Parameter.of("boost", data.getNoVerticalBoost()),
                            Parameter.of("min", maxCooldown));
            } else {
                // set last time player achieved a possible low-jump boost.
                data.setLastLowJumpBoost(System.currentTimeMillis());

                // keep track of how fast the player reached.
                if (horizontal > data.getMaxLowJumpSpeedReached())
                    data.setMaxLowJumpSpeedReached(Math.min(horizontal, cc.maxBlockLowJumpSpeed));

                if (checkPossibleViolation(horizontal, cc.maxBlockLowJumpSpeed))
                    populateViolationResult(player, result, setback, horizontal, cc.maxBlockLowJumpSpeed,
                            Parameter.of("tags", "ground_vertical_block"),
                            Parameter.of("vertical", vertical),
                            Parameter.of("min", cc.minimumVertical),
                            Parameter.of("boost", data.getNoVerticalBoost()),
                            Parameter.of("min", maxCooldown));
            }
        } else {
            data.setMaxLowJumpSpeedReached(0);
            if (vertical > 0.41) {
                // player had an initial jump boost, ensure they didn't gain too much speed on lift-off.
                // TODO: In the future, we may want lift off phases/jump phases.

                if (checkPossibleViolation(horizontal, cc.maxInitialJumpBoost))
                    populateViolationResult(player, result, setback, horizontal, cc.maxInitialJumpBoost,
                            Parameter.of("tags", "ground_initial_jump_boost"),
                            Parameter.of("vertical", vertical),
                            Parameter.of("min", 0.41));
            } else if ((System.currentTimeMillis() - data.getLastLowJumpBoost()) >= cc.timeRequiredSinceLastJumpBoost
                    && data.offIceTime() >= cc.minimumOffIceTime) {
                // otherwise, ensure no recent boost from block above head + ice

                // TODO: Check if player has actual vertical velocity while going up stairs.
                // TODO: This could allow enforcing move speed when normally going up vs jumping and going up.
                if (hasStair) {
                    if (checkPossibleViolation(horizontal, cc.maxMoveSpeedStairs))
                        populateViolationResult(player, result, setback, horizontal, cc.maxMoveSpeedStairs,
                                Parameter.of("tags", "ground_stairs"),
                                Parameter.of("lastJumpBoostTimeRequired", cc.timeRequiredSinceLastJumpBoost),
                                Parameter.of("offIceTime", data.offIceTime()),
                                Parameter.of("offIceTimeRequired", cc.minimumOffIceTime));
                } else {
                    // TODO: Possible bypass with data.getNoVerticalBoost()
                    if (data.getOffModifierTime() > cc.minimumOffModifierTime
                            && data.getNoVerticalBoost() <= cc.noVerticalBoostRequired
                            && checkPossibleViolation(horizontal, base))
                        populateViolationResult(player, result, setback, horizontal, base,
                                Parameter.of("tags", "normal"),
                                Parameter.of("lastJumpBoostTimeRequired", cc.timeRequiredSinceLastJumpBoost),
                                Parameter.of("offIceTime", data.offIceTime()),
                                Parameter.of("offIceTimeRequired", cc.minimumOffIceTime),
                                Parameter.of("modTime", data.getOffModifierTime()),
                                Parameter.of("offModTimeRequired", cc.minimumOffModifierTime),
                                Parameter.of("noVerticalBoostRequired", cc.noVerticalBoostRequired));
                }
            }
        }
    }

    /**
     * Check if there is a possible violation.
     * This is to reduce general pollution when checking.
     * So instead of always allocating strings even if we didn't fail.
     *
     * @param horizontal the horizontal speed
     * @param baseSpeed  the max speed
     * @return {@code true} if {@code horizontal} >= {@code baseSpeed}
     */
    private boolean checkPossibleViolation(double horizontal, double baseSpeed) {
        return horizontal >= baseSpeed;
    }

    /**
     * Populate the check result and handle it.
     *
     * @param player  the player
     * @param result  the result
     * @param setback the setback
     * @param tags    the parameter tags
     */
    private void populateViolationResult(Player player, CheckResult result, Location setback, double horizontal,
                                         double baseSpeed, Parameter... tags) {
        result.setFailed("Horizontal speed greater than base speed allowed")
                .withParameter("horizontal", horizontal)
                .withParameter("baseSpeed", baseSpeed)
                .withParameters(tags);
        handleCheckViolationAndReset(player, result, setback);
    }

    /**
     * Return our base move speed.
     *
     * @param player the player
     * @return players move speed
     */
    private double getBaseMoveSpeed(Player player) {
        double baseSpeed = cc.baseMoveSpeedSprint;

        for (Effect effect : player.getEffects().values()) {
            if (effect.getName().equalsIgnoreCase(NukkitAccess.MOVE_SPEED_POTION)) {
                baseSpeed *= 1.0 + 0.2 * effect.getAmplifier() + 1;
            }
        }
        return baseSpeed;
    }

    @Override
    public void reloadConfig() {
        cc.load(configuration);
    }

    @Override
    public void load() {
        cc.load(configuration);
        CheckTimings.registerTiming(checkType);
    }

}
