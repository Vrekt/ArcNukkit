package me.vrekt.arc.utility.block.ray;

import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.math.AxisAlignedBB;

/**
 * Represents a ray tracer.
 */
public interface RayTrace {

    /**
     * Set where to ray-trace.
     *
     * @param from the starting location
     * @param to   the end location
     * @param bb   the player bounding box.
     */
    void set(Location from, Location to, AxisAlignedBB bb);

    /**
     * Loop through all blocks defined within the start and end position.
     */
    void loop();

    /**
     * Step through the next block, returning {@code true} if there is a collision.
     *
     * @param level  the level
     * @param blockX the block X
     * @param blockY the block Y
     * @param blockZ the block Z
     * @param minX   minimum X
     * @param minY   minimum Y
     * @param minZ   minimum Z
     * @param maxX   maximum X
     * @param maxY   maximum Y
     * @param maxZ   maximum Z
     * @return {@code true} if any collision occurs.
     */
    boolean step(Level level, final int blockX, final int blockY, final int blockZ,
                 final double minX, final double minY, final double minZ,
                 final double maxX, final double maxY, final double maxZ);

    boolean collides();

}
