package me.vrekt.arc.listener.combat;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.math.Vector3;
import me.vrekt.arc.Arc;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.check.combat.KillAura;
import me.vrekt.arc.check.combat.Reach;
import me.vrekt.arc.data.combat.CombatData;

/**
 * Listens for combat related events
 */
public final class CombatListener implements Listener {

    /**
     * The reach check
     */
    private final Reach reach;

    /**
     * The killaura check
     */
    private final KillAura killAura;

    public CombatListener() {
        reach = Arc.getInstance().getCheckManager().getCheck(CheckType.REACH);
        killAura = Arc.getInstance().getCheckManager().getCheck(CheckType.KILL_AURA);
    }

    @EventHandler
    private void onEntityDamaged(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            final Player player = (Player) event.getDamager();
            final CombatData data = CombatData.get(player);
            data.setLastAttack(System.currentTimeMillis());

            // ignore everything and just out-right cancel.
            if (data.cancelNextAttack()) {
                event.setCancelled(true);
                return;
            }

            // reset fastclick
            data.setBatchingThreshold(0);

            final float base = event.getKnockBack();

            final double deltaX = player.x - event.getEntity().x;
            final double deltaZ = player.z - event.getEntity().y;
            Vector3 motion = new Vector3(player.getMotion().x, player.getMotion().y, player.getMotion().z);

            double f = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
            if (f <= 0) {
                f = 1;
            } else {
                f = 1 / f;
            }

            motion.x /= 2d;
            motion.y /= 2d;
            motion.z /= 2d;
            motion.x += deltaX * f * base;
            motion.y += base;
            motion.z += deltaZ * f * base;

            if (motion.y > base) {
                motion.y = base;
            }

            event.setCancelled(killAura.check(player, event.getEntity(), data));
            event.setCancelled(reach.check(player, event.getEntity(), motion));
        }
    }

}
