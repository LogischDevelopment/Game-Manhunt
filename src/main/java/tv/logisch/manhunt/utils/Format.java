package tv.logisch.manhunt.utils;

public class Format {

    public static String time(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long sec = seconds % 60;

        if (hours > 0) {
            return String.format("%02dh %02dm %02ds", hours, minutes, sec);
        } else if (minutes > 0) {
            return String.format("%02dm %02ds", minutes, sec);
        } else {
            return String.format("%02ds", sec);
        }
    }

}
