package me.vrekt.arc.check.combat;


import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;
import me.vrekt.arc.check.Check;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.check.result.CheckResult;

/**
 * Checks if the player is attacking from too far away.
 * The default config is optimized to REDUCE false positives.
 * Its not strict what-so-ever.
 * <p>
 * Check imported from https://github.com/Vrekt/Arc
 */
public final class Reach extends Check {

    /**
     * Max survival and creative distances.
     * The default eye height;
     */
    private double maxSurvivalDistance, maxCreativeDistance, defaultEyeHeight;

    /**
     * If the Y axis should be ignored.
     * If the eye height should be subtracted.
     */
    private boolean ignoreVerticalAxis, subtractEyeHeight;

    /**
     * If velocities should be subtracted.
     */
    private boolean subtractPlayerVelocity, subtractEntityVelocity;

    public Reach() {
        super(CheckType.REACH);
        enabled(true)
                .cancel(true)
                .cancelLevel(0)
                .notify(true)
                .notifyEvery(1)
                .ban(false)
                .kick(false)
                .build();

        addConfigurationValueWithComment("max-survival-distance", 2.66,
                "The max reach distance for players in survival.");
        addConfigurationValueWithComment("max-creative-distance", 6,
                "The max reach distance for players in creative.");
        addConfigurationValueWithComment("ignore-vertical-axis", true,
                "If the Y values of players and entities should be ignored.");
        addConfigurationValueWithComment("subtract-eye-height", true,
                "If the players and entities eye heights should be subtracted from the final result.");
        addConfigurationValueWithComment("default-eye-height", 1.75,
                "The default eye height.");
        addConfigurationValueWithComment("subtract-player-velocity", true,
                "If the knockback of players should be accounted for.");
        addConfigurationValueWithComment("subtract-entity-velocity", true,
                "If the knockback of entities should be accounted for.");
        if (enabled()) load();
    }

    /**
     * Invoked when we interact with an entity.
     *
     * @param player the player
     * @param entity the entity
     */
    public boolean check(Player player, Entity entity, Vector3 motion) {
        if (exempt(player)) return false;
        final CheckResult result = new CheckResult();

        // retrieve our recent location and the entity location.
        final Vector3f location = player.getLocation().clone().asVector3f();
        final Vector3f entityLocation = entity.getLocation().clone().asVector3f();

        // if ignore y values, just set them to 0.
        if (ignoreVerticalAxis) {
            location.y = 0;
            entityLocation.y = 0;
        }

        // retrieve the combined subtracted eye height for later.
        final double livingEyeHeight = entity.getEyeHeight();
        final double playerEyeHeight = player.getEyeHeight();
        final double subtractAmount = livingEyeHeight == 0.0 ? defaultEyeHeight : livingEyeHeight == playerEyeHeight ? 0 : livingEyeHeight;
        final double eyeHeight = subtractEyeHeight ? Math.abs(player.getEyeHeight() - subtractAmount) : 0.0;

        // subtract the velocities.
        // TODO Won't be that significant I don't think.
        final Vector3 entityVelocity = entity.getMotion();
        if (subtractPlayerVelocity) location.subtract((float) motion.x, (float) motion.y, (float) motion.z);
        if (subtractEntityVelocity)
            entityLocation.subtract((float) entityVelocity.x, (float) entityVelocity.y, (float) entityVelocity.z);
        // finally, calculate the distance
        final double distance = location.distance(entityLocation) - eyeHeight;
        // retrieve the allowed amount
        final double allowed = player.gamemode == 1 ? maxCreativeDistance : maxSurvivalDistance;
        if (distance > allowed) {
            result.setFailed("Distance greater than allowed.")
                    .withParameter("distance", distance)
                    .withParameter("allowed", allowed)
                    .withParameter("ignore-y", ignoreVerticalAxis)
                    .withParameter("eyeHeight", eyeHeight)
                    .withParameter("vel", motion)
                    .withParameter("entityVel", entityVelocity);
        }

        return checkViolation(player, result);
    }

    @Override
    public void reloadConfig() {
        load();
    }

    @Override
    public void load() {
        maxSurvivalDistance = configuration.getDouble("max-survival-distance");
        maxCreativeDistance = configuration.getDouble("max-creative-distance");
        ignoreVerticalAxis = configuration.getBoolean("ignore-vertical-axis");
        subtractEyeHeight = configuration.getBoolean("subtract-eye-height");
        defaultEyeHeight = configuration.getDouble("default-eye-height");
        subtractPlayerVelocity = configuration.getBoolean("subtract-player-velocity");
        subtractEntityVelocity = configuration.getBoolean("subtract-entity-velocity");
    }
}
