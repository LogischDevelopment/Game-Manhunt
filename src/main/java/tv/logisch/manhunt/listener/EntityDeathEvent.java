package tv.logisch.manhunt.listener;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import tv.logisch.manhunt.Manhunt;
import tv.logisch.manhunt.manager.GameManager;

public class EntityDeathEvent implements Listener {

    @EventHandler
    public void onEntityDeath(org.bukkit.event.entity.EntityDeathEvent e) {

        if(e.getEntity() instanceof EnderDragon) {
            Entity causingEntity = e.getDamageSource().getCausingEntity();
            if(causingEntity != null) {
                if(causingEntity.getType().equals(EntityType.PLAYER)) {
                    if(!GameManager.isRunner(causingEntity.getUniqueId())) {
                        e.setCancelled(true);
                        causingEntity.sendMessage(Component.text(Manhunt.prefix() + "§cDu kannst den Enderdrachen nicht töten!"));
                        return;
                    }
                }
            }
            GameManager.endGame(true);
        }

    }

}
