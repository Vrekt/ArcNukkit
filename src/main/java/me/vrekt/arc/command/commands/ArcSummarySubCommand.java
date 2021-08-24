package me.vrekt.arc.command.commands;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import me.vrekt.arc.Arc;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.permissions.Permissions;
import me.vrekt.arc.utility.chat.ColoredChat;
import me.vrekt.arc.utility.misc.DeviceType;
import me.vrekt.arc.violation.ViolationHistory;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;
import java.util.stream.Collectors;

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

        final List<String> bypass = Permissions.getChecksCanBypass(player)
                .stream().map(CheckType::getName)
                .collect(Collectors.toList());
        final ColoredChat message = ColoredChat.forRecipient(sender);

        final int playerDevice = player.getLoginChainData().getDeviceOS();
        final int playerPing = player.getPing();

        // TODO: Mapping of devices.
        final String playerDeviceId = DeviceType.getByCode(playerDevice).getPrettyName();
        final String checksBypassed = bypass.isEmpty() ? " cannot bypass any checks."
                : " can bypass " + ArrayUtils.toString(bypass);
        final String playerGameMode = player.getGamemode() == 0 ? "Survival"
                : player.getGamemode() == 1 ? "Creative"
                : player.getGamemode() == 2 ? "Adventure"
                : player.getGamemode() == 3 ? "Spectator"
                : "Survival";

        message.setMainColor(TextFormat.AQUA).setParameterColor(TextFormat.GRAY);
        message.messagePrefix("Viewing summary report for: ").parameter(player.getName()).newLine();
        message.parameterPrefix(player.getName()).message(" is playing on ").parameter(playerDeviceId).newLine();
        message.parameterPrefix(player.getName()).message(" is currently in ").parameter(playerGameMode).newLine();
        message.parameterPrefix(player.getName()).message(checksBypassed).newLine();
        message.messagePrefix("The ping of ").parameter(player.getName()).message(" is ").parameter(playerPing + " ms.").newLine();

        final ViolationHistory history = Arc.getInstance().getViolationManager().getHistory().get(player.getUniqueId());
        if (history != null) {
            history.getViolations().forEach((check, level) -> message.parameterPrefix(player.getName())
                    .message(" failed ")
                    .parameter(check.getName())
                    .parameter(" " + level)
                    .message(" times.")
                    .newLine());
        }

        message.send();
    }
}
