package me.vrekt.arc.command;


import cn.nukkit.command.Command;
import cn.nukkit.command.CommandExecutor;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import me.vrekt.arc.Arc;
import me.vrekt.arc.command.commands.CancelBanSubCommand;
import me.vrekt.arc.command.commands.ReloadConfigSubCommand;
import me.vrekt.arc.command.commands.ToggleViolationsSubCommand;
import me.vrekt.arc.permissions.Permissions;
import org.apache.commons.lang3.ArrayUtils;

/**
 * The base command for /arc
 */
public final class ArcCommand extends ArcBaseCommand implements CommandExecutor {

    /**
     * Initialize
     */
    public ArcCommand() {
        // initialize sub commands
        addSubCommand("violations", new ToggleViolationsSubCommand());
        addSubCommand("reload", new ReloadConfigSubCommand());
        addSubCommand("cancelban", new CancelBanSubCommand());

        // initialize help message.
        final String prefix = Arc.arc().configuration().prefix();
        helpLine(Permissions.ARC_COMMANDS_TOGGLE_VIOLATIONS, prefix + TextFormat.DARK_AQUA + " /arc violations - " + TextFormat.GRAY + "Toggle violations on or off.");
        helpLine(Permissions.ARC_COMMANDS_RELOAD_CONFIG, prefix + TextFormat.DARK_AQUA + " /arc reload - " + TextFormat.GRAY + "Reloads the configuration.");
        helpLine(Permissions.ARC_COMMANDS_CANCEL_BAN, prefix + TextFormat.DARK_AQUA + " /arc cancelban <player> - " + TextFormat.GRAY + "Cancel a pending player ban.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!checkBasePermissions(sender)) return true;
        if (args.length == 0) {
            // display inventory
            return printHelpLines(sender);
        }

        final String argument = args[0];
        // execute help command
        if (help(argument)) return printHelpLines(sender);
        // execute sub-commands
        if (isSubCommand(argument)) {
            return executeSubCommand(sender, argument, ArrayUtils.remove(args, 0));
        } else {
            // not found, print help.
            return printHelpLines(sender);
        }
    }

}
