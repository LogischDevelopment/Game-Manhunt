package tv.logisch.manhunt.commands.completions;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

public class EventCommandCompletion implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {

        if(strings.length == 1) {
            return Stream.of("start", "pause", "resume", "releaseTime", "runner").filter(w -> w.toLowerCase().startsWith(strings[0].toLowerCase())).toList();
        }
        if(strings.length == 2) {
            if(strings[0].equalsIgnoreCase("runner")) {
                return Stream.of("add", "remove").filter(w -> w.toLowerCase().startsWith(strings[1].toLowerCase())).toList();
            }
            if(strings[0].equalsIgnoreCase("releaseTime")) {
                return Stream.of("60", "120", "180", "240", "300", "360", "420", "480", "540", "600").filter(w -> w.toLowerCase().startsWith(strings[1].toLowerCase())).toList();
            }
            return List.of();
        }
        if(strings.length == 3) {
            if(strings[0].equalsIgnoreCase("runner")) {
                return Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(w -> w.toLowerCase().startsWith(strings[2].toLowerCase())).toList();
            }
        }
        return List.of();

    }
}
