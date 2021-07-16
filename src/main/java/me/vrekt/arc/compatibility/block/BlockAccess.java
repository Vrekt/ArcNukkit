package me.vrekt.arc.compatibility.block;

import cn.nukkit.block.*;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.math.BlockFace;
import me.vrekt.arc.utility.math.MathUtil;

/**
 * Block access.
 * <p>
 * Serves to maintain compatibility across multiple versions.
 */
public final class BlockAccess {

    /**
     * Check if a block is interactable.
     *
     * @param block the block
     * @return {@code true} if so
     */
    public static boolean isInteractable(Block block) {
        return block.canBeActivated() ||
                block instanceof BlockTrapdoor ||
                block instanceof BlockButton ||
                block instanceof BlockDoor ||
                block instanceof BlockFlowerPot ||
                block instanceof BlockBed ||
                block instanceof BlockAnvil ||
                block instanceof BlockChest ||
                block instanceof BlockEnderChest ||
                block instanceof BlockBeacon ||
                block instanceof BlockFence ||
                block instanceof BlockFenceGate ||
                block instanceof BlockSignPost ||
                block instanceof BlockCauldron ||
                block instanceof BlockWallBanner ||
                block instanceof BlockUndyedShulkerBox ||
                block instanceof BlockNoteblock ||
                block instanceof BlockRedstoneRepeaterUnpowered ||
                block instanceof BlockRedstoneRepeaterPowered ||
                block instanceof BlockRedstoneComparator ||
                block instanceof BlockLever ||
                block instanceof BlockJukebox ||
                block instanceof BlockHopper ||
                block instanceof BlockFurnace ||
                block instanceof BlockCraftingTable ||
                block instanceof BlockCake ||
                block instanceof BlockBrewingStand;
    }

    /**
     * Check if this block has a vertical modifier, above the max jump height.
     * <p>
     * Not sure if {@link BlockSnow} is needed or just the layer.
     *
     * @param block the block
     * @return {@code true} if so (>0.42)
     */
    public static boolean hasVerticalModifier(Block block) {
        return block instanceof BlockFence ||
                block instanceof BlockFenceGate ||
                block instanceof BlockStairs ||
                block instanceof BlockSlab ||
                block instanceof BlockWall ||
                block instanceof BlockSnow ||
                block instanceof BlockSnowLayer ||
                block instanceof BlockSkull ||
                block instanceof BlockBed;
    }

    /**
     * Check if a block is considered solid, able to be walked on.
     *
     * @param block the block
     * @return {@code true} if the provided block is considered solid.
     */
    public static boolean isConsideredGround(Block block) {
        return block.isSolid() || hasVerticalModifier(block) || block instanceof BlockTrapdoor;
    }

    /**
     * Check if a block can be climbed.
     *
     * @param block the block
     * @return {@code true} if so
     */
    public static boolean isClimbable(Block block) {
        return block instanceof BlockLadder ||
                block instanceof BlockVine;
    }

    /**
     * Check if the provided origin and modified X, Y, Z coordinates have a vertical modifier
     *
     * @param origin the origin
     * @param level  the level
     * @param x      the modified X
     * @param y      the modified Y
     * @param z      the modified Z
     * @return {@code true} if so
     */
    public static boolean hasVerticalModifierAt(Location origin, Level level, double x, double y, double z) {
        if (hasVerticalModifierAt0(origin, level, x, y, z)) return true;
        if (hasVerticalModifierAt0(origin, level, x, y, -z)) return true;
        if (hasVerticalModifierAt0(origin, level, -x, y, z)) return true;
        return hasVerticalModifierAt0(origin, level, -x, y, -z);
    }

    /**
     * Check if the provided origin and modified X, Y, Z coordinates have solid ground.
     *
     * @param origin the origin
     * @param level  the level
     * @param x      the modified X
     * @param y      the modified Y
     * @param z      the modified Z
     * @return {@code true} if so
     */
    public static boolean hasSolidGroundAt(Location origin, Level level, double x, double y, double z) {
        if (hasSolidGroundAt0(origin, level, x, y, z)) return true;
        if (hasSolidGroundAt0(origin, level, x, y, -z)) return true;
        if (hasSolidGroundAt0(origin, level, -x, y, z)) return true;
        return hasSolidGroundAt0(origin, level, -x, y, -z);
    }

    /**
     * Check if the provided origin and modified X, Y, Z coordinates have a climbable block.
     *
     * @param origin the origin
     * @param level  the level
     * @param x      the modified X
     * @param y      the modified Y
     * @param z      the modified Z
     * @return {@code true} if so
     */
    public static boolean hasClimbableAt(Location origin, Level level, double x, double y, double z) {
        if (hasClimbableAt0(origin, level, x, y, z)) return true;
        if (hasClimbableAt0(origin, level, x, y, -z)) return true;
        if (hasClimbableAt0(origin, level, -x, y, z)) return true;
        return hasClimbableAt0(origin, level, -x, y, -z);
    }

