package me.vrekt.arc.command.commands;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;

/**
 * A basic sub command.
 */
public abstract class ArcSubCommand {

    /**
     * Permission required
     */
    private final String permission;

    /**
     * The permission required.
     *
     * @param permission permission
     */
    public ArcSubCommand(String permission) {
        this.permission = permission;
    }

    /**
     * Check if the provided {@code sender} has the required {@code permission}
     *
     * @param sender the sender
     * @return {@code true} if so
     */
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission(permission);
    }

    /**
     * Execute this sub-command
     *
     * @param sender    the sender
     * @param arguments the arguments
     */
    public abstract void execute(CommandSender sender, String[] arguments);

    /**
     * Check if the sender is a player
     *
     * @param sender the sender
     * @return {@code true} if so
     */
    protected boolean isPlayer(CommandSender sender) {
        return sender instanceof Player;
    }

    /**
     * Print no player
     *
     * @param sender the sender
     */
    protected void printNoPlayer(CommandSender sender) {
        sender.sendMessage(TextFormat.RED + "You must be a player to do this.");
    }

}
