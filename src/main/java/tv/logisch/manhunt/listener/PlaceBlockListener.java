package tv.logisch.manhunt.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import tv.logisch.manhunt.enums.GameState;
import tv.logisch.manhunt.manager.GameManager;

public class PlaceBlockListener implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (!GameManager.state().equals(GameState.RUNNING)) {
            e.setCancelled(true);
            return;
        }
    }

}
