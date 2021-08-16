package me.vrekt.arc.command.commands;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import me.vrekt.arc.permissions.Permissions;
import me.vrekt.arc.utility.chat.ColoredChat;

/**
 * Allows you to view a summary about a player.
 */
public final class ArcSummarySubCommand extends ArcSubCommand {

    public ArcSummarySubCommand() {
        super(Permissions.ARC_COMMANDS_SUMMARY);

        setCommand("/arc summary (player)");
        setDescription("Allows you view a summary of another player");
        setUsage("/arc (summary) (player)");

    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        if (arguments.length == 0) {
            printUsage(sender);
            return;
        }

        final Player player = Server.getInstance().getPlayer(arguments[0]);
        if (player == null) {
            sendErrorMessage(sender, "That player must be online to view their summary.");
            return;
        }

        ColoredChat.forRecipient(sender)
                .setMainColor(TextFormat.DARK_AQUA)
                .setParameterColor(TextFormat.GRAY)
                .messagePrefix(TextFormat.STRIKETHROUGH + "------------------\n")
                .messagePrefix("Viewing player summary for ")
                .parameterPrefix(player.getName())
                .messagePrefix(TextFormat.STRIKETHROUGH + "\n------------------\n")
                .messagePrefix("Device: ")
                .parameterPrefix(player.getLoginChainData().getDeviceOS() + "")
                .send();
    }
}
