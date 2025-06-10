package tv.logisch.manhunt.manager;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.scheduler.BukkitRunnable;
import tv.logisch.manhunt.Manhunt;
import tv.logisch.manhunt.enums.GameState;
import tv.logisch.manhunt.objects.LatestPositionObject;
import tv.logisch.manhunt.utils.AnimationUtils;
import tv.logisch.manhunt.utils.Format;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class GameManager {

    @Getter @Setter @Accessors(fluent = true)
    private static GameState state = GameState.WAITING;

    @Getter @Setter @Accessors(fluent = true)
    private static long totalReleaseTime = 300;
    @Getter @Setter @Accessors(fluent = true)
    private static long releaseTime = 300;

    @Getter @Setter @Accessors(fluent = true)
    private static long time = 0;

    public static BossBar bossBar;

    public static List<LatestPositionObject> latestPositions = new ArrayList<>();
    private static BukkitRunnable runnable;

    @Getter @Setter
    private static List<UUID> runners = new ArrayList<>();

    public static void setBossBar(String title, double progress) {
        bossBar.setTitle(title);
        bossBar.setProgress(progress);
    }

    public static void startGame() {
        if(state != GameState.WAITING) return;
        state = GameState.STARTING;
        releaseTime = totalReleaseTime;
        bossBar = Bukkit.createBossBar(Format.time(releaseTime), BarColor.BLUE, BarStyle.SOLID);
        bossBar.setProgress(1);
        bossBar.setVisible(true);
        Bukkit.getOnlinePlayers().forEach(bossBar::addPlayer);
        AnimationUtils.startAnimation();

        runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if(GameManager.state().equals(GameState.RUNNING)) {
                    if(GameManager.releaseTime() == 1) {
                        Bukkit.setWhitelist(false);
                        Bukkit.getOnlinePlayers().forEach(player -> {
                            player.sendMessage("§8[§bManhunt§8] §7Die Hunter wurden freigelassen!");
                            player.playSound(player, Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
                        });
                        GameManager.startCompassTracker();
                    }

                    GameManager.releaseTime(GameManager.releaseTime() - 1);
                    if(releaseTime < 1) GameManager.time(GameManager.time() + 1);
                }
            }
        };
        runnable.runTaskTimer(Manhunt.instance(), 20, 20);

        state = GameState.RUNNING;
        Bukkit.getServerTickManager().setFrozen(false);

    }

    public static void pauseGame() {
        state = GameState.PAUSED;
        Bukkit.getServer().getServerTickManager().setFrozen(true);
    }

    public static void resumeGame() {
        state = GameState.RUNNING;
        Bukkit.getServer().getServerTickManager().setFrozen(false);
    }

    public static void endGame(boolean runnerFinished) {
        state = GameState.ENDING;
        long time = GameManager.time();
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.sendMessage("§8[§bManhunt§8] §7Das Spiel ist vorbei!");
            player.sendMessage(Component.text("§8[§bManhunt§8] §7Die Zeit: §b" + Format.time(time)));
            player.playSound(player, Sound.ENTITY_ENDER_DRAGON_DEATH, 1, 1);

            if(runnerFinished) {
                player.sendMessage("§8[§bManhunt§8] §aDie Runner haben gewonnen!");
                player.sendTitlePart(TitlePart.TITLE, Component.text("§aDie Runner haben gewonnen!"));
                player.sendTitlePart(TitlePart.SUBTITLE, Component.text("§b" + Format.time(time)));
            } else {
                player.sendMessage("§8[§bManhunt§8] §cDie Hunter haben gewonnen!");
                player.sendTitlePart(TitlePart.TITLE, Component.text("§cDie Hunter haben gewonnen!"));
                player.sendTitlePart(TitlePart.SUBTITLE, Component.text("§b" + Format.time(time)));
            }
            player.setGameMode(GameMode.SPECTATOR);

        });
        Bukkit.getOnlinePlayers().forEach(GameManager.bossBar::removePlayer);
        GameManager.bossBar.removeAll();
        AnimationUtils.stopAnimation();
    }

    public static boolean isRunner(UUID player) {
        return getRunners().contains(player);
    }

    public static void addRunner(UUID player) {
        runners.add(player);
    }

    public static void removeRunner(UUID player) {
        runners.remove(player);
    }

    public static void startCompassTracker() {
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                Map<Player, Location> locations = new HashMap<>();
                for(UUID pUuid : GameManager.getRunners()) {
                    OfflinePlayer p = Bukkit.getOfflinePlayer(pUuid);
                    if(p.isOnline() && p.getPlayer() != null && p.getPlayer().getGameMode().equals(GameMode.SURVIVAL)) {
                        Location clone = p.getPlayer().getLocation().clone();
                        clone.setY(clone.getWorld().getMinHeight());
                        clone.getBlock().setType(Material.LODESTONE);
                        locations.put(p.getPlayer(), clone);
                        latestPositions.removeIf(l -> l.getPlayer().equals(p.getPlayer()) && l.getL().getWorld().equals(clone.getWorld()));
                        latestPositions.add(new LatestPositionObject(p.getPlayer(), clone));
                    }
                }
                for(Player player : Bukkit.getOnlinePlayers()) {
                    Location loc = player.getLocation();
                    AtomicReference<Location> closest = new AtomicReference<>(null);
                    AtomicReference<Player> closestPlayer = new AtomicReference<>(null);
                    locations.forEach((p, l) -> {
                        if(closest.get() == null && l.getWorld() == loc.getWorld()) {
                            closest.set(l);
                            closestPlayer.set(p);
                        } else if(loc.getWorld() == l.getWorld() && checkDistance(loc, l) < checkDistance(loc, closest.get())) {
                            closest.set(l);
                            closestPlayer.set(p);
                        }
                    });
                    if(closest.get() == null) {
                        latestPositions.forEach(l -> {
                            if (closest.get() == null && l.getL().getWorld() == loc.getWorld()) {
                                closest.set(l.getL());
                                closestPlayer.set(l.getPlayer());
                            } else if (loc.getWorld() == l.getL().getWorld() && checkDistance(loc, l.getL()) < checkDistance(loc, closest.get())) {
                                closest.set(l.getL());
                                closestPlayer.set(l.getPlayer());
                            }
                        });
                        if(closest.get() == null) {
                            player.sendMessage("§8[§bManhunt§8] §7Es wurde kein Spieler zum tracken gefunden!");
                            return;
                        }
                    }
                    Bukkit.getConsoleSender().sendMessage("Update compass for "+player.getName());
                    player.getInventory().forEach(itemStack -> {
                        if(itemStack != null && itemStack.getType().equals(Material.COMPASS)) {
                            CompassMeta meta = (CompassMeta) itemStack.getItemMeta();
                            if(closestPlayer.get() != null) {
                                meta.setLodestone(closest.get());
                                meta.setLodestoneTracked(true);
                                meta.displayName(Component.text("§8» §b" + closestPlayer.get().getName()));
                                itemStack.setItemMeta(meta);
                            }
                        }
                    });
                }
            }
        };
        runnable.runTaskTimer(Manhunt.instance(), 0, 600);
    }

    public static double checkDistance(Location l1, Location l2) {
        if(l1.getWorld() != l2.getWorld()) return Double.MAX_VALUE;
        double x1 = l1.getX();
        double z1 = l1.getZ();
        double x2 = l2.getX();
        double z2 = l2.getZ();
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(z1 - z2, 2));
    }

}
