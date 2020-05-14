package arc.check.exemption;

import arc.check.Check;
import arc.check.permission.Permissions;
import cn.nukkit.Player;

/**
 * Handles player exemptions
 */
public final class Exemptions {

    /**
     * Check if a player is exempt.
     *
     * @param player the player
     * @return {@code true} if the player is exempt.
     */
    public static boolean isPlayerExempt(Player player, Check check) {
        return player.hasPermission(Permissions.PERMISSION_EXEMPT) || check.isExemptBecauseOfGamemode(player);
    }

}
