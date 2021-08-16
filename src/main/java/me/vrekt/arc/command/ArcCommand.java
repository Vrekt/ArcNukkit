package me.vrekt.arc.command;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandExecutor;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import me.vrekt.arc.command.commands.*;
import org.apache.commons.lang3.ArrayUtils;

/**
 * The base command for /arc
 */
public final class ArcCommand extends ArcBaseCommand implements CommandExecutor {

    /**
     * Initialize
     */
    public ArcCommand() {
        initializeSubCommands();
        populateCommandHelp();
    }

    /**
     * Initialize sub commands
     */
    private void initializeSubCommands() {
        addSubCommand("violations", new ToggleViolationsSubCommand());
        addSubCommand("reload", new ReloadConfigSubCommand());
        addSubCommand("timings", new TimingsSubCommand());
        addSubCommand("cancelban", new CancelBanSubCommand());
        addSubCommand("exempt", new ExemptPlayerSubCommand());
        addSubCommand("debug", new DebugSubCommand());
        addSubCommand("version", new ArcVersionSubCommand());
        addSubCommand("history", new ViolationHistorySubCommand());
        addSubCommand("summary", new ArcSummarySubCommand());
    }

    /**
     * Populate the command help line(s)
     */
    private void populateCommandHelp() {
        subCommands.values()
                .forEach((command) ->
                        addHelpLine(command.getPermission(), TextFormat.DARK_AQUA + command.getCommand() + " - "
                                + TextFormat.GRAY + command.getDescription()));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!checkBasePermissions(sender)) return true;
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) return printHelpLines(sender);

        final String argument = args[0];
        // execute sub-commands
        if (isSubCommand(argument)) {
            return executeSubCommand(sender, argument, (String[]) ArrayUtils.remove(args, 0));
        } else {
            // not found, print help.
            return printHelpLines(sender);
        }
    }

}
