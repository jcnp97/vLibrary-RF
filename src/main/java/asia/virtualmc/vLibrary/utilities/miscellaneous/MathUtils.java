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
}
