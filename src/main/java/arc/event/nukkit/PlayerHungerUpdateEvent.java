package arc.event.nukkit;

import cn.nukkit.Player;
import cn.nukkit.entity.Attribute;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.player.PlayerEvent;

/**
 * A custom nukkit event for when a players hunger is updated from the server.
 */
public final class PlayerHungerUpdateEvent extends PlayerEvent implements Cancellable {

    /**
     * Nukkit internal
     */
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    /**
     * The attribute for this event.
     */
    private final Attribute attribute;

    /**
     * 0.0
     * 20.0
     * 20.0
     * the hunger value that was updated to.
     */
    private final float minimumHunger, maximumHunger, defaultHunger, hunger;

    public PlayerHungerUpdateEvent(Player player, Attribute attribute) {
        this.player = player;
        this.attribute = attribute;

        this.minimumHunger = attribute.getMinValue();
        this.maximumHunger = attribute.getMaxValue();
        this.defaultHunger = attribute.getDefaultValue();
        this.hunger = attribute.getValue();
    }

    /**
     * @return the hunger attribute
     */
    public Attribute attribute() {
        return attribute;
    }

    /**
     * @return 0.0
     */
    public float minimumHunger() {
        return minimumHunger;
    }

    /**
     * @return 20.0
     */
    public float maximumHunger() {
        return maximumHunger;
    }

    /**
     * @return 20.0
     */
    public float defaultHunger() {
        return defaultHunger;
    }

    /**
     * @return the new hunger value
     */
    public float hunger() {
        return hunger;
    }
}
