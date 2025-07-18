package tv.logisch.manhunt.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import tv.logisch.manhunt.Manhunt;
import tv.logisch.manhunt.enums.GameState;
import tv.logisch.manhunt.guis.SettingGUI;
import tv.logisch.manhunt.manager.GameManager;

public class EventCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {

        if(!commandSender.hasPermission("logisch.manhunt.admin") && !GameManager.isHost(commandSender.getName())) {
            commandSender.sendMessage(Manhunt.prefix() + "§cDazu hast du keine Rechte!");
            return false;
        }

        if(strings.length == 0) {
            commandSender.sendMessage("""
                    {prefix} §cUsage:
                    {prefix} §7/event start
                    {prefix} §7/event releaseTime <seconds>
                    {prefix} §7/event runner <add/remove> <player>
                    {prefix} §7/event pause
                    {prefix} §7/event resume
                    {prefix} §7/event gui""".replaceAll("\\{prefix}", Manhunt.prefix()));
            return true;
        }

        String subCommand = strings[0];

        if(subCommand.equalsIgnoreCase("start")) {
            if(!GameManager.state().equals(GameState.WAITING)) {
                commandSender.sendMessage(Component.text(Manhunt.prefix() + "§cDas Event läuft bereits! §8(§c/stop§8)"));
                return true;
            }
            GameManager.startGame();
            commandSender.sendMessage(Component.text(Manhunt.prefix() + "§7Das Event wurde gestartet!"));

        } else if(subCommand.equalsIgnoreCase("releaseTime")) {

            if(GameManager.released()) {
                commandSender.sendMessage(Component.text(Manhunt.prefix() + "§cDie Hunter wurden bereits freigelassen!"));
                return true;
            }

            if(strings.length != 2) {
                commandSender.sendMessage(Component.text(Manhunt.prefix() + "§7Die Release-Zeit ist aktuell auf §8(§7" + GameManager.totalReleaseTime() + "§8) §7Sekunden gesetzt!"));
                return true;
            }
            long seconds = Long.parseLong(strings[1]);
            GameManager.totalReleaseTime(seconds);
            GameManager.releaseTime(seconds);
            commandSender.sendMessage(Component.text(Manhunt.prefix() + "§7Die Release-Zeit wurde auf §8(§7" + seconds + "§8) §7Sekunden gesetzt!"));

        } else if(subCommand.equalsIgnoreCase("runner")) {

            if (strings.length != 3) {
                commandSender.sendMessage(Manhunt.prefix() + "§cUsage: /event runner <add/remove> <player>");
                return true;
            }
            String action = strings[1];
            String playerName = strings[2];
            if(action.equalsIgnoreCase("add")) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);
                if(GameManager.isRunner(target.getUniqueId())) {
                    commandSender.sendMessage(Manhunt.prefix() + "§c" + target.getName() + " §8(§7Runner§8) §cist bereits ein Runner!");
                    return true;
                }
                GameManager.addRunner(target.getUniqueId());
                commandSender.sendMessage(Manhunt.prefix() + "§7" + target.getName() + " §8(§7Runner§8) §7wurde hinzugefügt!");
            } else if(action.equalsIgnoreCase("remove")) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);
                if(!GameManager.isRunner(target.getUniqueId())) {
                    commandSender.sendMessage(Manhunt.prefix() + "§c" + target.getName() + " §8(§7Hunter§8) §cist kein Runner!");
                    return true;
                }
                GameManager.removeRunner(target.getUniqueId());
                commandSender.sendMessage(Manhunt.prefix() + "§7" + target.getName() + " §8(§7Hunter§8) §7wurde entfernt!");
            } else {
                commandSender.sendMessage(Manhunt.prefix() + "§cUsage: /event runner <add/remove> <player>");
            }

        } else if(subCommand.equalsIgnoreCase("pause")) {
            if(GameManager.state().equals(GameState.WAITING)) {
                commandSender.sendMessage(Manhunt.prefix() + "§cDas Event läuft nicht!");
                return true;
            }
            GameManager.pauseGame();
            commandSender.sendMessage(Component.text(Manhunt.prefix() + "§7Das Event wurde pausiert!"));
        } else if(subCommand.equalsIgnoreCase("resume")) {
            if(!GameManager.state().equals(GameState.PAUSED)) {
                commandSender.sendMessage(Manhunt.prefix() + "§cDas Event ist nicht pausiert!");
                return true;
            }
            GameManager.resumeGame();
            commandSender.sendMessage(Component.text(Manhunt.prefix() + "§7Das Event wurde fortgesetzt!"));
        } else if(subCommand.equalsIgnoreCase("gui")) {
            if(!(commandSender instanceof Player player)) {
                commandSender.sendMessage(Manhunt.prefix() + "§cDieser Befehl kann nur von einem Spieler ausgeführt werden!");
                return true;
            }
            SettingGUI.get(player).open();
            commandSender.sendMessage(Component.text(Manhunt.prefix() + "§7Die Einstellungen wurden geöffnet!"));
        } else {
            commandSender.sendMessage("""
                    {prefix} §cUsage:
                    {prefix} §7/event start
                    {prefix} §7/event releaseTime <seconds>
                    {prefix} §7/event runner <add/remove> <player>
                    {prefix} §7/event pause
                    {prefix} §7/event resume
                    {prefix} §7/event gui""".replaceAll("\\{prefix}", Manhunt.prefix()));
        }

        return true;

    }
}
