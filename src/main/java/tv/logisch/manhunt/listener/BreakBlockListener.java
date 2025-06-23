package tv.logisch.manhunt.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import tv.logisch.manhunt.enums.GameState;
import tv.logisch.manhunt.manager.GameManager;

public class BreakBlockListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {

        if(!GameManager.state().equals(GameState.RUNNING)) {
            e.setCancelled(true);
            return;
        }

    }

}
