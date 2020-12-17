package rs.ac.bg.etf.running.workouts;

import java.text.SimpleDateFormat;

public class DateTimeUtil {
    private static final SimpleDateFormat simpleDateFormat =
            new SimpleDateFormat("dd/MM/yyyy");

    public static SimpleDateFormat getSimpleDateFormat() {
        return simpleDateFormat;
    }

    public static String realMinutesToString(double realMinutes) {
        int minutes = (int) realMinutes;
        int seconds = (int) (60 * (realMinutes - minutes));

        return minutes + ":" + String.format("%02d", seconds);
    }
}
