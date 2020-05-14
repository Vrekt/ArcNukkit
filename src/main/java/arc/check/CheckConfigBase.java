package arc.check;

import arc.Arc;
import cn.nukkit.utils.ConfigSection;

/**
 * Provides a base for checks configuration wise.
 */
public abstract class CheckConfigBase {

    /**
     * The four basic config properties for each check.
     */
    private final boolean enabled, ban, cancel, kick;

    /**
     * When to ban.
     * When to kick
     */
    private final int banLevel, kickLevel;

    /**
     * The kick message
     */
    private final String kickMessage;

    /**
     * The configuration section for this check.
     */
    protected final ConfigSection section;

    public CheckConfigBase(CheckType check) {
        final var configurationName = check.configurationName();
        section = Arc.configuration().getSection(configurationName);

        enabled = section.getBoolean("enabled");
        ban = section.getBoolean("ban");
        cancel = section.getBoolean("cancel");
        kick = section.getBoolean("kick");
        banLevel = section.getInt("ban-level");
        kickLevel = section.getInt("kick-level");
        kickMessage = section.getString("kick-message");
    }

    /**
     * @return if this check is enabled
     */
    public boolean enabled() {
        return enabled;
    }

    /**
     * @return if this check should ban
     */
    public boolean ban() {
        return ban;
    }

    /**
     * @return if this check should cancel
     */
    public boolean cancel() {
        return cancel;
    }

    /**
     * @return if this check should kick the player
     */
    public boolean kick() {
        return kick;
    }

    /**
     * @return when to ban
     */
    public int banLevel() {
        return banLevel;
    }

    /**
     * @return when to kick
     */
    public int kickLevel() {
        return kickLevel;
    }

    /**
     * @return the kick message.
     */
    public String kickMessage() {
        return kickMessage;
    }
}
