package me.vrekt.arc.command.commands;

import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import me.vrekt.arc.Arc;
import me.vrekt.arc.permissions.Permissions;
import me.vrekt.arc.timings.CheckTimings;
import me.vrekt.arc.utility.chat.ColoredChat;

/**
 * Allows viewing of timings
 */
public final class TimingsSubCommand extends ArcSubCommand {

    public TimingsSubCommand() {
        super(Permissions.ARC_COMMANDS_TIMINGS);

        setCommand("/arc timings");
        setUsage("/arc timings");
        setDescription("Allows you to view timings information.");
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        if (!Arc.getInstance().getArcConfiguration().enableCheckTimings()) {
            sendErrorMessage(sender, "Check timings are disabled in the configuration.");
            return;
        }

        CheckTimings.getAllTimings()
                .keySet()
                .forEach(check -> {
                    final double avg = Math.floor((CheckTimings.getAverageTiming(check)) * 1000) / 1000;
                    final double toMs = Math.floor((avg / 1e+6) * 1000) / 1000;

                    ColoredChat.forRecipient(sender)
                            .setMainColor(TextFormat.DARK_AQUA)
                            .setParameterColor(TextFormat.GRAY)
                            .parameter(check.getName())
                            .message(" took on average ")
                            .parameter(avg + "")
                            .message(" ns or ")
                            .parameter(toMs + "")
                            .message(" ms.")
                            .send();
                });
    }
}
