package me.vrekt.arc.command.commands;

import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import me.vrekt.arc.Arc;
import me.vrekt.arc.permissions.Permissions;

/**
 * Simple version command.
 */
public final class ArcVersionSubCommand extends ArcSubCommand {

    public ArcVersionSubCommand() {
        super(Permissions.ARC_COMMANDS_BASE);

        setCommand("/arc version");
        setDescription("Allows you view the current version of Arc.");
        setUsage("/arc version");
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        sendMessage(sender, TextFormat.DARK_AQUA + "Current version of Arc is: " + TextFormat.GRAY + Arc.VERSION_STRING);
    }
}
