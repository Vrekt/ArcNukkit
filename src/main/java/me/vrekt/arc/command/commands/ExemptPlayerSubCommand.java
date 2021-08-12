package me.vrekt.arc.command.commands;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import me.vrekt.arc.Arc;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.permissions.Permissions;
import me.vrekt.arc.utility.chat.ColoredChat;

/**
 * A sub command for exempting from checks
 */
public final class ExemptPlayerSubCommand extends ArcSubCommand {

    public ExemptPlayerSubCommand() {
        super(Permissions.ARC_COMMANDS_EXEMPT);

        setCommand("/arc exempt (player) (checkName or all)");
        setUsage("/arc exempt (player) (checkName or all)");
        setDescription("Allows you to exempt players manually.");
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        if (arguments.length == 0) {
            printUsage(sender);
            return;
        }


        Player toExempt;
        boolean isMyself = false;
        if (arguments.length == 1) {
            if (!isPlayer(sender)) {
                sendErrorMessage(sender, "You must be a player to exempt yourself.");
                return;
            }

            toExempt = (Player) sender;
            isMyself = true;
        } else {
            toExempt = Server.getInstance().getPlayer(arguments[0]);
            if (toExempt == null) {
                sendErrorMessage(sender, "That player must be online to be exempted.");
                return;
            }
        }

        final String check = arguments.length == 1 ? arguments[0] : arguments[1];
        if (check.equalsIgnoreCase("all")) {
            Arc.getInstance().getExemptionManager().addExemptionPermanently(toExempt, CheckType.values());

            ColoredChat.forRecipient(sender)
                    .setMainColor(TextFormat.DARK_AQUA)
                    .setParameterColor(TextFormat.GRAY)
                    .messageIf(isMyself, "You are now exempt from all checks.")
                    .parameterIf(!isMyself, toExempt.getName())
                    .messageIf(!isMyself, " is now exempt from all checks.")
                    .send();
        } else {
            final CheckType checkType = CheckType.getCheckTypeByName(check);
            if (checkType == null) {
                sendErrorMessage(sender, "Check not found.");
                return;
            }

            Arc.getInstance().getExemptionManager().addExemptionPermanently(toExempt, checkType);

            ColoredChat.forRecipient(sender)
                    .setMainColor(TextFormat.DARK_AQUA)
                    .setParameterColor(TextFormat.GRAY)
                    .messageIf(isMyself, "You are now exempt from ")
                    .parameterIf(isMyself, checkType.getName())
                    .parameterIf(!isMyself, toExempt.getName())
                    .messageIf(!isMyself, " is now exempt from ")
                    .parameterIf(!isMyself, checkType.getName())
                    .send();
        }

        ColoredChat.forRecipient(sender)
                .message("WARNING: ", TextFormat.GOLD, TextFormat.BOLD)
                .message("ExemptionHistory added via commands will not persist over a sever reload, plugin reload or restart.", TextFormat.YELLOW)
                .send();
    }

}
