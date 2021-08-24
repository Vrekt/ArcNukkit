package me.vrekt.arc.utility;


import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityBoat;
import cn.nukkit.level.Location;
import cn.nukkit.math.BlockFace;
import me.vrekt.arc.utility.block.BlockAccess;
import me.vrekt.arc.data.moving.MovingData;
import me.vrekt.arc.utility.math.MathUtil;

/**
 * Moving utility for calculating various things related to movement.
 * WARNING: Locations here are directly manipulated, you should clone before using any method.
 */
public final class MovingAccess {

    /**
     * Check if the location is on a solid block
     * 0.5, 0.3, 0.1
     *
     * @param location the location
     * @return {@code true} if so
     */
    public static boolean onGround(Location location) {
        final Block block = location.subtract(0, 1, 0).getLevelBlock();
        location.add(0, 1, 0);

        return BlockAccess.isConsideredGround(block) ||
                BlockAccess.hasSolidGroundAt(location, location.getLevel(), 0.3, -0.1, 0.3);
    }

    /**
     * Check if the player has a climbable block at this location
     *
     * @param location the location
     * @return {@code true} if so
     */
    public static boolean hasClimbable(Location location) {
        return BlockAccess.isClimbable(location.getLevelBlock()) ||
                BlockAccess.hasClimbableAt(location, location.getLevel(), 0.1, -0.06, 0.1);
    }

    /**
     * @return if we are in or on liquid.
     */
    public static boolean isInOrOnLiquid(Location location) {
        return BlockAccess.isLiquid(location.getLevelBlock()) ||
                BlockAccess.isLiquid(location.getLevelBlock().getSide(BlockFace.DOWN)) ||
                BlockAccess.hasLiquidAt(location, location.getLevel(), 0.3, -0.1, 0.3);
    }

    /**
     * @param location the location
     * @return {@code true} if the location is on ice
     */
    public static boolean isOnIce(Location location) {
        return BlockAccess.isIce(location.getLevelBlock()) ||
                BlockAccess.isIce(location.getLevelBlock().getSide(BlockFace.DOWN)) ||
                BlockAccess.hasIceAt(location, location.getLevel(), 0.1, -0.01, 0.1);
    }

    /**
     * Check if a player is on a boat.
     *
     * @param player the player
     * @return {@code true} if so
     */
    public static boolean isOnBoat(Player player) {
        for (Entity entity : player.getLevel().getNearbyEntities(player.boundingBox)) {
            if (entity instanceof EntityBoat && entity.y <= player.y) return true;
        }
        return false;
    }

