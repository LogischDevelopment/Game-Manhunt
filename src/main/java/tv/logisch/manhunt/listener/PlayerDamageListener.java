package tv.logisch.manhunt.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import tv.logisch.manhunt.enums.GameState;
import tv.logisch.manhunt.manager.GameManager;

public class PlayerDamageListener implements Listener {

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e) {
        if(!(e.getEntity() instanceof Player target)) return;

        if(!GameManager.state().equals(GameState.RUNNING)) {
            e.setCancelled(true);
            return;
        }
        if(!GameManager.released() && !GameManager.isRunner(target.getUniqueId())) {
            e.setCancelled(true);
            return;
        }

        if(!(e.getDamageSource().getCausingEntity() instanceof Player damager)) return;

        if(GameManager.isRunner(damager.getUniqueId()) && GameManager.isRunner(target.getUniqueId())) {
            e.setCancelled(true);
            return;
        }
        if(!GameManager.isRunner(damager.getUniqueId()) && !GameManager.isRunner(target.getUniqueId())) {
            e.setCancelled(true);
            return;
        }
    }

}
