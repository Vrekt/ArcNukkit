package me.vrekt.arc.command.commands;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import me.vrekt.arc.Arc;
import me.vrekt.arc.permissions.Permissions;

/**
 * Allows viewing of debug information
 */
public final class DebugSubCommand extends ArcSubCommand {

    public DebugSubCommand() {
        super(Permissions.ARC_COMMANDS_DEBUG);
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
        return Arc.arc().violations().toggleDebugViewer(player);
    }
}
