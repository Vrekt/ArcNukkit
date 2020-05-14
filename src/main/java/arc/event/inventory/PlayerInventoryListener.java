package arc.event.inventory;

import arc.check.inventory.FastUse;
import arc.data.inventory.InventoryData;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
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
    public void onConsumeItem(PlayerItemConsumeEvent event) {
        final var player = event.getPlayer();
        if (fastUse.canCheck(player)) {
            final var data = InventoryData.getData(player);
            final var result = fastUse.check(player, data);

            if (result.failed()) {
                event.setCancelled(result.cancel());
            } else {
                data.isConsuming(false);
                data.eatingPackets(0);
            }
        }
    }
}
