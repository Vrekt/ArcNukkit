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

        setCommand("/arc reload");
        setUsage("/arc reload");
        setDescription("Allows you to view timings information.");
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        sendMessage(sender, TextFormat.DARK_AQUA + "Reloading....");
        try {
            Arc.getInstance().getArcConfiguration().reloadConfigurationAndComponents();
            sendMessage(sender, TextFormat.DARK_AQUA + "Configuration reloaded.");
        } catch (Exception any) {
            any.printStackTrace();
            sendErrorMessage(sender, "Any internal error occurred, it has been printed to console.");
        }
    }
}
