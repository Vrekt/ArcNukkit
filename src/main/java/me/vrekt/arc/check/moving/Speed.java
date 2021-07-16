package me.vrekt.arc.check.moving;

import cn.nukkit.Player;
import me.vrekt.arc.check.Check;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.data.moving.MovingData;

/**
 * Checks if the player is moving too fast.
 */
public final class Speed extends Check {

    public Speed() {
        super(CheckType.SPEED);

        enabled(true)
                .cancel(true)
                .cancelLevel(0)
                .notify(true)
                .notifyEvery(1)
                .ban(false)
                .banLevel(20)
                .kick(false)
                .build();

        if (enabled()) load();
    }

    /**
     * Check
     *
     * @param player the player
     * @param data   their data
     */
    public void check(Player player, MovingData data) {
        if (exempt(player)) return;
    }

    @Override
    public void reloadConfig() {
        load();
    }

    @Override
    public void load() {

    }
}
