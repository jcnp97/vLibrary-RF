package asia.virtualmc.vLibrary.utilities.core;

import asia.virtualmc.vLibrary.utilities.text.DigitUtils;

public class DecorationUtils {

    /**
     * Converts a double value to a long by rounding it to 2 decimal places
     * and multiplying by 100 to preserve precision.
     *
     * @param value the double value to convert
     * @return the converted long value
     */
    public static Long convertToLong(double value) {
        double formatDouble = DigitUtils.getPreciseValue(value, 2);
        return (long) (formatDouble * 100);
    }

    /**
     * Converts a long value back to a double by dividing by 100,
     * reversing the precision scaling applied earlier.
     *
     * @param value the long value to convert
     * @return the converted double value
     */
    public static double convertToDouble(long value) {
        return (double) (value / 100);
    }
}
