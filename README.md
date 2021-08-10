# Arc

Arc is an experimental anticheat for Nukkit. It is a "fork" of my Bukkit version, which you can find on my page. Arc is still in the very early stages of development and as such alot of checks are missing, incomplete or not fully functional yet.

Regardless, The goal of Arc is to be highly performant, configurable and easy to work with. Checks are designed for the smoothest gameplay, while still being very effective. Every check is very customizable and features many vaules within the configuration allowing you to tune exactly how it works within your server.

## Features

#### Configurable
- Customize checks
- Customize violations
- Customize messages
- Customize punishment
- And more!

#### Performant

Arc is built with performance in mind with every check and feature, and optimizations are constantly being added. In the future, there will be tick/lag watch features and Arc will take into account the server TPS and adjust accordingly.

#### Permissions
- Permissions for each command
- Permissions for each check
- Permissions for check categories
- Permissions for administrative actions

#### Commands
- Toggle violations on or off.
- Toggle debug messages on or off.
- Cancel a ban that was scheduled by Arc
- Configuration reloading for easy modification during up-time.

#### Misc

The base of Arc is very stable and easy to work with. This allows new features or checks to be implemented easily, or for anybody else to add something!

## Permissions 
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

## Checks

Please note not all checks are finished, and not all checks fully block that certain cheat, **YET**

###### Flight
- Prevents the player from jumping too high.
- Prevents the player from climbing vines or ladders too fast.
- Prevents the player from jumping/ascending for too long. (AirJump, Spider, etc)
- Prevents the player from hovering. (AirStuck, etc)
- Prevents the player from bouncing really high on slimeblocks
- work-in-progress

###### FastUse
- Checks if the player is eating or drinking potions too fast.

###### MorePackets
- Prevents the player from sending too many position packets.
- Prevents the player from using timer cheats and regeneration.
- Prevents the player from 'blinking'

###### Nuker
- Prevents the player from breaking an impossible amount of blocks.

###### FastBreak
- Prevents the player from breaking blocks too fast.

## Configuration

You can view each configuration value and what they mean/do in the wiki [here](https://github.com/Vrekt/ArcNukkit/wiki)

## In-depth 

### Extensive configuration

Arc allows you to customize checks, violations, messages, banning, kicking, and a few advanced features within Arc. You can also customize check parameters allowing you to tune the check to your players needs.

For example, in the `MorePackets` check you can change how many packets are allowed per second. This is useful for laggy players or a laggy server.

`max-moves-per-second: 25 -> max-moves-per-second: 30`

With this change, there is more headroom for lag and thus a smoother experience while still being safe.

**All check configurations are already tested and are optimized, so in most cases you shouldn't need to change much.**

### Performant

Arc is built with performance in mind with every check and feature, and optimizations are constantly being added.

In the future, there will be tick/lag watch features and Arc will take into account the server TPS and adjust accordingly.

### Permissions

Arc includes a very extensive permission set and system. Arc has permissions for every command, check category and check. This allows you to fully disable certain categories for players, or just disable one check.

For example, giving the player the permission `arc.bypass.moving` will allow them to bypass **ALL** moving checks. 

But assigning `arc.bypass.moving.flight` will allow them to bypass **ONLY** the flight check.

### Commands

Another goal of Arc is to provide powerful administration tools. These features are still work-in-progress but right now there are a few.

At the moment Arc allows you to cancel bans scheduled, enable or disable violation messages, enable or disable debugging and the ability to reload the configuration anytime via a command.

### Other

Arc is able to keep player violation data when they logout for a set period of time. This prevents players from re-logging to restate their state within the Anticheat. This timeout value is also configurable. 

The base of Arc is very strong and easy to work with. This allows anybody to easily fork or implement and feature or check they want.
