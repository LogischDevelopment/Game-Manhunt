package tv.logisch.manhunt.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Getter
@AllArgsConstructor
public class LatestPositionObject {

    private Player player;
    private Location l;

}
