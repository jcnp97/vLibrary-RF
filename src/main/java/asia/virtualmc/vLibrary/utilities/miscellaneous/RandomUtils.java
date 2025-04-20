package asia.virtualmc.vLibrary.utilities.miscellaneous;

import java.util.Random;

public class RandomUtils {
    private static final Random random = new Random();

    public static int getInt(int min, int max) {
        if (min == max) return min;
        return random.nextInt(Math.min(min, max), Math.max(min, max) + 1);
    }

    public static int getInt(int max) {
        return getInt(0, max);
    }

    public static double getDouble(double min, double max) {
        if (min == max) return min;
        return min + (max - min) * random.nextDouble();
    }

    public static boolean rollChance(double chance) {
        if (chance <= 0.0) return false;
        if (chance >= 100.0) return true;
        return random.nextDouble() * 100 < chance;
    }
}
