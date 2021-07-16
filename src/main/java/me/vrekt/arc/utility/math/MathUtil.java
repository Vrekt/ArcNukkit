package me.vrekt.arc.utility.math;

import cn.nukkit.level.Location;

/**
 * A math utility.
 */
public final class MathUtil {

    /**
     * Calculate the vertical distance
     *
     * @param from from
     * @param to   to
     * @return the vertical distance
     */
    public static double vertical(Location from, Location to) {
        if (from == null || to == null) return 0.0;
        final double dy = to.getY() - from.getY();
        return Math.sqrt(dy * dy);
    }

    /**
     * Calculate the 3D distance
     *
     * @param from from
     * @param to   to
     * @return the distance
     */
    public static double distance(Location from, Location to) {
        if (from == null || to == null) return 0.0;

        final double dx = to.getX() - from.getX();
        final double dy = to.getY() - from.getY();
        final double dz = to.getZ() - from.getZ();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    /**
     * Calculate horizontal distance
     *
     * @param from from
     * @param to   to
     * @return distance
     */
    public static double horizontal(Location from, Location to) {
        if (from == null || to == null) return 0.0;

        final double dx = to.getX() - from.getX();
        final double dz = to.getZ() - from.getZ();
        return Math.sqrt(dx * dx + dz * dz);
    }

    public static int clampInt(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * Wrap the angle
     *
     * @param angle the angle
     * @return the modified angle
     */
    public static float wrapAngle(float angle) {
        angle = angle % 360.0F;
        if (angle >= 180.0F) angle -= 360.0F;
        if (angle < -180.0F) angle += 360.0F;
        return angle;
    }

    /**
     * NumberConversions floor from Bukkit.
     *
     * @param num the number
     * @return the floor, carpet preferably.
     */
    public static int floor(double num) {
        final int floor = (int) num;
        return floor == num ? floor : floor - (int) (Double.doubleToRawLongBits(num) >>> 63);
    }


}
