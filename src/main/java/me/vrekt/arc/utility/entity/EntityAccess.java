package me.vrekt.arc.utility.entity;

import cn.nukkit.level.Location;
import me.vrekt.arc.utility.math.MathUtil;

/**
 * Entity utility.
 */
public final class EntityAccess {

    /**
     * Get the yaw value required to face the entity.
     *
     * @param player the player
     * @param entity the entity
     * @return the yaw
     */
    public static float getYawToEntity(Location player, float playerYaw, Location entity) {
        final double deltaX = entity.getX() - player.getX();
        final double deltaZ = entity.getZ() - player.getZ();
        double yaw;

        final double atan = (deltaX < 0.0 && deltaZ < 0.0 || deltaX > 0.0 && deltaZ < 0.0) ? Math.atan(deltaZ / deltaX) : -Math.atan(deltaX / deltaZ);
        final double v = Math.toDegrees(atan);

        if (deltaX < 0.0 && deltaZ < 0.0) {
            yaw = 90.0 + v;
        } else if (deltaX > 0.0 && deltaZ < 0.0) {
            yaw = -90.0 + v;
        } else {
            yaw = v;
        }

        return Math.abs(MathUtil.wrapAngle(-(playerYaw - (float) yaw)));
    }

    /**
     * Get the pitch to an entity
     *
     * @param playerLocation the player location
     * @param playerPitch    the players pitch
     * @param entityLocation the entity location
     * @return the pitch
     */
    public static float getPitchToEntity(Location playerLocation, float playerPitch, Location entityLocation) {
        final double deltaX = entityLocation.getX() - playerLocation.getX();
        final double deltaY = entityLocation.getY() - playerLocation.getY();
        final double deltaZ = entityLocation.getZ() - playerLocation.getZ();
        final double horizontal = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        final double pitch = -Math.toDegrees(Math.atan(deltaY / horizontal));
        return Math.abs(MathUtil.wrapAngle(playerPitch - (float) pitch));
    }

}
