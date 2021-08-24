package me.vrekt.arc.check.moving;

import cn.nukkit.Player;
import cn.nukkit.level.Location;
import me.vrekt.arc.check.Check;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.check.result.CheckResult;
import me.vrekt.arc.data.moving.MovingData;
import me.vrekt.arc.utility.MovingAccess;

/**
 * Checks if the player is walking on liquids like lava and water.
 */
public final class LiquidWalk extends Check {

    /**
     * The time in liquid required to start checking.
     * The max amount of times a player cannot be moving vertically in liquid.
     * The max amount of small vertical movements allowed.
     */
    private int timeInLiquidRequired, maxNoVerticalCount, maxSmallMovementVerticalCount;

    /**
     * The min vertical ascend possible.
     * The max setback distance.
     */
    private double minVerticalAscend, maxSetbackDistance;

    /**
     * The minimum time after swimming to start checking
     * TODO: This can be abused.
     */
    private long minSwimTime;

    public LiquidWalk() {
        super(CheckType.LIQUID_WALK);
        enabled(true)
                .cancel(true)
                .cancelLevel(0)
                .notify(true)
                .notifyEvery(1)
                .ban(false)
                .kick(false)
                .build();

        addConfigurationValueWithComment("time-in-liquid-required", 3,
                "The time in liquid required before checking.");
        addConfigurationValueWithComment("max-no-vertical-count", 5,
                "The max amount of times a player cannot be vertically moving in liquid.");
        addConfigurationValueWithComment("min-vertical-ascend", 0.1,
                "The minimum vertical ascend speed possible.");
        addConfigurationValueWithComment("max-small-vertical-movement-count", 5,
                "The max amount of times a player can barely move.");
        addConfigurationValueWithComment("max-setback-distance", 2,
                "The max setback distance allowed.");
        addConfigurationValueWithComment("min-swim-time", 1000,
                "The minimum time required from the last time a player was swimming to check.");

        if (enabled()) load();
    }

    /**
     * Check this player
     *
     * @param player the player
     * @param data   their data
     */
    public void check(Player player, MovingData data) {
        // TODO: Possible bypass with these flags set.
        if (exempt(player) || player.isGliding()) return;
        if (player.isSwimming()) {
            data.setLastSwimTime(System.currentTimeMillis());
            return;
        } else if (System.currentTimeMillis() - data.getLastSwimTime() <= minSwimTime) return;

        final Location to = data.to();
        final boolean liquid = data.inLiquid();

        // reset data if in liquid
        if (!liquid) {
            data.liquidTime(0);
            data.waterLocation(null);
        }

        if (liquid && !data.onGround() && data.getInAirTime() >= 5 && player.riding == null && !MovingAccess.isOnBoat(player)) {
            final CheckResult result = new CheckResult();
            final double vertical = Math.floor((data.vertical()) * 100) / 100;
            final int liquidTime = data.liquidTime() + 1;
            data.liquidTime(liquidTime);

            // make sure we have had liquid time for a while
            if (liquidTime > timeInLiquidRequired) {
                Location water = data.waterLocation();

                // reset water location for setback
                if (water == null) {
                    water = to;
                    data.waterLocation(water);
                }

                // basic check, if on ground and no vertical, flag.
                if (vertical == 0.0) {
                    final int count = data.getNoVerticalCount() + 1;
                    data.setNoVerticalCount(count);

                    if (count > maxNoVerticalCount) {
                        result.setFailed("No vertical movement in liquid.")
                                .withParameter("vertical", vertical)
                                .withParameter("count", count)
                                .withParameter("max", maxNoVerticalCount);
                    }
                } else {
                    data.setNoVerticalCount(0);

                    if (vertical < minVerticalAscend && data.ascending()) {
                        final int count = data.getLiquidSmallMovementCount() + 1;
                        data.setLiquidSmallMovementCount(count);

                        if (count > maxSmallMovementVerticalCount) {
                            result.setFailed("Ascending too slowly in liquid.")
                                    .withParameter("v", vertical)
                                    .withParameter("min", minVerticalAscend)
                                    .withParameter("count", count)
                                    .withParameter("max", maxSmallMovementVerticalCount);
                        }
                    } else {
                        data.setLiquidSmallMovementCount(0);
                    }
                }
                if (checkViolation(player, result)) {
                    if (data.ground() != null) {
                        final double distance = data.getGroundDistance();
                        player.teleport(distance > maxSetbackDistance ? data.from() : data.ground());
                    } else {
                        player.teleport(data.from());
                    }
                }
            }
        }

    }

    @Override
    public void reloadConfig() {
        load();
    }

    @Override
    public void load() {
        timeInLiquidRequired = configuration.getInt("time-in-liquid-required");
        maxNoVerticalCount = configuration.getInt("max-no-vertical-count");
        maxSmallMovementVerticalCount = configuration.getInt("max-small-vertical-movement-count");
        minVerticalAscend = configuration.getDouble("min-vertical-ascend");
        maxSetbackDistance = configuration.getDouble("max-setback-distance");
        minSwimTime = configuration.getLong("min-swim-time");
    }
}
