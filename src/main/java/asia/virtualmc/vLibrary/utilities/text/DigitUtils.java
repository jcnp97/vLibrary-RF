package asia.virtualmc.vLibrary.utilities.text;

import java.time.Duration;

public class DigitUtils {

    /**
     * Returns the value rounded to the specified number of decimal places.
     *
     * @param value The original double value to round.
     * @param decimals The number of decimal places to keep.
     * @return The value rounded to the specified decimal places.
     */
    public static double precise(double value, int decimals) {
        String format = "%." + decimals + "f";
        return Double.parseDouble(String.format(format, value));
    }

    /**
     * Returns the value rounded to the specified number of decimal places.
     *
     * @param value The original float value to round.
     * @param decimals The number of decimal places to keep.
     * @return The value rounded to the specified decimal places.
     */
    public static float precise(float value, int decimals) {
        String format = "%." + decimals + "f";
        return Float.parseFloat(String.format(format, value));
    }

    /**
     * Formats the given value with no decimal places and comma separators.
     *
     * @param value The double value to format.
     * @return A string representing the integer part of the value with commas.
     */
    public static String toInt(double value) {
        return String.format("%,d", (int) value);
    }

    /**
     * Converts a duration in seconds into a human-readable format.
     *
     * @param seconds The total number of seconds to convert.
     * @return A string in the format of "Xd Xh Xm Xs", omitting units with zero values.
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

    /**
     * Formats the given double value with commas. If the value is a whole number,
     * it shows no decimal places; otherwise, it shows two decimals.
     *
     * @param value The double value to format.
     * @return A string representation of the value with appropriate formatting.
     */
    public static String format(double value) {
        if (value == (long) value) {
            return String.format("%,d", (long) value);
        } else {
            return String.format("%,.2f", value);
        }
    }

    /**
     * Formats the given double value into shortened units (K, M, B, T) with up to two decimal places.
     * For example, 1150000 becomes "1.15M".
     *
     * @param value The double value to format.
     * @return A string representation of the value with unit suffix.
     */
    public static String formatInUnits(double value) {
        final double absValue = Math.abs(value);
        final String suffix;
        double scaledValue;

        if (absValue >= 1_000_000_000_000.0) {
            scaledValue = value / 1_000_000_000_000.0;
            suffix = "T";
        } else if (absValue >= 1_000_000_000.0) {
            scaledValue = value / 1_000_000_000.0;
            suffix = "B";
        } else if (absValue >= 1_000_000.0) {
            scaledValue = value / 1_000_000.0;
            suffix = "M";
        } else if (absValue >= 1_000.0) {
            scaledValue = value / 1_000.0;
            suffix = "K";
        } else {
            return format(value); // fallback to original format for small values
        }

        if (scaledValue == (long) scaledValue) {
            return String.format("%d%s", (long) scaledValue, suffix);
        } else {
            return String.format("%.2f%s", scaledValue, suffix);
        }
    }

    /**
     * Rounds the given double value to the nearest integer.
     *
     * @param value The double value to round.
     * @return The rounded integer value.
     */
    public static int roundToInt(double value) {
        return (int) Math.round(value);
    }

    /**
     * Converts an integer to its corresponding Roman numeral representation.
     *
     * @param num the integer to convert
     * @return a string containing the Roman numeral equivalent of the input number
     */
    public static String toRoman(int num) {
        int[] values = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] symbols = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};

        StringBuilder roman = new StringBuilder();

        for (int i = 0; i < values.length; i++) {
            while (num >= values[i]) {
                roman.append(symbols[i]);
                num -= values[i];
            }
        }

        return roman.toString();
    }
}