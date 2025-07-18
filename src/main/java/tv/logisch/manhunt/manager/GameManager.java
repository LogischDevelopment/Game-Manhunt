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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import tv.logisch.manhunt.Manhunt;
import tv.logisch.manhunt.enums.GameState;
import tv.logisch.manhunt.objects.LatestPositionObject;
import tv.logisch.manhunt.utils.AnimationUtils;
import tv.logisch.manhunt.utils.Format;
import tv.logisch.manhunt.utils.Velocity;

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
    private static boolean keepInventory = false;
    @Getter @Setter @Accessors(fluent = true)
    private static boolean bedBomb = true;

    @Getter @Setter @Accessors(fluent = true)
    private static long time = 0;

    @Getter @Setter @Accessors(fluent = true)
    private static boolean released = false;

    @Getter @Setter @Accessors(fluent = true)
    private static World gameWorld;
    @Getter @Setter @Accessors(fluent = true)
    private static World waitingWorld;

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
        Bukkit.setWhitelist(true);
        state = GameState.STARTING;
        releaseTime = totalReleaseTime;
        bossBar = Bukkit.createBossBar(Format.time(releaseTime), BarColor.BLUE, BarStyle.SOLID);
        bossBar.setProgress(1);
        bossBar.setVisible(true);
        Bukkit.getOnlinePlayers().forEach(p -> {
            bossBar.addPlayer(p);
            if(GameManager.isRunner(p.getUniqueId())) {
                p.setGameMode(GameMode.SURVIVAL);
                p.teleport(GameManager.gameWorld().getSpawnLocation());
            }
        });
        AnimationUtils.startAnimation();

        runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if(GameManager.state().equals(GameState.RUNNING)) {
                    if(GameManager.releaseTime() == 1) {
                        Bukkit.setWhitelist(true);
                        released = true;
                        Bukkit.getOnlinePlayers().forEach(player -> {
                            player.sendMessage(Manhunt.prefix() + "§7Die Hunter wurden freigelassen!");
                            player.playSound(player, Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);

                            if(!GameManager.isRunner(player.getUniqueId())) {
                                player.setGameMode(GameMode.SURVIVAL);
                                player.teleport(GameManager.gameWorld().getSpawnLocation());
                                ItemStack compass = new ItemStack(Material.COMPASS);
                                ItemMeta meta = compass.getItemMeta();
                                meta.displayName(Component.text("§8» §bTracker"));
                                compass.setItemMeta(meta);
                                player.getInventory().addItem(compass);
                            }
                        });
                        GameManager.startCompassTracker();

                        Bukkit.setWhitelist(false);
                        Player host = GameManager.getHost();
                        Velocity.sendToVelocity(host, "executeCommand:joinme:"+host.getUniqueId());
                    }

                    GameManager.releaseTime(GameManager.releaseTime() - 1);
                    if(releaseTime < 1) GameManager.time(GameManager.time() + 1);
                }
            }
        };
        runnable.runTaskTimer(Manhunt.instance(), 20, 20);

        state = GameState.RUNNING;
        Bukkit.setWhitelist(false);
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
            player.sendMessage(Manhunt.prefix() + "§7Das Spiel ist vorbei!");
            player.sendMessage(Component.text(Manhunt.prefix() + "§7Die Zeit: §b" + Format.time(time)));
            player.playSound(player, Sound.ENTITY_ENDER_DRAGON_DEATH, 1, 1);

            if(runnerFinished) {
                player.sendMessage(Manhunt.prefix() + "§aDie Runner haben gewonnen!");
                player.sendTitlePart(TitlePart.TITLE, Component.text("§aDie Runner haben gewonnen!"));
                player.sendTitlePart(TitlePart.SUBTITLE, Component.text("§b" + Format.time(time)));
            } else {
                player.sendMessage(Manhunt.prefix() + "§cDie Hunter haben gewonnen!");
                player.sendTitlePart(TitlePart.TITLE, Component.text("§cDie Hunter haben gewonnen!"));
                player.sendTitlePart(TitlePart.SUBTITLE, Component.text("§b" + Format.time(time)));
            }
            player.setGameMode(GameMode.SPECTATOR);
            player.sendMessage(Component.text(Manhunt.prefix() + "§7Der Server stoppt in §b15 §7Sekunden!"));

        });
        try {
            GameManager.bossBar.removeAll();
        } catch (Exception ignored) { }
        AnimationUtils.stopAnimation();

        BukkitRunnable endRunnable = getBukkitRunnable();
        endRunnable.runTaskTimerAsynchronously(Manhunt.instance(), 0, 20);

    }

    private static @NotNull BukkitRunnable getBukkitRunnable() {
        final int[] seconds = {15};
        return new BukkitRunnable() {
            @Override
            public void run() {
                if(seconds[0] <= 0) {
                    System.out.println("Shutting down server now...");
                    Bukkit.getServer().shutdown();
                    return;
                }
                Bukkit.getOnlinePlayers().forEach(player -> {
                    if(seconds[0] == 5 || seconds[0] <= 3) {
                        player.sendMessage(Component.text(Manhunt.prefix() + "§7Der Server stoppt in §b" + seconds[0] + " §7Sekunden!"));
                        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                    }
                });
                seconds[0]--;
            }
        };
    }

    public static boolean isRunner(UUID player) {
        return getRunners().contains(player);
    }

    public static void addRunner(UUID player) {
        runners.add(player);
    }

    public static void removeRunner(UUID player) {
        runners.remove(player);
        latestPositions.removeIf(l -> l.getPlayer().getUniqueId().equals(player));
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
                            player.sendMessage(Manhunt.prefix() + "§7Es wurde kein Spieler zum tracken gefunden!");
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

    public static Player getHost() {
        return Bukkit.getPlayer(Manhunt.gameConfig().hostUUID());
    }

    public static boolean isHost(String playerName) {
        return playerName.equalsIgnoreCase(Manhunt.gameConfig().hostName());
    }

}
