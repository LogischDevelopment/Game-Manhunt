package tv.logisch.manhunt.listener;

import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import tv.logisch.manhunt.enums.GameState;
import tv.logisch.manhunt.manager.GameManager;

public class PlayerMoveListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if(e.getFrom().getX() == e.getTo().getX() && e.getFrom().getZ() == e.getTo().getZ()) return;

        if(GameManager.state().equals(GameState.PAUSED)) {
            e.setCancelled(true);
            e.getPlayer().sendActionBar(Component.text("§7§lEvent §c§lpausiert§r§8: §7Du kannst dich §c§nnicht§r§7 bewegen!"));
        }

    }

}
