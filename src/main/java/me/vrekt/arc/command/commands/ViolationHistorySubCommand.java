package me.vrekt.arc.command.commands;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import me.vrekt.arc.Arc;
import me.vrekt.arc.permissions.Permissions;
import me.vrekt.arc.utility.chat.ColoredChat;
import me.vrekt.arc.violation.ViolationHistory;

/**
 * Allows the sender to view violation history for a player.
 */
public final class ViolationHistorySubCommand extends ArcSubCommand {

    public ViolationHistorySubCommand() {
        super(Permissions.ARC_COMMANDS_VIOLATION_HISTORY);

        setCommand("/arc history (player)");
        setUsage("/arc history (player)");
        setDescription("Allows you to view violation history for a player.");
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        if (arguments.length == 0) {
            printUsage(sender);
            return;
        }

        final Player player = Server.getInstance().getPlayer(arguments[0]);
        if (player == null) {
            sendErrorMessage(sender, "That player is not online.");
            return;
        }

        final ViolationHistory history = Arc.getInstance().getViolationManager().getHistory().get(player.getUniqueId());

        if (history.getViolations().size() == 0) {
            sendErrorMessage(sender, "That player does not have any violations yet.");
            return;
        }

        final ColoredChat message = ColoredChat.forRecipient(sender);
        int totalViolations = 0;
        for (Integer value : history.getViolations().values()) {
            totalViolations += value;
        }

        message.setMainColor(TextFormat.DARK_AQUA);
        message.setParameterColor(TextFormat.GRAY);

        message.messagePrefix("Showing violation history for ");
        message.parameter(player.getName() + ":");
        message.parameter(" (" + totalViolations + " total violations.)\n");

        history.getViolations().forEach((check, level) ->
                message.parameterPrefix(check.getName())
                        .message(" was failed ")
                        .parameter(level + "")
                        .message(" times.\n"));

        message.send();
    }
}
