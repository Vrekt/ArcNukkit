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
     * The command string, for example: /arc cancelban
     * The description of the command
     * The usage of this command
     */
    private String command, description, usage;

    /**
     * The permission required.
     *
     * @param permission permission
     */
    public ArcSubCommand(String permission) {
        this.permission = permission;
    }

    /**
     * Set the command
     *
     * @param command the command
     */
    public void setCommand(String command) {
        this.command = command;
    }

    /**
     * Set the description message
     *
     * @param description description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Set the usage message
     *
     * @param usage usage
     */
    public void setUsage(String usage) {
        this.usage = usage;
    }

    /**
     * @return the permission
     */
    public String getPermission() {
        return permission;
    }

    /**
     * @return the command
     */
    public String getCommand() {
        return command;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the usage
     */
    public String getUsage() {
        return usage;
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

    /**
     * Print usage
     *
     * @param sender the sender
     */
    protected void printUsage(CommandSender sender) {
        sender.sendMessage(TextFormat.RED + "Usage: " + getUsage());
    }

    /**
     * Send a message
     *
     * @param sender  the sender
     * @param message the message
     */
    protected void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(message);
    }

    /**
     * Send an error message
     *
     * @param sender  the sender
     * @param message the message
     */
    protected void sendErrorMessage(CommandSender sender, String message) {
        sender.sendMessage(TextFormat.RED + message);
    }

}
