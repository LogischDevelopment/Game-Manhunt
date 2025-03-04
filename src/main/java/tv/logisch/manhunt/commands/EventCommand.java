package tv.logisch.manhunt.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import tv.logisch.manhunt.enums.GameState;
import tv.logisch.manhunt.manager.GameManager;

public class EventCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {

        if(!commandSender.hasPermission("logisch.event.setup")) {
            commandSender.sendMessage("§8[§bManhunt§8] §cDazu hast du keine Rechte!");
            return false;
        }

        Player p = (Player) commandSender;

        if(strings.length == 0) {
            p.sendMessage("""
                    §8[§bManhunt§8] §cUsage:
                    §8[§bManhunt§8] §7/event start
                    §8[§bManhunt§8] §7/event releaseTime <seconds>
                    §8[§bManhunt§8] §7/event runner <add/remove> <player>
                    §8[§bManhunt§8] §7/event pause
                    §8[§bManhunt§8] §7/event resume""");
            return true;
        }

        String subCommand = strings[0];

        if(subCommand.equalsIgnoreCase("start")) {
            if(!GameManager.state().equals(GameState.WAITING)) {
                p.sendMessage(Component.text("§8[§bManhunt§8] §cDas Event läuft bereits! §8(§c/stop§8)"));
                return true;
            }
            GameManager.startGame();
            commandSender.sendMessage(Component.text("§8[§bManhunt§8] §7Das Event wurde gestartet!"));

        } else if(subCommand.equalsIgnoreCase("releaseTime")) {

            if(!Bukkit.hasWhitelist()) {
                p.sendMessage(Component.text("§8[§bManhunt§8] §cDie Hunter wurden bereits freigelassen!"));
                return true;
            }

            if(strings.length != 2) {
                p.sendMessage(Component.text("§8[§bManhunt§8] §7Die Release-Zeit ist aktuell auf §8(§7" + GameManager.totalReleaseTime() + "§8) §7Sekunden gesetzt!"));
                return true;
            }
            long seconds = Long.parseLong(strings[1]);
            GameManager.totalReleaseTime(seconds);
            GameManager.releaseTime(seconds);
            commandSender.sendMessage(Component.text("§8[§bManhunt§8] §7Die Release-Zeit wurde auf §8(§7" + seconds + "§8) §7Sekunden gesetzt!"));

        } else if(subCommand.equalsIgnoreCase("runner")) {

            if (strings.length != 3) {
                p.sendMessage("§8[§bManhunt§8] §cUsage: /event runner <add/remove> <player>");
                return true;
            }
            String action = strings[1];
            String playerName = strings[2];
            if(action.equalsIgnoreCase("add")) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);
                target.setWhitelisted(true);
                p.sendMessage("§8[§bManhunt§8] §7" + target.getName() + " §8(§7Runner§8) §7wurde hinzugefügt!");
            } else if(action.equalsIgnoreCase("remove")) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);
                target.setWhitelisted(false);
                p.sendMessage("§8[§bManhunt§8] §7" + target.getName() + " §8(§7Runner§8) §7wurde entfernt!");
            } else {
                p.sendMessage("§8[§bManhunt§8] §cUsage: /event runner <add/remove> <player>");
            }

        } else if(subCommand.equalsIgnoreCase("pause")) {
            if(GameManager.state().equals(GameState.WAITING)) {
                p.sendMessage("§8[§bManhunt§8] §cDas Event läuft nicht!");
                return true;
            }
            GameManager.pauseGame();
            commandSender.sendMessage(Component.text("§8[§bManhunt§8] §7Das Event wurde pausiert!"));
        } else if(subCommand.equalsIgnoreCase("resume")) {
            if(!GameManager.state().equals(GameState.PAUSED)) {
                p.sendMessage("§8[§bManhunt§8] §cDas Event ist nicht pausiert!");
                return true;
            }
            GameManager.resumeGame();
            commandSender.sendMessage(Component.text("§8[§bManhunt§8] §7Das Event wurde fortgesetzt!"));
        } else {
            p.sendMessage("""
                    §8[§bManhunt§8] §cUsage:
                    §8[§bManhunt§8] §7/event start
                    §8[§bManhunt§8] §7/event releaseTime <seconds>
                    §8[§bManhunt§8] §7/event runner <add/remove> <player>
                    §8[§bManhunt§8] §7/event pause
                    §8[§bManhunt§8] §7/event resume""");
        }

        return true;

    }
}
