package asia.virtualmc.vLibrary.utilities;

import java.time.Duration;

public class DigitUtils {

    /**
     * Rounds a given double value to a specified number of decimal places.
     *
     * @param value    The double value to be rounded.
     * @param decimals The number of decimal places to round to.
     * @return The rounded double value.
     */
    public static double getPreciseValue(double value, int decimals) {
        String format = "%." + decimals + "f";
        return Double.parseDouble(String.format(format, value));
    }

    /**
     * Formats a given double value to two decimal places with commas as thousand separators.
     *
     * @param value The double value to format.
     * @return A string representation of the value with two decimal places.
     */
    public static String getTwoDecimals(double value) {
        return String.format("%,.2f", value);
    }

    /**
     * Formats a given double value as an integer string with commas as thousand separators.
     *
     * @param value The double value to format.
     * @return A string representation of the value without decimal places.
     */
    public static String getNoDecimals(double value) {
        return String.format("%,d", (int) value);
    }

    /**
     * Converts a given time in seconds to a human-readable format (e.g., "2d 3h 4m 5s").
     *
     * @param seconds The time duration in seconds.
     * @return A formatted string representing the duration in days, hours, minutes, and seconds.
     */
    public static String getReadableTime(long seconds) {
        Duration duration = Duration.ofSeconds(seconds);

        long days = duration.toDays();
        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;
        long secs = duration.getSeconds() % 60;

        StringBuilder formatted = new StringBuilder();
        if (days > 0) formatted.append(days).append("d ");
        if (hours > 0) formatted.append(hours).append("h ");
        if (minutes > 0) formatted.append(minutes).append("m ");
        if (secs > 0 || formatted.isEmpty()) formatted.append(secs).append("s");

        return formatted.toString().trim();
    }
}