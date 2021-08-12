package me.vrekt.arc.command.commands;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import me.vrekt.arc.Arc;
import me.vrekt.arc.permissions.Permissions;

/**
 * Toggle player violations
 */
public final class ToggleViolationsSubCommand extends ArcSubCommand {

    public ToggleViolationsSubCommand() {
        super(Permissions.ARC_COMMANDS_TOGGLE_VIOLATIONS);

        setCommand("/arc violations");
        setUsage("/arc violations");
        setDescription("Allows you to toggle violation messages.");
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        if (!isPlayer(sender)) {
            printNoPlayer(sender);
            return;
        }

        final Player player = (Player) sender;
        final boolean state = toggleState(player);
        sendMessage(player, TextFormat.DARK_AQUA + "Violations are now " + (state ? TextFormat.GREEN + "on." : TextFormat.RED + "off."));
    }

    /**
     * Toggle state
     *
     * @param player the player
     * @return {@code true} if violations are on.
     */
    private boolean toggleState(Player player) {
        return Arc.getInstance().getViolationManager().toggleViolationsViewer(player);
    }

}
