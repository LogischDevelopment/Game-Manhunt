package tv.logisch.manhunt.utils;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.scheduler.BukkitRunnable;
import tv.logisch.manhunt.Manhunt;
import tv.logisch.manhunt.manager.GameManager;

public class AnimationUtils {

    private static final String[] COLOR_GRADIENT = {
            "#41CFFF", "#3CC8F1", "#37C2E2", "#32BCD4", "#2DB6C6",
            "#29AFB7", "#24A9A9", "#1FA39B", "#1A8C8C", "#15807E"
    };

    private static double animationProgress = 0.0;
    private static BukkitRunnable animationTask;

    @Getter
    @Setter
    private static boolean isRunning;

    /**
     * This class cannot be instantiated.
     */
    public AnimationUtils() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    /**
     * Starts the animation.
     */
    public static void startAnimation() {
        isRunning = true;
        animationTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (isRunning) {
                    sendBossbar();
                    animationProgress = (animationProgress + 0.03) % 1.0;
                } else {
                    this.cancel();
                }
            }
        };
        animationTask.runTaskTimer(Manhunt.instance(), 0, 1);
    }

    /**
     * Stops the animation.
     */
    public static void stopAnimation() {
        isRunning = false;
        animationTask.cancel();
    }

    /**
     * Sends the bossbar with the animated item name.
     */
    public static void sendBossbar() {

        long releaseTime = GameManager.releaseTime();
        if(releaseTime > 0) {
            String animatedName = animateGradient(Format.time(GameManager.releaseTime()));
            double progress = (double) GameManager.releaseTime() / GameManager.totalReleaseTime();
            GameManager.bossBar.setTitle(animatedName);
            GameManager.bossBar.setProgress(progress);
        } else {
            long gameTime = GameManager.time();
            String animatedName = animateGradient(Format.time(gameTime));
            double progress = 1;
            GameManager.bossBar.setTitle(animatedName);
            GameManager.bossBar.setProgress(progress);
        }

    }

    /**
     * Animates the gradient of the given text.
     * @param text the text to animate
     * @return the animated text
     */
    private static String animateGradient(String text) {
        StringBuilder result = new StringBuilder();
        int gradientLength = COLOR_GRADIENT.length;
        int extendedLength = Math.max(text.length(), gradientLength);

        for (int i = 0; i < text.length(); i++) {
            double position = (animationProgress + (double) i / extendedLength) % 1.0;
            int index = (int) (position * gradientLength);
            int nextIndex = (index + 1) % gradientLength;

            double fraction = (position * gradientLength) % 1.0;
            String color = interpolateColor(COLOR_GRADIENT[index], COLOR_GRADIENT[nextIndex], fraction);
            result.append(ChatColor.of(color)).append(text.charAt(i));
        }
        return result.toString();
    }

    /**
     * Interpolates the color between two colors.
     * @param color1 the first color
     * @param color2 the second color
     * @param fraction the fraction
     * @return the interpolated color
     */
    private static String interpolateColor(String color1, String color2, double fraction) {
        int r1 = Integer.parseInt(color1.substring(1, 3), 16);
        int g1 = Integer.parseInt(color1.substring(3, 5), 16);
        int b1 = Integer.parseInt(color1.substring(5, 7), 16);

        int r2 = Integer.parseInt(color2.substring(1, 3), 16);
        int g2 = Integer.parseInt(color2.substring(3, 5), 16);
        int b2 = Integer.parseInt(color2.substring(5, 7), 16);

        int r = (int) (r1 + fraction * (r2 - r1));
        int g = (int) (g1 + fraction * (g2 - g1));
        int b = (int) (b1 + fraction * (b2 - b1));

        return String.format("#%02X%02X%02X", r, g, b);
    }

}
