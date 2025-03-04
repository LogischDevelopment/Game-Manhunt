package tv.logisch.manhunt.listener;

import org.bukkit.entity.EnderDragon;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import tv.logisch.manhunt.manager.GameManager;

public class EntityDeathEvent implements Listener {

    @EventHandler
    public void onEntityDeath(org.bukkit.event.entity.EntityDeathEvent e) {

        if(e.getEntity() instanceof EnderDragon) {
            GameManager.endGame(true);
        }

    }

}
