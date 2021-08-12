package me.vrekt.arc.command.commands;

import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import me.vrekt.arc.Arc;
import me.vrekt.arc.permissions.Permissions;
import me.vrekt.arc.utility.chat.ColoredChat;

/**
 * Allows the sender to cancel player bans
 */
public final class CancelBanSubCommand extends ArcSubCommand {

    public CancelBanSubCommand() {
        super(Permissions.ARC_COMMANDS_CANCEL_BAN);

        setCommand("/arc cancelban (player)");
        setDescription("Allows you to cancel a pending ban.");
        setUsage("/arc cancelban (player)");
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        if (arguments.length == 0) {
            printUsage(sender);
            return;
        }

        final String playerName = arguments[0];
        final boolean pending = Arc.getInstance().getPunishmentManager().hasPendingBan(playerName);
        if (!pending) {
            sendErrorMessage(sender, TextFormat.RED + "That player does not have a pending ban.");
            return;
        }

        Arc.getInstance().getPunishmentManager().cancelBan(playerName);
        ColoredChat.forRecipient(sender)
                .setMainColor(TextFormat.DARK_AQUA)
                .setParameterColor(TextFormat.GRAY)
                .message("The pending ban for ")
                .parameter(playerName)
                .message(" has been cancelled.");
    }
}
