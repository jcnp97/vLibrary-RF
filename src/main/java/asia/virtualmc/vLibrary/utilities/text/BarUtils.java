package asia.virtualmc.vLibrary.utilities.text;

import asia.virtualmc.vLibrary.VLibrary;
import asia.virtualmc.vLibrary.utilities.files.YAMLUtils;
import asia.virtualmc.vLibrary.utilities.messages.ConsoleUtils;
import asia.virtualmc.vLibrary.utilities.miscellaneous.MathUtils;

import java.util.List;

public class BarUtils {
    private static List<String> unicodes;

    private static void initialize() {
        unicodes = YAMLUtils.getList(VLibrary.getInstance(), "glyphs/progress_bar.yml", "progress-bar");
        if (unicodes == null) {
            ConsoleUtils.severe("Unable to read glyphs/progress_bar.yml.");
            unicodes.add("null");
        }
    }

    public static String get(double current, double max) {
        if (unicodes == null) {
            initialize();
        }

        double percent = MathUtils.percent(current, max);
        int index = (int) (percent / 5.00);

        return "<white>" + unicodes.get(index) +
                " <gray>(<green>" + DigitUtils.format(percent) + "%<gray>)";
    }

    public static String get(int current, int max) {
        if (unicodes == null) {
            initialize();
        }

        double percent = MathUtils.percent(current, max);
        int index = (int) (percent / 5.00);

        return "<white>" + unicodes.get(index) +
                " <gray>(<green>" + DigitUtils.format(percent) + "%<gray>)";
    }
}
