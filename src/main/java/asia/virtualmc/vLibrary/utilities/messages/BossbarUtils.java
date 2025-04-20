package asia.virtualmc.vLibrary.utilities.messages;

import asia.virtualmc.vLibrary.enums.EnumsLib;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;

public class BossbarUtils {

    public static BossBar.Color getColor(EnumsLib.BossBarColor color) {
        return switch (color) {
            case BLUE -> BossBar.Color.BLUE;
            case GREEN -> BossBar.Color.GREEN;
            case PINK -> BossBar.Color.PINK;
            case PURPLE -> BossBar.Color.PURPLE;
            case RED -> BossBar.Color.RED;
            case WHITE -> BossBar.Color.WHITE;
            case YELLOW -> BossBar.Color.YELLOW;
        };
    }

    public static BossBar getBossBar(String message, EnumsLib.BossBarColor color, float progress) {
        Component component = AdventureUtils.convertToComponent(message);
        float progressLimit = Math.max(0.0f, Math.min(progress, 1.0f));

        return BossBar.bossBar(
                component,
                progressLimit,
                getColor(color),
                BossBar.Overlay.PROGRESS
        );
    }

    public static void modifyBossBar(BossBar bossBar, String message, float progress) {
        Component component = AdventureUtils.convertToComponent(message);
        float progressLimit = Math.max(0.0f, Math.min(progress, 1.0f));

        bossBar.name(component);
        bossBar.progress(progressLimit);
    }
}
