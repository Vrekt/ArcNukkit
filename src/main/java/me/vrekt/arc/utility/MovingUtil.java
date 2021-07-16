package me.vrekt.arc.utility;


import cn.nukkit.block.Block;
import cn.nukkit.level.Location;
import cn.nukkit.math.BlockFace;
import me.vrekt.arc.compatibility.block.BlockAccess;
import me.vrekt.arc.data.moving.MovingData;
import me.vrekt.arc.utility.math.MathUtil;

/**
 * Moving utility for calculating various things related to movement.
 * WARNING: Locations here are directly manipulated, you should clone before using any method.
 */
public final class MovingUtil {

    /**
     * Check if the location is on a solid block
     * 0.5, 0.3, 0.1
     *
     * @param location the location
     * @return {@code true} if so
     */
    public static boolean onGround(Location location) {
        final Block block = location.subtract(0, 0.5, 0).getLevelBlock();
        location.add(0, 0.5, 0);

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
     * Calculate player movement
     *
     * @param data their data
     * @param from from
     * @param to   to
     */
    public static void calculateMovement(MovingData data, Location from, Location to) {
        final long now = System.currentTimeMillis();
        // prevent cloning multiple times to save performance
        final Location cloneFrom = from.clone();
        final Location cloneTo = to.clone();

        data.from(cloneFrom);
        data.to(cloneTo);

        // calculate ground
        final boolean currentOnGround = data.onGround();
        final boolean previousOnGround = data.wasOnGround();
        final boolean onGround = MovingUtil.onGround(cloneTo);

        data.onGround(onGround);
        data.wasOnGround(!onGround || (currentOnGround && previousOnGround));

        // calculate ground stuff.
        if (onGround) {
            // set initial safe location if null.
            if (data.getSafeLocation() == null) {
                data.setSafeLocation(cloneTo);
            }

            // Set ground location with the cloned location.
            data.ground(cloneTo);
            // Here, reset the ladder location once we touch the ground.
            data.setLadderLocation(null);

            // TODO: Work on slime-block compatibility.

            final boolean isOnIce = MovingUtil.isOnIce(cloneTo);
            final boolean wasOnIce = MovingUtil.isOnIce(cloneFrom);

            data.onIce(isOnIce);

            if (isOnIce) {
                data.incrementOnIceTime();
                data.offIceTime(0);
            } else {
                data.onIceTime(0);
                if (!wasOnIce) data.incrementOffIceTime();
            }

        } else {
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

        // calculate ascending/descending
        final boolean ascending = distance > 0.0 && cloneTo.getY() > cloneFrom.getY();
        final boolean descending = distance > 0.0 && cloneTo.getY() < cloneFrom.getY();
        data.ascending(ascending);
        data.descending(descending);
        if (ascending) {
            data.incrementAscendingTime();
        } else {
            data.ascendingTime(0);
        }

        if (descending) {
            data.incrementDescendingTime();
        } else {
            data.descendingTime(0);
        }

        // calculate climbing
        final boolean hasClimbable = MovingUtil.hasClimbable(cloneTo);
        final boolean climbing = hasClimbable && (ascending || descending);
        data.hasClimbable(hasClimbable);
        data.climbing(climbing);

        // calculate liquids
        final boolean inLiquid = MovingUtil.isInOrOnLiquid(cloneTo);
        data.inLiquid(inLiquid);
        data.lastMovingUpdate(now);
    }

}
