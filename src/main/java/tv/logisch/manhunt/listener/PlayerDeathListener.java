package tv.logisch.manhunt.listener;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.*;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scoreboard.Team;
import tv.logisch.manhunt.Manhunt;
import tv.logisch.manhunt.enums.GameState;
import tv.logisch.manhunt.manager.GameManager;

import java.util.Objects;
import java.util.UUID;

public class PlayerDeathListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {

        String msg = getDeathMessage(e);
        e.deathMessage(Component.empty());
        e.setKeepInventory(GameManager.keepInventory());
        if(GameManager.isRunner(e.getPlayer().getUniqueId())) {

            e.getPlayer().setGameMode(GameMode.SPECTATOR);
            e.getPlayer().sendMessage(Component.text(Manhunt.prefix() + "§cDu bist ausgeschieden!"));
            GameManager.latestPositions.removeIf(latestPosition -> latestPosition.getPlayer().getUniqueId().equals(e.getPlayer().getUniqueId()));
            Bukkit.getOnlinePlayers().forEach(p -> {
                p.sendMessage(Component.text("§c§lDEATH §8» §7" + msg));
            });

            for(UUID playerUuid : GameManager.getRunners()) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(playerUuid);
                if(player.getPlayer() != null && Bukkit.getOnlinePlayers().contains(player.getPlayer()) && player.getPlayer().getGameMode().equals(GameMode.SURVIVAL)) {
                    return;
                }
            }
            GameManager.endGame(false);

        } else {
            if(!GameManager.released()) {
                e.getPlayer().teleport(GameManager.waitingWorld().getSpawnLocation());
                return;
            }
            Location respawn = e.getPlayer().getRespawnLocation();
            if(respawn == null || respawn.getWorld().equals(GameManager.waitingWorld())) {
                respawn = GameManager.gameWorld().getSpawnLocation();
            }
            e.getPlayer().teleport(respawn);
            Bukkit.getOnlinePlayers().forEach(p -> {
                p.sendMessage(Component.text("§c§lDEATH §8» §7"+ msg));
            });

        }

    }

    private static String getDeathMessage(PlayerDeathEvent e) {
        Component dMessage = e.deathMessage();
        if (dMessage == null) {
            return "§f" + e.getPlayer().getName() + "§7 ist aus unbekannten Grund gestorben.";
        }
        String message = PlainTextComponentSerializer.plainText().serialize(dMessage);

        Team team = e.getEntity().getScoreboard().getEntryTeam(e.getEntity().getName());
        TextColor teamColor = team != null ? team.color() : TextColor.fromHexString("#AAAAAA");
        String customName = "§"+teamColor.asHexString().toUpperCase().charAt(0) + e.getPlayer().getName() + "§7";
        String displayName = e.getEntity().getScoreboard().getEntryTeam(e.getEntity().getName()) != null
                ? PlainTextComponentSerializer.plainText().serialize(Objects.requireNonNull(e.getEntity().getScoreboard().getEntryTeam(e.getEntity().getName())).prefix())  + e.getEntity().getName()
                : e.getEntity().getName();
        message = message.replaceAll(displayName, customName);

        Entity killer = e.getDamageSource().getCausingEntity();
        if(killer != null) {
            String killerName = killer.getName();
            if(killer instanceof Player k) {
                Team killerTeam = k.getScoreboard().getEntryTeam(k.getName());
                TextColor killerTeamColor = killerTeam != null ? killerTeam.color() : TextColor.fromHexString("#AAAAAA");
                String killerCustomName = "§"+killerTeamColor.asHexString().toUpperCase().charAt(0) + killerName + "§7";
                message = message.replaceAll(killerName, killerCustomName);
            } else {
                message = message.replaceAll(killerName, "§f" + killerName + "§7");
            }
        }

        return message;

    }

}
