package me.vrekt.arc.command.commands;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.permissions.Permissions;

import java.util.List;

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

        final int device = player.getLoginChainData().getDeviceOS();
        final int gameMode = player.getGamemode();
        final boolean op = player.isOp();

        final List<CheckType> bypass = Permissions.getChecksCanBypass(player);
        final String header = "";

        sender.sendMessage(header);
    }
}
