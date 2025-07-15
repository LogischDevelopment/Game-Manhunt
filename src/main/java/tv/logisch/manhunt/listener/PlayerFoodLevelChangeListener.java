package tv.logisch.manhunt.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import tv.logisch.manhunt.enums.GameState;
import tv.logisch.manhunt.manager.GameManager;

public class PlayerFoodLevelChangeListener implements Listener {

    @EventHandler
    public void onPlayerFoodLevelChange(FoodLevelChangeEvent e) {
        if(!GameManager.state().equals(GameState.RUNNING)) e.setCancelled(true);
    }

}
