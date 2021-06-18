package me.vrekt.arc.utility.block;


import cn.nukkit.block.*;
import cn.nukkit.plugin.Plugin;
import me.vrekt.arc.Arc;

/**
 * A basic block utility
 */
public final class Blocks {

    /**
     * Plugin
     */
    private static final Plugin PLUGIN = Arc.plugin();

    /**
     * Check if the block is a fence.
     *
     * @param block the block
     * @return {@code true} if so
     */
    public static boolean isFence(Block block) {
        return block instanceof BlockFence;
    }

    /**
     * Check if the block is a slab
     *
     * @param block the block
     * @return {@code true} if so
     */
    public static boolean isSlab(Block block) {
        return block instanceof BlockSlab;
    }

    /**
     * Check if a block is a stair
     *
     * @param block the block
     * @return {@code true} if so
     */
    public static boolean isStair(Block block) {
        return block instanceof BlockStairs;
    }

    /**
     * Check if a block is a fence gate
     *
     * @param block the block
     * @return {@code true} if so
     */
    public static boolean isFenceGate(Block block) {
        return block instanceof BlockFenceGate;
    }

    /**
     * Check if a block is a climbable
     *
     * @param block the block
     * @return {@code true} if so
     */
    public static boolean isClimbable(Block block) {
        return block instanceof BlockLadder || block instanceof BlockVine;
    }

    /**
     * Check if a block is a liquid
     *
     * @param block the block
     * @return {@code true} if so
     */
    public static boolean isLiquid(Block block) {
        return block instanceof BlockLiquid;
    }

    /**
     * Check if a block is a trapdoor
     *
     * @param block the block
     * @return {@code true} if so
     */
    public static boolean isTrapdoor(Block block) {
        return block instanceof BlockTrapdoor;
    }

    /**
     * Check if a block is ice
     *
     * @param block the block
     * @return {@code true} if so
     */
    public static boolean isIce(Block block) {
        return false;
    }

    /**
     * Do not make isWall part of the {@code isSolid} check
     *
     * @param block the block
     * @return {@code true} if the block is a wall
     */
    public static boolean isWall(Block block) {
        return block instanceof BlockWall;
    }

    /**
     * Check if a block is considered solid
     *
     * @param block the block
     * @return {@code true} if so
     */
    public static boolean isSolid(Block block) {
        return block.isSolid();
    }

}
