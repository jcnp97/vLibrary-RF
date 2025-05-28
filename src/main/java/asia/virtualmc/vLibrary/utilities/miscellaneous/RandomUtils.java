package asia.virtualmc.vLibrary.utilities.miscellaneous;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

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

    public static boolean roll(double chance) {
        if (chance <= 0.0) return false;
        if (chance >= 100.0) return true;
        return random.nextDouble() * 100 < chance;
    }

    public static int getDrop(double[] weights) {
        double totalWeight = MathUtils.sum(weights);
        if (totalWeight <= 0) return 0;

        double rand = random.nextDouble() * totalWeight;
        double cumulativeWeight = 0;

        for (int i = 0; i < weights.length; i++) {
            cumulativeWeight += weights[i];
            if (rand < cumulativeWeight) {
                return i + 1;
            }
        }

        throw new IllegalStateException("Unexpected state in RandomUtils.getDrop()");
    }

    public static String getString(List<String> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }

        int index = ThreadLocalRandom.current().nextInt(list.size());
        return list.get(index);
    }
}
