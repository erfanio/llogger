package io.erfan.llogger;

import java.util.Locale;

public class Utils {
    public static String formatDuration(Long duration) {
        if (duration < 60) {
            return String.format(Locale.ENGLISH, "%dsec", duration);
        } else if (duration < 3600) {
            return String.format(Locale.ENGLISH, "%dmin %dsec", duration / 60, duration % 60);
        } else if (duration % 3600 < 60) {
            return String.format(Locale.ENGLISH, "%dhr", duration / 3600);
        } else {
            return String.format(Locale.ENGLISH, "%dhr %dmin", duration / 3600, (duration % 3600) / 60);
        }
    }

    public static String formatDistance(Long distance) {
        if (distance < 1000) {
            return String.format(Locale.ENGLISH, "%dm", distance);
        } else {
            return String.format(Locale.ENGLISH, "%.1fkm", ((float) distance)/1000);
        }
    }
}
