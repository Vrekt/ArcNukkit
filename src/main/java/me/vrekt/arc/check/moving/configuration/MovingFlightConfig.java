package me.vrekt.arc.check.moving.configuration;

import me.vrekt.arc.configuration.check.BasicCheckConfiguration;
import me.vrekt.arc.configuration.check.CheckConfiguration;

/**
 * The moving config for flight
 */
public final class MovingFlightConfig extends BasicCheckConfiguration {

    /**
     * The max jump distance.
     * Max climbing speeds
     * The amount of time a player has to be on a climbable
     * <p>
     * The distance (from the ground) required to start checking ascending stuff.
     * The distance (from the ground) (horizontal) that is capped, if the hDist > capped, no check is executed.
     * <p>
     * Ground distance threshold is lenient here to account for bedrock movement.
     */
    public double maxJumpDistance, maxClimbSpeedUp, maxClimbSpeedDown, climbingCooldown, groundDistanceThreshold, groundDistanceHorizontalCap;

    /**
     * The max ascend time
     * The amount to add to {@code maxAscendTime} when the player has jump boost.
     * Ascend cooldown work around.
     * Max time allowed to b e hovering
     */
    public int maxAscendTime, jumpBoostAscendAmplifier, ascendCooldown, maxInAirHoverTime, noGlideDifferenceMax;

    /**
     * No reset ascend checks if the player is ascending too high.
     * Players previously could bypass regular ascend check by ascending slowly and descending every now and again.
     * This check does not reset the players ascend time if they descend.
     * <p>
     * The distance needed away from ground to start checking a no reset ascend.
     * The max amount of ascending moves allowed.
     */
    public double noResetAscendGroundDistanceThreshold, maxNoResetAscendMoves;

    /**
     * The minimum time needed to be descending to check glide.
     * The minimum distance away from ground needed to check glide, 0.85 = player jump
     * The max difference allowed between calculated fall velocity and actual fall velocity.
     */
    public double glideDescendTimeMin, glideDescendDistanceMin, glideMaxDifference;

    @Override
    public void write(CheckConfiguration configuration) {
        configuration.addConfigurationValueWithComment("max-jump-distance", 0.422,
                "The max height a player can jump normally.");
        configuration.addConfigurationValueWithComment("max-climbing-speed-up", 0.21,
                "The max speed at which a player can climb a ladder.");
        configuration.addConfigurationValueWithComment("max-climbing-speed-down", 0.21,
                "The max speed at which a player can climb a ladder.");
        configuration.addConfigurationValueWithComment("climbing-cooldown", 7,
                "The amount of time a player has to be climbing a ladder to start checking.");
        configuration.addConfigurationValueWithComment("max-ascend-time", 7,
                "The max amount of time a player can be ascending for.");
        configuration.addConfigurationValueWithComment("ascend-cooldown", 3,
                "to be removed");
        configuration.addConfigurationValueWithComment("jump-boost-ascend-amplifier", 3,
                "The amount to add to the max ascend time when the player has jump boost.");
        configuration.addConfigurationValueWithComment("ground-distance-threshold", 2.0,
                "The minimum distance required to be away from the ground to start checking ascending moves.");
        configuration.addConfigurationValueWithComment("ground-distance-horizontal-cap", 0.50,
                "The maximum distance allowed horizontally from the ground to check ascending moves.");
        configuration.addConfigurationValueWithComment("max-in-air-hover-time", 6,
                "The max amount of time a player is allowed to be in air without ascending or descending.");
        configuration.addConfigurationValueWithComment("no-glide-difference-max", 2,
                "The max amount of a time a player can have no difference in their descending speed.");
        configuration.addConfigurationValueWithComment("no-reset-ascend-ground-distance-threshold", 1,
                "The minimum distance required to be away from the ground to check alternate ascending moves.");
        configuration.addConfigurationValueWithComment("max-no-reset-ascend-moves", 10,
                "The max amount of ascending moves allowed that don't reset.");
        configuration.addConfigurationValueWithComment("glide-descend-time-min", 5,
                "The minimum time a player should be descending to start checking descending.");
        configuration.addConfigurationValueWithComment("glide-descend-distance-min", 1.6,
                "The minimum distance a player should be away from the ground to start checking descending.");
        configuration.addConfigurationValueWithComment("glide-max-difference", 0.010,
                "The maximum difference allowed between the expected fall distance and the actual fall distance.");
    }

    @Override
    public void load(CheckConfiguration configuration) {
        maxJumpDistance = configuration.getDouble("max-jump-distance");
        maxClimbSpeedUp = configuration.getDouble("max-climbing-speed-up");
        maxClimbSpeedDown = configuration.getDouble("max-climbing-speed-down");
        climbingCooldown = configuration.getDouble("climbing-cooldown");
        maxAscendTime = configuration.getInt("max-ascend-time");
        ascendCooldown = configuration.getInt("ascend-cooldown");
        jumpBoostAscendAmplifier = configuration.getInt("jump-boost-ascend-amplifier");
        groundDistanceThreshold = configuration.getDouble("ground-distance-threshold");
        groundDistanceHorizontalCap = configuration.getDouble("ground-distance-horizontal-cap");
        maxInAirHoverTime = configuration.getInt("max-in-air-hover-time");
        noGlideDifferenceMax = configuration.getInt("no-glide-difference-max");
        noResetAscendGroundDistanceThreshold = configuration.getDouble("no-reset-ascend-ground-distance-threshold");
        maxNoResetAscendMoves = configuration.getDouble("max-no-reset-ascend-moves");
        glideDescendTimeMin = configuration.getInt("glide-descend-time-min");
        glideDescendDistanceMin = configuration.getDouble("glide-descend-distance-min");
        glideMaxDifference = configuration.getDouble("glide-max-difference");
    }
}
