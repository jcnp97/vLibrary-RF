package asia.virtualmc.vLibrary.utilities.core;

import asia.virtualmc.vLibrary.VLibrary;
import asia.virtualmc.vLibrary.enums.EnumsLib;
import asia.virtualmc.vLibrary.helpers.AsyncUtils;
import asia.virtualmc.vLibrary.utilities.messages.ActionBarUtils;
import asia.virtualmc.vLibrary.utilities.messages.BossbarUtils;
import asia.virtualmc.vLibrary.utilities.text.DigitUtils;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EXPDisplayUtils {

    public static String getEXPMessage(String skillName,
                                       int currentLevel,
                                       double addedEXP,
                                       float progress) {

        String percentage = DigitUtils.format(Math.min(100.0, (progress) * 100.0)) + "%";
        if (currentLevel >= 120) {
            percentage = "0.0%";
        }

        String hourlyExp = DigitUtils.format(addedEXP * 240);

        return "<white>" + skillName + " Lv. " + currentLevel + " <gray>(<yellow>" + percentage + "<gray>) | <green>+ "
                + addedEXP + " XP <gray>| <red>" + hourlyExp + " XP/HR";
    }

    public static void buildEXPBossBar(Player player,
                                       String skillName,
                                       double currentEXP,
                                       int currentLevel,
                                       int nextLevelEXP,
                                       double addedEXP) {

        AsyncUtils.runAsyncThenSync(VLibrary.getInstance(),
                () -> {
                    float progress = Math.max(0.0f, Math.min(1.0f, (float) currentEXP / nextLevelEXP));
                    String message = getEXPMessage(skillName, currentLevel, addedEXP, progress);
                    return new Object[] { progress, message };
                },
                result -> {
                    float progress = (float) result[0];
                    String message = (String) result[1];

                    BossBar bossBar = BossbarUtils.getBossBar(message, EnumsLib.BossBarColor.GREEN, progress);
                    if (player != null) {
                        BossbarUtils.showBossBar(player, bossBar, 5.0);
                    }
                }
        );
    }

    public static void buildEXPActionBar(@NotNull Player player,
                                         String skillName,
                                         double addedEXP,
                                         double bonusEXP) {

        String message = "<green>+" + addedEXP + " " + skillName + " EXP <gray>(<aqua>" + bonusEXP + " Bonus EXP<gray>)";
        ActionBarUtils.showActionBar(player, message);
    }
}
