# ArcNukkit

**An experimental anticheat for Nukkit.**

*This version is a fork of my Bukkit/Spigot version of Arc.*

*Most of the base is the same as the Bukkit one, meaning it has a-lot of the same features!*

# Features
- Extensive configuration and permission system
- Customizable check configurations
- Performant
- More to come soon...

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
- FastUse
- MorePackets
- Flight
- TODO.


