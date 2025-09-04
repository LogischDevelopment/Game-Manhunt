package tv.logisch.manhunt.listener;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import tv.logisch.manhunt.Manhunt;
import tv.logisch.manhunt.enums.GameState;
import tv.logisch.manhunt.manager.GameManager;

public class VehicleMoveListener implements Listener {

    @EventHandler
    public void onVehicleMove(VehicleMoveEvent e) {
        if(e.getFrom().getX() == e.getTo().getX() && e.getFrom().getZ() == e.getTo().getZ()) return;

        if(GameManager.state().equals(GameState.PAUSED)) {
            e.getVehicle().teleport(e.getFrom());
            e.getVehicle().getPassengers().forEach(p -> {
                if (!(p instanceof Player target)) return;
                target.sendActionBar(Component.text(Manhunt.prefix() + "§7§lEvent §c§lpausiert§r§8: §7Du kannst dich §c§nnicht§r§7 bewegen!"));
            });
        }

    }

}
