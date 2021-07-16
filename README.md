# Arc

### What is it?

Arc is an experimental anticheat for Nukkit. It is based off my Bukkit version, which you can find [here](https://github.com/Vrekt/Arc)

The goal of Arc is to be highly performant, configurable and easy to work with.

Checks are designed to be more leniant allowing smooth gameplay, but not allowing anything abnormal. (This of course can be changed through configuration)

# Features
- Extensive configuration and permission system
- Customizable check configurations
- Performant
- More to come soon...

# Requirements
- Java 8

# Permissions 
Arc has a very extensive permissions system. You can choose to bypass all checks, a category all together or a singular check.

*Command access*
- `arc.commands.all` Allows access to all /arc commands.
- `arc.commands.base` Allows access to the base /arc command.
- `arc.commands.toggleviolations` Allows a player to toggle violations.
- `arc.commands.cancelban` Allows a player to cancel a pending ban.
- `arc.commands.reloadconfig` Allows a player to reload the Arc configuration.
- `arc.commands.debug` Allows a player to view debug information with each violation.

*Bypass all checks*
- `arc.bypass` Allows you to bypass all checks.

*Bypass all checks within a category*
- `arc.bypass.moving` Allows you to bypass all moving checks.

*Bypass a singular check*
- `arc.bypass.moving.morepackets` Allows you to bypass the MorePackets check.

*View player violations as they come in*
- `arc.violations` Allows you to view violations.

# Configuration
Configuration documentation for checks has not been written yet.

However, you can view documentation for all the other configuration values in the config.yml file in this repository.

# Checks

Please note not all checks are finished, and not all checks fully block that certain cheat, **YET**

###### Flight
- Jumping too high
- Climbing a ladder too fast
- Jumping for too long (air jump, spider, etc)
- More to come.

###### FastUse
- Checks if the player is eating too fast.

###### MorePackets
- Prevents the player from sending too many movement packets.
- Prevents things like timer and regeneration
- Prevents blink

**TODO**


