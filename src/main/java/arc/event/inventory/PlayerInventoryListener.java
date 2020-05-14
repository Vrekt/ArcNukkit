package arc.event.inventory;

import arc.check.inventory.FastUse;
import arc.data.inventory.InventoryData;
import arc.event.nukkit.PlayerHungerUpdateEvent;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerItemConsumeEvent;

/**
 * Listens for inventory events/checks.
 */
public final class PlayerInventoryListener implements Listener {

    /**
     * The fast-use check.
     */
    private final FastUse fastUse = new FastUse();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {

    }

    /**
     * Invoked when the hunger was updated.
     * TODO :Exempt
     *
     * @param event the event.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onHungerUpdate(PlayerHungerUpdateEvent event) {
        final var player = event.getPlayer();
        final var data = InventoryData.getData(player);
        if (data.isConsuming()) {
            // check for fast-use.
            final var check = fastUse.check(player, data);
            if (check.failed()) {
                if (check.cancel()) event.setCancelled(true);
            } else {
                data.isConsuming(false);
                data.eatingPackets(0);
            }

            // TODO: Currently it doesn't always cancel in time and the action is allowed.
            // TODO: I believe the item also gets used when failing.
            // TODO: Look into this later on
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onConsumeItem(PlayerItemConsumeEvent event) {

    }


}
