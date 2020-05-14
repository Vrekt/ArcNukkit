package arc.check.inventory;

import arc.check.Check;
import arc.check.CheckType;
import arc.check.result.CancelAction;
import arc.check.result.CheckResult;
import arc.data.inventory.InventoryData;
import cn.nukkit.Player;

/**
 * Checks if a player is using or consuming an item too fast.
 * TODO: Doesn't always cancel, may just be a problem with Nukkit.
 * TODO: Maybe use tick based time calculation in the future.
 * TODO: Configuration
 * TODO: Consume time is 1200-1201
 */
public final class FastUse extends Check {

    /**
     * How long it takes to consume.
     */
    private final int consumeTime;

    /**
     * Exempt creative and spectator
     */
    public FastUse() {
        super(CheckType.FAST_USE);
        setExemptGamemodes(1, 3);

        consumeTime = section.getInt("consume-time");
    }

    /**
     * Check for fast-use.
     *
     * @param player the player
     * @param data   the data
     * @return the result
     */
    public CheckResult check(Player player, InventoryData data) {
        final var deltaTime = System.currentTimeMillis() - data.startConsumeTime();
        if (deltaTime < consumeTime)
            return violation(player, "delta time " + deltaTime + " less than " + consumeTime, CancelAction.CANCEL);
        return CheckResult.PASSED;
    }

}
