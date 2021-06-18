package me.vrekt.arc.command.commands;

import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import me.vrekt.arc.Arc;
import me.vrekt.arc.permissions.Permissions;

/**
 * Allows reloading of the config
 */
public final class ReloadConfigSubCommand extends ArcSubCommand {

    public ReloadConfigSubCommand() {
        super(Permissions.ARC_COMMANDS_RELOAD_CONFIG);
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        sender.sendMessage(TextFormat.RED + "Reloading....");
        try {
            Arc.arc().configuration().reloadConfiguration();
            sender.sendMessage(TextFormat.GREEN + "Configuration reloaded.");
        } catch (Exception any) {
            any.printStackTrace();
            sender.sendMessage(TextFormat.RED + "Any internal error occurred, it has been printed to console.");
        }
    }
}
