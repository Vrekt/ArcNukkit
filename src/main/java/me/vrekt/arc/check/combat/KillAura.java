package me.vrekt.arc.check.combat;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Location;
import me.vrekt.arc.check.Check;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.check.result.CheckResult;
import me.vrekt.arc.data.combat.CombatData;
import me.vrekt.arc.utility.entity.EntityAccess;

/**
 * Checks multiple related combat checks.
 */
public final class KillAura extends Check {

    /**
     * Max diffs
     * <p>
     * 75.0 is a safe value.
     * 50 is modest.
     */
    private float maxYawDiff, maxPitchDiff;

    public KillAura() {
        super(CheckType.KILL_AURA);
        enabled(true)
                .cancel(true)
                .cancelLevel(0)
                .notify(true)
                .notifyEvery(1)
                .ban(false)
                .kick(false)
                .build();

        addConfigurationValueWithComment("max-yaw-difference", 50.0,
                "The max difference allowed between where the player should be looking.");
        addConfigurationValueWithComment("max-pitch-difference", 50.0,
                "The max difference allowed between where the player should be looking.");

        if (enabled()) load();
    }

    /**
     * Invoked when the player attacks
     *
     * @param player the player
     * @param entity the entity
     * @param data   their data
     */
    public boolean check(Player player, Entity entity, CombatData data) {
        if (exempt(player)) return false;

        // grab a new result, our entity and player data.
        final CheckResult result = new CheckResult();
        checkDirection(player, data, entity, result);

        // return result.
        return checkViolation(player, result);
    }

    /**
     * Check direction
     *
     * @param player the player
     * @param data   the player data
     * @param result the result
     */
    private void checkDirection(Player player, CombatData data, Entity entity, CheckResult result) {
        final Location playerLocation = player.getLocation();
        final Location entityLocation = entity.getLocation();

        final float yawToEntity = EntityAccess.getYawToEntity(playerLocation, (float) player.yaw, entityLocation);
        final float pitchToEntity = EntityAccess.getPitchToEntity(playerLocation, (float) player.pitch, entityLocation);

        if (yawToEntity >= maxYawDiff) {
            result.setFailed("Yaw difference greater than allowed.")
                    .withParameter("yawToEntity", yawToEntity)
                    .withParameter("maxYawDiff", maxYawDiff);
        }

        if (pitchToEntity >= maxPitchDiff) {
            result.setFailed("Pitch difference greater than allowed.")
                    .withParameter("pitchToEntity", pitchToEntity)
                    .withParameter("maxPitchDiff", maxPitchDiff);
        }
    }

    @Override
    public void reloadConfig() {
        load();
    }

    @Override
    public void load() {
        maxYawDiff = (float) configuration.getDouble("max-yaw-difference");
        maxPitchDiff = (float) configuration.getDouble("max-pitch-difference");
    }
}
