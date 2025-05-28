package asia.virtualmc.vLibrary.utilities.miscellaneous;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for working with {@link Instant} time operations.
 */
public class InstantUtils {

    /**
     * Returns the current system time as an {@link Instant}.
     *
     * @return the current time
     */
    public static Instant getPresent() {
        return Instant.now();
    }

    /**
     * Returns an {@link Instant} representing the current time plus the specified number of seconds.
     *
     * @param seconds the number of seconds to add
     * @return the future time
     */
    public static Instant getFuture(long seconds) {
        return Instant.now().plusSeconds(seconds);
    }

    /**
     * Checks if the specified time has already passed (expired).
     *
     * @param time the time to check
     * @return true if the specified time is before the current time; false otherwise
     */
    public static boolean hasExpired(Instant time) {
        return Instant.now().isAfter(time);
    }

    /**
     * Checks if the specified time is still in the future.
     *
     * @param time the time to check
     * @return true if the specified time is after the current time; false otherwise
     */
    public static boolean isFuture(Instant time) {
        return Instant.now().isBefore(time);
    }

    /**
     * Returns the number of seconds remaining until the specified time.
     * Returns 0 if the time has already passed.
     *
     * @param time the future time
     * @return the remaining seconds, or 0 if expired
     */
    public static long getRemainingSeconds(Instant time) {
        return Math.max(0, time.getEpochSecond() - Instant.now().getEpochSecond());
    }

    /**
     * Checks if the specified time is within a range of seconds from now.
     *
     * @param time         the target time
     * @param secondsRange the range in seconds (± range from now)
     * @return true if the time is within the range; false otherwise
     */
    public static boolean isWithin(Instant time, long secondsRange) {
        Instant now = Instant.now();
        return !now.isBefore(time.minusSeconds(secondsRange)) && !now.isAfter(time.plusSeconds(secondsRange));
    }

    /**
     * Formats the specified {@link Instant} as a readable local date-time string.
     *
     * @param time the time to format
     * @return a formatted date-time string in the system's default time zone
     */
    public static String toReadableFormat(Instant time) {
        return DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(ZoneId.systemDefault()).format(time);
    }

    /**
     * Returns the percentage of time elapsed between the start time and the future time.
     * <p>
     * For example, if {@code futureTime} is 30 seconds after {@code startTime}, and
     * 15 seconds have passed since {@code startTime}, this method will return {@code 50.0}.
     * <p>
     * If the current time is after {@code futureTime}, it returns {@code 100.0}.
     * If the current time is before {@code startTime}, it returns {@code 0.0}.
     *
     * @param startTime  the starting {@link Instant}
     * @param futureTime the target {@link Instant} in the future
     * @return the percentage of elapsed time from startTime to futureTime, as a double between 0.0 and 100.0
     */
    public static double getPercent(Instant startTime, Instant futureTime) {
        Instant now = Instant.now();
        long totalMillis = futureTime.toEpochMilli() - startTime.toEpochMilli();
        long elapsedMillis = now.toEpochMilli() - startTime.toEpochMilli();

        if (totalMillis <= 0) {
            return 100.0;
        }

        double percent = (elapsedMillis / (double) totalMillis) * 100.0;
        return Math.max(0.0, Math.min(100.0, percent));
    }

    /**
     * Converts an {@link Instant} to a long value representing epoch seconds for database storage.
     *
     * @param time the {@link Instant} to convert
     * @return the epoch second representation of the time
     */
    public static long toLong(Instant time) {
        if (time == null) {
            throw new IllegalArgumentException("Instant cannot be null");
        }
        return time.getEpochSecond();
    }

    /**
     * Converts a long value representing epoch seconds from the database into an {@link Instant}.
     *
     * @param epochSeconds the epoch seconds value
     * @return the corresponding {@link Instant}
     */
    public static Instant toInstant(long epochSeconds) {
        return Instant.ofEpochSecond(epochSeconds);
    }
}