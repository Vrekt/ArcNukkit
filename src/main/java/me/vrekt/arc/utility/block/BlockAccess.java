package me.vrekt.arc.utility.block;

import cn.nukkit.block.Block;
import me.vrekt.arc.utility.math.MathUtil;

/**
 * Block access utility
 */
public final class BlockAccess {

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
