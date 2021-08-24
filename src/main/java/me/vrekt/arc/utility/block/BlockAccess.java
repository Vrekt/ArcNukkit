package me.vrekt.arc.utility.block;

import cn.nukkit.block.*;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
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
                block instanceof BlockDoubleSlab ||
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
     * Check if the modified location has slimeblock
     *
     * @param origin the origin
     * @param level  the level
     * @param x      the modified X
     * @param y      the modified Y
     * @param z      the modified Z
     * @return the result
     */
    public static boolean hasSlimeblockAt(Location origin, Level level, double x, double y, double z) {
        if (hasSlimeblockAt0(origin, level, x, y, z)) return true;
        if (hasSlimeblockAt0(origin, level, x, y, -z)) return true;
        if (hasSlimeblockAt0(origin, level, -x, y, z)) return true;
        return hasSlimeblockAt0(origin, level, -x, y, -z);
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
        final Block block = level.getBlock(MathUtil.floor(origin.getX() + x),
                MathUtil.floor(origin.getY() + y), MathUtil.floor(origin.getZ() + z));
        return block.getId() == BlockID.PACKED_ICE || block.getId() == BlockID.ICE;
    }

    /**
     * Check if the modified location has slime-block
     *
     * @param origin the origin
     * @param level  the level
     * @param x      the modified X
     * @param y      the modified Y
     * @param z      the modified Z
     * @return the result
     */
    private static boolean hasSlimeblockAt0(Location origin, Level level, double x, double y, double z) {
        return level.getBlock(MathUtil.floor(origin.getX() + x),
                MathUtil.floor(origin.getY() + y), MathUtil.floor(origin.getZ() + z))
                instanceof BlockSlime;
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

    /**
     * Get block at
     *
     * @param origin the origin
     * @param level  the level
     * @param x      the modified X
     * @param y      the modified Y
     * @param z      the modified Z
     * @return the block
     */
    public static Block getBlockAt(Location origin, Level level, double x, double y, double z) {
        return level.getBlock(MathUtil.floor(origin.getX() + x),
                MathUtil.floor(origin.getY() + y), MathUtil.floor(origin.getZ() + z));
    }

    /**
     * Check if the player has a solid block
     *
     * @param origin the origin
     * @param level  the level
     * @param x      the modified X
     * @param y      the modified Y
     * @param z      the modified Z
     * @return {@code true} if so
     */
    public static boolean hasSolidBlockAt(Location origin, Level level, double x, double y, double z) {
        if (hasSolidBlockAt0(origin, level, x, y, z)) return true;
        if (hasSolidBlockAt0(origin, level, x, y, -z)) return true;
        if (hasSolidBlockAt0(origin, level, -x, y, z)) return true;
        return hasSolidBlockAt0(origin, level, -x, y, -z);
    }

    /**
     * Check if there is a solid block
     *
     * @param origin the origin
     * @param level  the level
     * @param x      the modified X
     * @param y      the modified Y
     * @param z      the modified Z
     * @return {@code true} if so
     */
    public static boolean hasSolidBlockAt0(Location origin, Level level, double x, double y, double z) {
        return level.getBlock(MathUtil.floor(origin.getX() + x),
                        MathUtil.floor(origin.getY() + y), MathUtil.floor(origin.getZ() + z))
                .isSolid();
    }

    /**
     * Check if there is a slab
     *
     * @param origin the origin
     * @param level  the level
     * @param x      the modified X
     * @param y      the modified Y
     * @param z      the modified Z
     * @return the block
     */
    public static boolean hasSlabAt(Location origin, Level level, double x, double y, double z) {
        if (hasSlabAt0(origin, level, x, y, z)) return true;
        if (hasSlabAt0(origin, level, x, y, -z)) return true;
        if (hasSlabAt0(origin, level, -x, y, z)) return true;
        return hasSlabAt0(origin, level, -x, y, -z);
    }

    /**
     * Check if there is a slab
     *
     * @param origin the origin
     * @param level  the level
     * @param x      the modified X
     * @param y      the modified Y
     * @param z      the modified Z
     * @return the block
     */
    private static boolean hasSlabAt0(Location origin, Level level, double x, double y, double z) {
        final Block block = level.getBlock(MathUtil.floor(origin.getX() + x),
                MathUtil.floor(origin.getY() + y), MathUtil.floor(origin.getZ() + z));
        return block.getId() == BlockID.DOUBLE_SLAB
                || block.getId() == BlockID.SLAB
                || block.getId() == BlockID.WOOD_SLAB
                || block.getId() == BlockID.DOUBLE_WOOD_SLAB
                || block.getId() == BlockID.RED_SANDSTONE_SLAB
                || block.getId() == BlockID.DOUBLE_RED_SANDSTONE_SLAB;
    }

    /**
     * Check if there is a stair
     *
     * @param origin the origin
     * @param level  the level
     * @param x      the modified X
     * @param y      the modified Y
     * @param z      the modified Z
     * @return the block
     */
    public static boolean hasStairAt(Location origin, Level level, double x, double y, double z) {
        if (hasStairAt0(origin, level, x, y, z)) return true;
        if (hasStairAt0(origin, level, x, y, -z)) return true;
        if (hasStairAt0(origin, level, -x, y, z)) return true;
        return hasStairAt0(origin, level, -x, y, -z);
    }

    /**
     * Check if there is a stair
     *
     * @param origin the origin
     * @param level  the level
     * @param x      the modified X
     * @param y      the modified Y
     * @param z      the modified Z
     * @return the block
     */
    private static boolean hasStairAt0(Location origin, Level level, double x, double y, double z) {
        return level.getBlock(MathUtil.floor(origin.getX() + x),
                MathUtil.floor(origin.getY() + y), MathUtil.floor(origin.getZ() + z)) instanceof BlockStairs;
    }

    /**
     * Returns this block's coordinates packed into a long value.
     *
     * @return This block's x, y, and z coordinates packed into a long value
     */
    public static long getBlockKey(Block block) {
        return getBlockKey(MathUtil.floor(block.x), MathUtil.floor(block.y), MathUtil.floor(block.z));
    }

    /**
     * Returns the specified block coordinates packed into a long value
     * <p>
     * The return value can be computed as follows:
     * <br>
     * {@code long value = ((long)x & 0x7FFFFFF) | (((long)z & 0x7FFFFFF) << 27) | ((long)y << 54);}
     * </p>
     *
     * <p>
     * And may be unpacked as follows:
     * <br>
     * {@code int x = (int) ((packed << 37) >> 37);}
     * <br>
     * {@code int y = (int) (packed >>> 54);}
     * <br>
     * {@code int z = (int) ((packed << 10) >> 37);}
     * </p>
     *
     * @return This block's x, y, and z coordinates packed into a long value
     */
    public static long getBlockKey(int x, int y, int z) {
        return ((long) x & 0x7FFFFFF) | (((long) z & 0x7FFFFFF) << 27) | ((long) y << 54);
    }

}
