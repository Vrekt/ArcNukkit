package me.vrekt.arc.command;


import cn.nukkit.command.CommandSender;
import me.vrekt.arc.Arc;
import me.vrekt.arc.command.commands.ArcSubCommand;
import me.vrekt.arc.permissions.Permissions;

import java.util.HashMap;
import java.util.Map;

/**
 * Arc base command
 */
public abstract class ArcBaseCommand {

    /**
     * Map of sub-commands.
     */
    protected final Map<String, ArcSubCommand> subCommands = new HashMap<>();

    /**
     * Map of help-lines
     */
    private final Map<String, String> helpLines = new HashMap<>();

    /**
     * Add a sub command
     *
     * @param name    the name
     * @param command the command
     */
    protected void addSubCommand(String name, ArcSubCommand command) {
        subCommands.put(name, command);
    }

    /**
     * Add a help line.
     *
     * @param permission the permission
     * @param line       the line
     */
    protected void addHelpLine(String permission, String line) {
        helpLines.put(permission, Arc.getInstance().getArcConfiguration().getPrefix() + " " + line);
    }

    /**
     * Check if the argument is a sub-command
     *
     * @param argument the argument
     * @return {@code true} if so
     */
    protected boolean isSubCommand(String argument) {
        return subCommands.containsKey(argument.toLowerCase());
    }

    /**
     * Execute a sub-command
     *
     * @param sender    the sender
     * @param arguments the arguments
     * @return {@code true}
     */
    protected boolean executeSubCommand(CommandSender sender, String command, String[] arguments) {
        final ArcSubCommand subCommand = subCommands.get(command);
        if (sender.hasPermission(Permissions.ARC_COMMANDS_ALL) || subCommand.hasPermission(sender) || sender.isOp()) {
            subCommand.execute(sender, arguments);
        } else {
            sender.sendMessage(Arc.getInstance().getArcConfiguration().getNoPermissionMessage());
        }
        return true;
    }

    /**
     * Check base permissions
     *
     * @param sender the sender
     * @return {@code false} if no permission
     */
    protected boolean checkBasePermissions(CommandSender sender) {
        if (!sender.hasPermission(Permissions.ARC_COMMANDS_BASE)
                || !sender.hasPermission(Permissions.ARC_COMMANDS_ALL) || !sender.isOp()) {
            sender.sendMessage(Arc.getInstance().getArcConfiguration().getNoPermissionMessage());
            return false;
        }
        return true;
    }

    /**
     * Print help lines
     *
     * @param sender the sender
     * @return {@code true}
     */
    protected boolean printHelpLines(CommandSender sender) {
        final StringBuilder builder = new StringBuilder();
        helpLines.forEach((permission, line) -> {
            if (sender.hasPermission(permission)) {
                builder.append(line);
                builder.append("\n");
            }
        });

        sender.sendMessage(builder.toString());
        return true;
    }

}
