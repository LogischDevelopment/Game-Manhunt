package tv.logisch.manhunt.listener;

import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import tv.logisch.manhunt.Manhunt;
import tv.logisch.manhunt.enums.GameState;
import tv.logisch.manhunt.manager.GameManager;

public class PlayerMoveListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if(e.getFrom().getX() == e.getTo().getX() && e.getFrom().getZ() == e.getTo().getZ()) return;

        if(GameManager.state().equals(GameState.PAUSED)) {
            e.setCancelled(true);
            e.getPlayer().sendActionBar(Component.text(Manhunt.prefix() + "§7§lEvent §c§lpausiert§r§8: §7Du kannst dich §c§nnicht§r§7 bewegen!"));
        }

        if(GameManager.state().equals(GameState.WAITING) && e.getTo().getY() < 50) {
            e.setTo(e.getPlayer().getWorld().getSpawnLocation());
            return;
        }
        if(!GameManager.released() && !GameManager.isRunner(e.getPlayer().getUniqueId()) && e.getTo().getY() < 50) {
            e.setTo(e.getPlayer().getWorld().getSpawnLocation());
        }

    }

}
