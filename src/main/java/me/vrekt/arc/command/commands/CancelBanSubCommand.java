package me.vrekt.arc.command.commands;

import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import me.vrekt.arc.Arc;
import me.vrekt.arc.permissions.Permissions;

/**
 * Allows the sender to cancel player bans
 */
public final class CancelBanSubCommand extends ArcSubCommand {

    public CancelBanSubCommand() {
        super(Permissions.ARC_COMMANDS_CANCEL_BAN);
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        if (arguments.length == 0) {
            sender.sendMessage(TextFormat.RED + "You must provide a player name.");
            return;
        }

        final boolean pending = Arc.arc().punishment().hasPendingBan(arguments[0]);
        if (!pending) {
            sender.sendMessage(TextFormat.RED + "That player does not have a pending ban.");
            return;
        }

        Arc.arc().punishment().cancelBan(arguments[0]);
        sender.sendMessage(TextFormat.GREEN + "The pending ban for " + TextFormat.GRAY + arguments[0] + TextFormat.GREEN + " has been cancelled.");
    }
}
