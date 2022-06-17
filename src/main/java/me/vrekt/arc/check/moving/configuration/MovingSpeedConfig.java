package me.vrekt.arc.check.moving.configuration;

import me.vrekt.arc.configuration.check.BasicCheckConfiguration;
import me.vrekt.arc.configuration.check.CheckConfiguration;

/**
 * The moving configuration for speed.
 */
public final class MovingSpeedConfig extends BasicCheckConfiguration {

    /**
     * The base move speeds.
     * Max initial jump boost given to the player.
     * Max speed on ice + block above head
     * Max move speed on stairs
     */
    public double baseMoveSpeedSprint, maxInitialJumpBoost, blockIceMaxSpeed, maxMoveSpeedStairs;

    /**
     * The minimum vertical required to be considered actually in-air/giving boost.
     * The minimum speed required to boost the cooldown for preventing false flags.
     * The amount to multiply the max speed reached by to increase the cooldown.
     * The initial cooldown given to players who did not travel fast enough on ice.
     */
    public double minimumVertical, maxIceSpeedReachedMin, maxIceSpeedReachedCooldownModifier, maxIceSpeedReachedCooldown;

    /**
     * if greater than this number, will flag. Ensures no false positives on first lift off.
     * TODO: Maybe bypass
     */
    public double noVerticalBoostRequired;

    /**
     * The minimum speed required to boost the cooldown for preventing false flags.
     * The amount to multiply the max speed reached by to increase the cooldown.
     * The initial cooldown given to players who did not travel fast enough while low-jumping.
     */
    public double maxLowJumpSpeedReachedMin, maxLowJumpSpeedReachedCooldownModifier, maxLowJumpSpeedReachedCooldown;

    /**
     * The max speed a player can achieve while jumping with a block above their head.
     * The minimum distance required between the last setback location to update it.
     */
    public double maxBlockLowJumpSpeed, minSetbackDistance;

    /**
     * The time required since the last time the player jump boosted with a block above their head.
     * The minimum time required since the last violation to update the setback location.
     */
    public long timeRequiredSinceLastJumpBoost, timeRequiredSinceLastViolation;

    /**
     * The minimum off ice time required to start checking normal ground speed.
     * The minimum off modifier time (slabs/stairs) required to start checking normal ground speed.
     * Time required on ground to start checking normal ground stuff
     */
    public int minimumOffIceTime, minimumOffModifierTime, minimumOnGroundTime;

    @Override
    public void write(CheckConfiguration configuration) {
        configuration.addConfigurationValueWithComment("base-move-speed-sprint", 0.289,
                "The max speed while sprinting.");
        configuration.addConfigurationValueWithComment("minimum-on-ground-time", 10,
                "The time required to be on ground before checking ground speed.");
        configuration.addConfigurationValueWithComment("max-initial-jump-boost", 0.5,
                "The max speed a player can gain from an initial jump.");
        configuration.addConfigurationValueWithComment("max-move-speed-web", 0.099,
                "The max move speed in webs.");
        configuration.addConfigurationValueWithComment("block-ice-speed-max", 1.2,
                "The max speed while jumping on ice.");
        configuration.addConfigurationValueWithComment("max-move-speed-stairs", 0.6,
                "The max speed while jumping on stairs");
        configuration.addConfigurationValueWithComment("minimum-vertical", 0.05,
                "The minimum vertical required to be considered actually in-air/giving boost.");
        configuration.addConfigurationValueWithComment("max-ice-speed-reached-min", 0.4,
                "The minimum speed required to boost the cooldown for preventing false flags.");
        configuration.addConfigurationValueWithComment("max-ice-speed-reached-cooldown-modifier", 20,
                "The amount to multiply the max speed reached by to increase the cooldown.");
        configuration.addConfigurationValueWithComment("max-ice-speed-reached-cooldown", 25,
                "The initial cooldown given to players who did not travel fast enough on ice.");
        configuration.addConfigurationValueWithComment("max-block-low-jump-speed", 0.68,
                "The max speed a player can achieve while jumping with a block above their head.");
        configuration.addConfigurationValueWithComment("max-block-low-jump-speed-reached-min", 0.35,
                "The minimum speed required to boost the cooldown for preventing false flags");
        configuration.addConfigurationValueWithComment("max-block-low-jump-speed-reached-cooldown-modifier", 5,
                "The amount to multiply the max speed reached by to increase the cooldown.");
        configuration.addConfigurationValueWithComment("max-block-low-jump-speed-reached-cooldown", 15,
                "The initial cooldown given to players who did not travel fast enough while low jumping.");
        configuration.addConfigurationValueWithComment("time-required-since-last-jump-boost", 1000,
                "The time required since the last time the player jump boosted with a block above their head.");
        configuration.addConfigurationValueWithComment("minimum-off-ice-time", 8,
                "The minimum off ice time required to start checking normal ground speed.");
        configuration.addConfigurationValueWithComment("minimum-off-modifier-time", 12,
                "The minimum off modifier time (slabs/stairs) required to start checking normal ground speed.");
        configuration.addConfigurationValueWithComment("setback-location-update-distance-criteria", 3.0,
                "The minimum distance required between the last setback location to update it.");
        configuration.addConfigurationValueWithComment("setback-location-update-last-violation-criteria", 5000,
                "The minimum time required since the last violation to update the setback location.");
        configuration.addConfigurationValueWithComment("no-vertical-boost-required", 1,
                "The maximum amount of time allowed before checking normal speed checks.");
    }

    @Override
    public void load(CheckConfiguration configuration) {
        baseMoveSpeedSprint = configuration.getDouble("base-move-speed-sprint");
        maxInitialJumpBoost = configuration.getDouble("max-initial-jump-boost");
        blockIceMaxSpeed = configuration.getDouble("block-ice-speed-max");
        maxMoveSpeedStairs = configuration.getDouble("max-move-speed-stairs");
        minimumOnGroundTime = configuration.getInt("minimum-on-ground-time");
        minimumVertical = configuration.getDouble("minimum-vertical");
        maxIceSpeedReachedMin = configuration.getDouble("max-ice-speed-reached-min");
        maxIceSpeedReachedCooldownModifier = configuration.getDouble("max-ice-speed-reached-cooldown-modifier");
        maxIceSpeedReachedCooldown = configuration.getDouble("max-ice-speed-reached-cooldown");
        maxLowJumpSpeedReachedMin = configuration.getDouble("max-block-low-jump-speed-reached-min");
        maxLowJumpSpeedReachedCooldownModifier = configuration.getDouble("max-block-low-jump-speed-reached-cooldown-modifier");
        maxLowJumpSpeedReachedCooldown = configuration.getDouble("max-block-low-jump-speed-reached-cooldown");
        maxBlockLowJumpSpeed = configuration.getDouble("max-block-low-jump-speed");
        minSetbackDistance = configuration.getDouble("setback-location-update-distance-criteria");
        timeRequiredSinceLastJumpBoost = configuration.getInt("time-required-since-last-jump-boost");
        timeRequiredSinceLastViolation = configuration.getLong("setback-location-update-last-violation-criteria");
        minimumOffIceTime = configuration.getInt("minimum-off-ice-time");
        minimumOffModifierTime = configuration.getInt("minimum-off-modifier-time");
        minimumOnGroundTime = configuration.getInt("minimum-on-ground-time");
        noVerticalBoostRequired = configuration.getInt("no-vertical-boost-required");
    }
}
