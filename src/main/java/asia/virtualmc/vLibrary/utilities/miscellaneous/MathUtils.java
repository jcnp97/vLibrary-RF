package asia.virtualmc.vLibrary.utilities.miscellaneous;

public class MathUtils {

    public static double sum(double[] array) {
        double sum = 0.0;
        for (double value : array) {
            sum += value;
        }
        return sum;
    }

    public static double average(double[] array) {
        double sum = 0.0;
        for (double value : array) {
            sum += value;
        }
        return sum/array.length;
    }

    /**
     * Calculates the percentage of currentValue out of maxValue as a double.
     * Returns a value between 0.00 and 100.0.
     *
     * @param currentValue the current value
     * @param maxValue     the maximum value
     * @return percentage (0.00 to 100.0)
     */
    public static double percent(double currentValue, double maxValue) {
        if (maxValue <= 0.0) return 0.0;
        double result = (currentValue / maxValue) * 100.0;
        return Math.min(100.0, Math.max(0.0, result));
    }

    /**
     * Calculates the percentage of currentValue out of maxValue using integer values.
     * Returns a double between 0.00 and 100.0.
     *
     * @param currentValue the current integer value
     * @param maxValue     the maximum integer value
     * @return percentage (0.00 to 100.0)
     */
    public static double percent(int currentValue, int maxValue) {
        if (maxValue <= 0) return 0.0;
        double result = ((double) currentValue / maxValue) * 100.0;
        return Math.min(100.0, Math.max(0.0, result));
    }
}
