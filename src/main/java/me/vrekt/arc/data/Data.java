package me.vrekt.arc.data;

import cn.nukkit.Player;
import me.vrekt.arc.data.block.BlockData;
import me.vrekt.arc.data.combat.CombatData;
import me.vrekt.arc.data.moving.MovingData;
import me.vrekt.arc.data.packet.PacketData;
import me.vrekt.arc.data.player.PlayerData;

/**
 * A basic data interface
 */
public interface Data {

    /**
     * Unregister all data for the provided {@code player}
     *
     * @param player the player
     */
    static void removeAll(Player player) {
        PlayerData.remove(player);
        MovingData.remove(player);
        BlockData.remove(player);
        PacketData.remove(player);
        CombatData.remove(player);
    }

}