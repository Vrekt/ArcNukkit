package me.vrekt.arc.utility;


import cn.nukkit.block.Block;
import cn.nukkit.level.Location;
import me.vrekt.arc.data.moving.MovingData;
import me.vrekt.arc.utility.block.Blocks;
import me.vrekt.arc.utility.math.MathUtil;

import java.util.function.Predicate;

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
        final Block selfBlock = location.subtract(0, 0.5, 0).getLevelBlock();
        location.add(0, 0.5, 0);
        if (Blocks.isSolid(selfBlock)) return true;

        return checkBlocksAround(location, 0.3, -0.1, 0.3, Blocks::isSolid);
    }

    /**
     * Check if the player has a climbable block at this location
     *
     * @param location the location
     * @return {@code true} if so
     */
    public static boolean hasClimbable(Location location) {
        final Block selfBlock = location.getLevelBlock();
        if (Blocks.isClimbable(selfBlock)) return true;
        return checkBlocksAround(location, 0.1, -0.06, 0.1, Blocks::isClimbable);
    }

    /**
     * @return if we are in or on liquid.
     */
    public static boolean isInOrOnLiquid(Location location) {
        if (Blocks.isLiquid(location.getLevelBlock())) return true;
        final boolean liquidRelative = Blocks.isLiquid(location.getLevelBlock().down());
        return liquidRelative || checkBlocksAround(location, 0.3, -0.1, 0.3, Blocks::isLiquid);
    }

    /**
     * @param location the location
     * @return {@code true} if the location is on ice
     */
    public static boolean isOnIce(Location location) {
        if (Blocks.isIce(location.getLevelBlock())) return true;
        final boolean iceRelative = Blocks.isIce(location.getLevelBlock().down());
        return iceRelative || checkBlocksAround(location, 0.1, -0.01, 0.1, Blocks::isIce);
    }

    /**
     * Check if we are on ice with trapdoors.
     *
     * @param location the location
     * @return {@code true} if so
     * TODO
     */
    public static boolean isOnIceTrapdoor(Location location) {
        return true;
    }

    /**
     * Check if the location has a block.
     *
     * @param location  the location
     * @param xModifier the X modifier
     * @param yModifier the Y modifier
     * @param zModifier the Z modifier
     * @param predicate the predicate to test
     * @return {@code true} if so
     */
    public static boolean hasBlock(Location location, double xModifier, double yModifier, double zModifier, Predicate<Block> predicate) {
        final Block self = location.getLevelBlock();
        if (predicate.test(self)) return true;
        final Block under = location.getLevelBlock().down();
        if (predicate.test(under)) return true;
        return checkBlocksAround(location, xModifier, yModifier, zModifier, predicate);
    }

    /**
     * Check blocks around a location
     *
     * @param location  the location
     * @param xModifier the xModifier
     * @param yModifier the yModifier
     * @param zModifier the zModifier
     * @param predicate the predicate to test against
     * @return {@code true} if the predicate is successful.
     */
    public static boolean checkBlocksAround(Location location, double xModifier, double yModifier, double zModifier, Predicate<Block> predicate) {
        final double originalX = location.getX();
        final double originalY = location.getY();
        final double originalZ = location.getZ();

        if (predicate.test(getBlockFromModifier(location, xModifier, yModifier, -zModifier, originalX, originalY, originalZ)))
            return true;
        if (predicate.test(getBlockFromModifier(location, -xModifier, yModifier, zModifier, originalX, originalY, originalZ)))
            return true;
        if (predicate.test(getBlockFromModifier(location, -xModifier, yModifier, -zModifier, originalX, originalY, originalZ)))
            return true;
        return predicate.test(getBlockFromModifier(location, xModifier, yModifier, zModifier, originalX, originalY, originalZ));
    }

    /**
     * Get a block from a modifier
     *
     * @param location  the location
     * @param xModifier the xModifier
     * @param yModifier the yModifier
     * @param zModifier the zModifier
     * @param originalX the original X
     * @param originalY the original Y
     * @param originalZ the original Z
     * @return the block
     */
    public static Block getBlockFromModifier(Location location, double xModifier, double yModifier, double zModifier, double originalX, double originalY, double originalZ) {
        location.add(xModifier, yModifier, zModifier);
        final Block block = location.getLevelBlock();
        reset(location, originalX, originalY, originalZ);
        return block;
    }

    /**
     * Reset the location
     *
     * @param location  the location
     * @param originalX the original X
     * @param originalY the original Y
     * @param originalZ the original Z
     */
    public static void reset(Location location, double originalX, double originalY, double originalZ) {
        location.x = originalX;
        location.y = originalY;
        location.z = originalZ;
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
            data.ground(cloneTo);
            data.incrementOnGroundTime();

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