    /**
     * Calculate player movement
     *
     * @param data their data
     * @param from from
     * @param to   to
     */
    public static void calculateMovement(Player player, MovingData data, Location from, Location to) {
        final long now = System.currentTimeMillis();

        // prevent cloning multiple times to save performance
        if (from == null) from = player.getLocation();
        if (to == null) to = player.getLocation();

        final Location cloneFrom = from.clone();
        final Location cloneTo = to.clone();

        data.from(cloneFrom);
        data.to(cloneTo);

        // calculate ground
        final boolean currentOnGround = data.onGround();
        final boolean previousOnGround = data.wasOnGround();
        final boolean onGround = MovingAccess.onGround(cloneTo);

        data.onGround(onGround);
        data.wasOnGround(!onGround || (currentOnGround && previousOnGround));

        // calculate ground stuff.
        if (onGround) {
            data.incrementOnGroundTime();

            // set initial safe location if null.
            if (data.getSafeLocation() == null) {
                data.setSafeLocation(cloneTo);
            }

            // Set ground location with the cloned location.
            data.ground(cloneTo);

            final boolean isOnIce = MovingAccess.isOnIce(cloneTo);
            final boolean wasOnIce = MovingAccess.isOnIce(cloneFrom);

            data.onIce(isOnIce);

            final boolean hasSlimeblock = BlockAccess.hasSlimeblockAt(cloneTo, cloneTo.getLevel(), 0.3, -0.1, 0.3)
                    || BlockAccess.hasSlimeblockAt(cloneTo, cloneTo.getLevel(), 0.3, -0.5, 0.3)
                    || BlockAccess.hasSlimeblockAt(cloneTo, cloneTo.getLevel(), 0.3, -1, 0.3);
            data.setHasSlimeBlockLaunch(hasSlimeblock);

            if (isOnIce) {
                data.incrementOnIceTime();
                data.offIceTime(0);
            } else {
                data.onIceTime(0);
                if (!wasOnIce) data.incrementOffIceTime();
            }

            data.setInAirTime(0);
            data.ascendingTime(0);
            data.descendingTime(0);

            data.setNoResetDescendTime(0);
            data.setNoResetAscendTime(0);
        } else {
            data.setInAirTime(data.getInAirTime() + 1);

            final boolean hadSlimeblock = BlockAccess.hasSlimeblockAt(cloneFrom, cloneFrom.getLevel(), 0.3, -0.1, 0.3)
                    || BlockAccess.hasSlimeblockAt(cloneFrom, cloneFrom.getLevel(), 0.3, -1, 0.3);
            final boolean hasSlimeblock = BlockAccess.hasSlimeblockAt(cloneTo, cloneTo.getLevel(), 0.3, -0.1, 0.3)
                    || BlockAccess.hasSlimeblockAt(cloneTo, cloneTo.getLevel(), 0.3, -1, 0.3)
                    || BlockAccess.hasSlimeblockAt(cloneTo, cloneTo.getLevel(), 0.3, -2, 0.3);
            data.setHasSlimeblock(hadSlimeblock || hasSlimeblock);

            data.onGroundTime(0);
            if (data.getSafeLocation() == null) data.setSafeLocation(from);
        }

        // calculate sprinting and sneaking times
        final boolean sprinting = data.sprinting();
        final boolean sneaking = data.sneaking();
        if (sprinting) {
            data.incrementSprintTime();
        } else {
            data.sprintTime(0);
        }

        if (sneaking) {
            data.incrementSneakTime();
        } else {
            data.sneakTime(0);
        }

        // distance moved vertically.
        final double distance = MathUtil.vertical(cloneFrom, cloneTo);
        data.lastVertical(data.vertical());
        data.vertical(distance);

        final double horizontal = MathUtil.horizontal(cloneFrom, cloneTo);
        data.setLastHorizontal(data.getHorizontal());
        data.setHorizontal(horizontal);

        final double ground = MathUtil.distance(data.ground(), cloneTo);
        final double hGround = MathUtil.horizontal(data.ground(), cloneTo);
        data.setGroundDistance(ground);
        data.setGroundHorizontalDistance(hGround);

        // calculate ascending/descending
        final boolean ascending = distance > 0.0 && cloneTo.getY() > cloneFrom.getY();
        final boolean descending = distance > 0.0 && cloneTo.getY() < cloneFrom.getY();
        data.ascending(ascending);
        data.descending(descending);

        if (ascending) {
            data.incrementAscendingTime();
            data.setNoResetAscendTime(data.getNoResetAscendTime() + 1);
        } else {
            data.ascendingTime(0);
        }

        if (descending) {
            data.incrementDescendingTime();
            data.setNoResetDescendTime(data.getNoResetDescendTime() + 1);
        } else {
            data.descendingTime(0);
        }

        // calculate climbing
        final boolean hasClimbable = MovingAccess.hasClimbable(cloneTo);
        final boolean hadClimbable = MovingAccess.hasClimbable(cloneFrom);
        final boolean climbing = hasClimbable && (ascending || descending);
        data.hasClimbable(hasClimbable);
        data.hadClimbable(hadClimbable);
        data.climbing(climbing);
        data.climbTime(hasClimbable ? data.climbTime() + 1 : 0);

        // calculate liquids
        final boolean inLiquid = MovingAccess.isInOrOnLiquid(cloneTo);
        data.inLiquid(inLiquid);
        data.lastMovingUpdate(now);
    }

}
