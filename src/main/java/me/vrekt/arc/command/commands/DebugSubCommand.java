package me.vrekt.arc.command.commands;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import me.vrekt.arc.Arc;
import me.vrekt.arc.permissions.Permissions;

/**
 * Allows the sender to enable or disable debug messages
 */
public final class DebugSubCommand extends ArcSubCommand {

    public DebugSubCommand() {
        super(Permissions.ARC_COMMANDS_DEBUG);

        setCommand("/arc debug");
        setUsage("/arc debug");
        setDescription("Allows you to toggle debug messaging.");
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        if (!isPlayer(sender)) {
            printNoPlayer(sender);
            return;
        }

        final Player player = (Player) sender;
        final boolean state = toggleState(player);
        player.sendMessage(TextFormat.GRAY + "Debug information is now " + (state ? TextFormat.GREEN + "on." : TextFormat.RED + "off."));
    }

    /**
     * Toggle state
     *
     * @param player the player
     * @return {@code true} if debug info is on.
     */
    private boolean toggleState(Player player) {
        return Arc.getInstance().getViolationManager().toggleDebugViewer(player);
    }
}
