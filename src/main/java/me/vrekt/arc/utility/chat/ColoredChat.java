package me.vrekt.arc.utility.chat;

import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import me.vrekt.arc.Arc;

/**
 * Colored chat utility.
 */
public final class ColoredChat {

    /**
     * Initialize a new instance for the sender
     *
     * @param sender the sender
     * @return a new instance
     */
    public static ColoredChat forRecipient(CommandSender sender) {
        return new ColoredChat(sender);
    }

    /**
     * The recipient
     */
    private final CommandSender sender;

    /**
     * Main chat color.
     */
    private TextFormat mainColor, parameterColor;

    /**
     * The builder used for building messages.
     */
    private final StringBuilder builder = new StringBuilder();

    private ColoredChat(CommandSender sender) {
        this.sender = sender;
    }

    /**
     * Set the main text color
     *
     * @param color the color
     * @return this
     */
    public ColoredChat setMainColor(TextFormat color) {
        this.mainColor = color;
        return this;
    }

    /**
     * Set the color to use when parameters are added
     *
     * @param color the color
     * @return this
     */
    public ColoredChat setParameterColor(TextFormat color) {
        this.parameterColor = color;
        return this;
    }

    /**
     * Append a message
     *
     * @param message the message
     * @return this
     */
    public ColoredChat message(String message) {
        builder.append(mainColor).append(message);
        return this;
    }

    /**
     * Append a message with multiple colors
     *
     * @param message the message
     * @param colors  the colors
     * @return this
     */
    public ColoredChat message(String message, TextFormat... colors) {
        for (TextFormat color : colors) {
            builder.append(color);
        }
        builder.append(message);
        return this;
    }

    /**
     * Append a message if the provided {@code condition} is {@code true}
     *
     * @param condition the condition
     * @param message   the message
     * @return this
     */
    public ColoredChat messageIf(boolean condition, String message) {
        if (condition) builder.append(mainColor).append(message);
        return this;
    }

    /**
     * Append a parameter
     *
     * @param parameter the parameter
     * @return this
     */
    public ColoredChat parameter(String parameter) {
        builder.append(parameterColor).append(parameter);
        return this;
    }

    /**
     * Append a new line
     *
     * @return this
     */
    public ColoredChat newLine() {
        builder.append("\n");
        return this;
    }

    /**
     * Append a parameter
     *
     * @param parameter the parameter
     * @return this
     */
    public ColoredChat parameterPrefix(String parameter) {
        builder.append(Arc.getInstance().getArcConfiguration().getPrefix()).append(" ").append(parameterColor).append(parameter);
        return this;
    }

    /**
     * Append a message with the arc prefix.
     *
     * @param message the message
     * @return this
     */
    public ColoredChat messagePrefix(String message) {
        builder.append(Arc.getInstance().getArcConfiguration().getPrefix()).append(" ").append(mainColor).append(message);
        return this;
    }

    /**
     * Append a parameter if the provided {@code condition} is {@code true}
     *
     * @param condition the condition
     * @param parameter the parameter
     * @return this
     */
    public ColoredChat parameterIf(boolean condition, String parameter) {
        if (condition) builder.append(parameterColor).append(parameter);
        return this;
    }

    /**
     * Send the message.
     */
    public void send() {
        sender.sendMessage(builder.toString());
    }

}
