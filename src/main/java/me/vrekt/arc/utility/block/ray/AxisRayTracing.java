package me.vrekt.arc.utility.block.ray;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockFenceGate;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.SimpleAxisAlignedBB;
import me.vrekt.arc.utility.block.BlockProperties;
import me.vrekt.arc.utility.block.model.BlockModel;
import me.vrekt.arc.utility.math.MathUtil;

/**
 * Default ray-tracing implementation.
 */
public final class AxisRayTracing implements RayTrace {

    /**
     * Start and end locations.
     */
    private double minX, minY, minZ, maxX, maxY, maxZ;

    /**
     * The level
     */
    private Level level;

    /**
     * The player bounding box.
     */
    private AxisAlignedBB bb;

    private boolean c;

    @Override
    public void set(Location from, Location to, AxisAlignedBB bb) {
        this.minX = from.x;
        this.minY = from.y;
        this.minZ = from.z;
        this.maxX = to.x;
        this.maxY = to.y;
        this.maxZ = to.z;
        this.level = to.level;
        this.bb = bb;
    }

    @Override
    public void loop() {

        double x = minX;
        double y = minY;
        double z = minZ;


        for (int i = 0; i < 3; i++) {
            final boolean yCollide = checkYAXis(minX, minY, minZ);
            final boolean xCollide = checkXAxis(minX, minY, minZ);
            final boolean zCollide = checkZAxis(minX, minY, minZ);

            x = maxX;
            y = maxY;
            z = maxZ;

            if (xCollide || zCollide || yCollide) c = true;

            if (c) break;
        }

    }

    @Override
    public boolean step(Level level, int blockX, int blockY, int blockZ, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        final Block block = level.getBlock(blockX, blockY, blockZ);

        if (BlockProperties.isPassable(block)) return false;

        // retrieve the special block model for the block.
        final BlockModel model = BlockModel.getModel(block);
        if (model == null) {
            // block does not have a model, so we can assume it does not have any special properties.
            return block.collidesWithBB(new SimpleAxisAlignedBB(minX, minY, minZ, maxX - 0.6, maxY, maxZ - 0.6));
        } else {

            if (block instanceof BlockFenceGate) {
                System.out.println(((BlockFenceGate) block).getBlockFace());
            }

            final double x = bb.getMaxX() - bb.getMinX();
            final double z = bb.getMaxZ() - bb.getMinZ();

            final double x1 = maxX - minX;
            final double z1 = maxZ - minZ;

            System.out.println("FIRST: " + x + ":" + z + "::: SECOND: " + x1 + ":" + z1);


            return false;
        }
    }


    public static double[] lerp3D(double amount, double x1, double y1, double z1, double x2, double y2, double z2) {
        return new double[]{x1 + (x2 - x1) * amount, y1 + (y2 - y1) * amount, z1 + (z2 - z1) * amount};
    }

    @Override
    public boolean collides() {
        return c;
    }