    /**
     * Check if the provided origin and modified X, Y, Z coordinates is liquid.
     *
     * @param origin the origin
     * @param level  the level
     * @param x      the modified X
     * @param y      the modified Y
     * @param z      the modified Z
     * @return {@code true} if so
     */
    public static boolean hasLiquidAt(Location origin, Level level, double x, double y, double z) {
        if (hasLiquidAt0(origin, level, x, y, z)) return true;
        if (hasLiquidAt0(origin, level, x, y, -z)) return true;
        if (hasLiquidAt0(origin, level, -x, y, z)) return true;
        return hasLiquidAt0(origin, level, -x, y, -z);
    }

    /**
     * Check if the provided origin and modified X, Y, Z coordinates has ice
     *
     * @param origin the origin
     * @param level  the level
     * @param x      the modified X
     * @param y      the modified Y
     * @param z      the modified Z
     * @return {@code true} if so
     */
    public static boolean hasIceAt(Location origin, Level level, double x, double y, double z) {
        if (hasIceAt0(origin, level, x, y, z)) return true;
        if (hasIceAt0(origin, level, x, y, -z)) return true;
        if (hasIceAt0(origin, level, -x, y, z)) return true;
        return hasIceAt0(origin, level, -x, y, -z);
    }

    /**
     * Check if the provided origin and modified X, Y, Z coordinates have a vertical modifier
     *
     * @param origin    the origin
     * @param blockFace the block face
     * @return {@code true} if so
     */
    public static boolean hasVerticalModifierAt(Location origin, BlockFace blockFace) {
        return hasVerticalModifier(origin.getLevelBlock().getSide(blockFace));
    }

    /**
     * Check if the modified location has a vertical modifier block.
     *
     * @param origin the origin
     * @param level  the level
     * @param x      the modified X
     * @param y      the modified Y
     * @param z      the modified Z
     * @return the result
     */
    private static boolean hasVerticalModifierAt0(Location origin, Level level, double x, double y, double z) {
        return hasVerticalModifier(level.getBlock(MathUtil.floor(origin.getX() + x),
                MathUtil.floor(origin.getY() + y), MathUtil.floor(origin.getZ() + z)));
    }

    /**
     * Check if the modified location has solid ground.
     *
     * @param origin the origin
     * @param level  the level
     * @param x      the modified X
     * @param y      the modified Y
     * @param z      the modified Z
     * @return the result
     */
    private static boolean hasSolidGroundAt0(Location origin, Level level, double x, double y, double z) {
        return isConsideredGround(level.getBlock(MathUtil.floor(origin.getX() + x),
                MathUtil.floor(origin.getY() + y), MathUtil.floor(origin.getZ() + z)));
    }

    /**
     * Check if the modified location has a climbable.
     *
     * @param origin the origin
     * @param x      the modified X
     * @param y      the modified Y
     * @param z      the modified Z
     * @return the result
     */
    private static boolean hasClimbableAt0(Location origin, Level level, double x, double y, double z) {
        return isClimbable(level.getBlock(MathUtil.floor(origin.getX() + x),
                MathUtil.floor(origin.getY() + y), MathUtil.floor(origin.getZ() + z)));
    }

    /**
     * Check if the modified location has liquid
     *
     * @param origin the origin
     * @param level  the level
     * @param x      the modified X
     * @param y      the modified Y
     * @param z      the modified Z
     * @return the result
     */
    private static boolean hasLiquidAt0(Location origin, Level level, double x, double y, double z) {
        return level.getBlock(MathUtil.floor(origin.getX() + x),
                MathUtil.floor(origin.getY() + y), MathUtil.floor(origin.getZ() + z))
                instanceof BlockLiquid;
    }

    /**
     * Check if the modified location has ice
     *
     * @param origin the origin
     * @param x      the modified X
     * @param y      the modified Y
     * @param z      the modified Z
     * @return the result
     */
    private static boolean hasIceAt0(Location origin, Level level, double x, double y, double z) {
        return level.getBlock(MathUtil.floor(origin.getX() + x),
                MathUtil.floor(origin.getY() + y), MathUtil.floor(origin.getZ() + z))
                instanceof BlockIce;
    }

    /**
     * Check if a block is ice.
     *
     * @param block the block
     * @return {@code true} if so
     */
    public static boolean isIce(Block block) {
        return block instanceof BlockIce;
    }

    /**
     * Check if a block is liquid.
     *
     * @param block the block
     * @return {@code true} if so
     */
    public static boolean isLiquid(Block block) {
        return block instanceof BlockLiquid;
    }

}