    private boolean checkZAxis(double xIn, double yIn, double zIn) {
        // Skip if there is nothing to iterate.
        if (zIn == maxZ) {
            return false;
        }
        // Iterate over axis, applying margins.
        final int increment;
        final double yMin = yIn - 0.0;
        final double yMax = yIn + 2;
        final double xMin = xIn - 0.0;
        final double xMax = xIn + 0.0;
        final double zStart, zEnd;
        final int iEndZ;
        if (zIn < maxZ) {
            increment = 1;
            zStart = (zIn);
            zEnd = maxZ + 0.4;
            iEndZ = MathUtil.floor(zEnd + 1);
        } else {
            increment = -1;
            zStart = (zIn);
            zEnd = maxZ - 0.4;
            iEndZ = MathUtil.floor(zEnd - 1);
        }
        final int iMinY = MathUtil.floor(yMin);
        final int iMaxY = MathUtil.floor(yMax);
        final int iMinX = MathUtil.floor(xMin);
        final int iMaxX = MathUtil.floor(xMax);
        final int iStartZ = MathUtil.floor(zStart);

        for (int z1 = iStartZ; z1 != iEndZ; z1 += increment) {

            for (int y1 = iMinY; y1 <= iMaxY; y1++) {
                for (int x1 = iMinX; x1 <= iMaxX; x1++) {
                    if (step(level, x1, y1, z1, xMin - 0.3, yMin, increment == 1 ? zStart - 0.3 : zEnd - 0.3, xMax + 0.3, yMax, increment == 1 ? zEnd + 0.3 : zStart + 0.3)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean checkYAXis(double xIn, double yIn, double zIn) {
        // Skip if there is nothing to iterate.
        if (yIn == maxY) {
            return false;
        }
        // Iterate over axis, applying margins.
        final int increment;
        final double xMin = xIn - 0.0;
        final double xMax = xIn + 0.0;
        final double zMin = zIn - 0.0;
        final double zMax = zIn + 0.0;
        final double yStart, yEnd;
        final int iEndY;
        if (yIn < maxY) {
            increment = 1;
            yStart = yIn;
            yEnd = maxY + 2;
            iEndY = MathUtil.floor(yEnd) + 1;
        } else {
            increment = -1;
            yStart = (yIn + 2);
            yEnd = maxY;
            iEndY = MathUtil.floor(yEnd) - 1;
        }
        final int iMinX = MathUtil.floor(xMin);
        final int iMaxX = MathUtil.floor(xMax);
        final int iMinZ = MathUtil.floor(zMin);
        final int iMaxZ = MathUtil.floor(zMax);
        final int iStartY = MathUtil.floor(yStart);

        for (int y1 = iStartY; y1 != iEndY; y1 += increment) {
            for (int x1 = iMinX; x1 <= iMaxX; x1++) {
                for (int z1 = iMinZ; z1 <= iMaxZ; z1++) {
                    if (step(level, x1, y1, z1, xMin - 0.3, increment == 1 ? yStart : yEnd, zMin - 0.3, xMax + 0.3, increment == 1 ? yEnd : yStart, zMax + 0.3)) {
                        return true;
                    }
                }
            }
        }
        // No collision.
        return false;
    }

    private boolean checkXAxis(double xIn, double yIn, double zIn) {
        // Skip if there is nothing to iterate.
        if (xIn == maxX) {
            return false;
        }
        // Iterate over axis, applying margins.
        final int increment;
        final double yMin = yIn - 0.0;
        final double yMax = yIn + 2;
        final double zMin = zIn - 0.0;
        final double zMax = zIn + 0.0;
        final double xStart, xEnd;
        final int iEndX;
        if (xIn < maxX) {
            increment = 1;
            xStart = (xIn);
            xEnd = maxX;
            iEndX = MathUtil.floor(xEnd) + 1;
        } else {
            increment = -1;
            xStart = (xIn);
            xEnd = maxX;
            iEndX = MathUtil.floor(xEnd) - 1;
        }

        final int iMinY = MathUtil.floor(yMin);
        final int iMaxY = MathUtil.floor(yMax);
        final int iMinZ = MathUtil.floor(zMin);
        final int iMaxZ = MathUtil.floor(zMax);
        final int iStartX = MathUtil.floor(xStart);

        for (int x1 = iStartX; x1 != iEndX; x1 += increment) {
            for (int y1 = iMinY; y1 <= iMaxY; y1++) {
                for (int z1 = iMinZ; z1 <= iMaxZ; z1++) {
                    if (step(level, x1, y1, z1, increment == 1 ? xStart - 0.3 : xEnd - 0.3, yMin, zMin - 0.3, increment == 1 ? xEnd + 0.3 : xStart + 0.3, yMax, zMax + 0.3)) {
                        return true;
                    }
                }
            }
        }
        // No collision.
        return false;
    }

}
