package asia.virtualmc.vLibrary.utilities.core;

import asia.virtualmc.vLibrary.utilities.text.DigitUtils;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class EXPDisplayUtils {

    /**
     * Displays a temporary boss bar to the player showing EXP progress toward the next level.
     *
     * @param plugin        the plugin instance used for scheduling the boss bar removal
     * @param player        the player to display the boss bar to
     * @param skillName     the name of the skill being leveled
     * @param currentExp    the player's current experience in the skill
     * @param nextLevelExp  the experience required to reach the next level
     * @param addedExp      the amount of experience recently gained
     * @param currentLevel  the player's current level in the skill
     */
    public static void showEXPBossBar(@NotNull Plugin plugin,
                                      @NotNull Player player,
                                      String skillName,
                                      double currentExp,
                                      int nextLevelExp,
                                      double addedExp,
                                      int currentLevel) {

        if (nextLevelExp == 0) {
            showEXPBossBarMaxed(plugin, player, skillName, currentExp, addedExp, currentLevel);
            return;
        }

        float progress = (float) currentExp / Math.max(nextLevelExp, 1);
        double hourlyExp = DigitUtils.getPreciseValue(addedExp * 240, 2);
        String percentProgress = DigitUtils.getFormatted(Math.min(100.0, progress * 100.0));

        Component bossBarText = Component.text()
                .append(Component.text(skillName + " Lv. " + currentLevel, NamedTextColor.WHITE))
                .append(Component.text(" | ", NamedTextColor.GRAY))
                .append(Component.text(percentProgress + "%", NamedTextColor.YELLOW))
                .append(Component.text(" | ", NamedTextColor.GRAY))
                .append(Component.text("+" + addedExp + " EXP", NamedTextColor.GREEN))
                .append(Component.text(" | ", NamedTextColor.GRAY))
                .append(Component.text(DigitUtils.getFormatted(hourlyExp) + " XP/HR", NamedTextColor.RED))
                .build();

        BossBar bossBar = BossBar.bossBar(
                bossBarText,
                0.0f,
                BossBar.Color.YELLOW,
                BossBar.Overlay.PROGRESS
        );
        bossBar.progress(Math.min(progress, 1.0f));

        player.showBossBar(bossBar);
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> player.hideBossBar(bossBar), 100L);
    }

    /**
     * Displays a max-level boss bar for a skill where no further experience is needed.
     *
     * @param plugin        the plugin instance used for scheduling the boss bar removal
     * @param player        the player to display the boss bar to
     * @param skillName     the name of the skill being leveled
     * @param currentExp    the player's total experience in the skill
     * @param addedExp      the amount of experience recently gained
     * @param currentLevel  the player's current level in the skill
     */
    public static void showEXPBossBarMaxed(@NotNull Plugin plugin,
                                           @NotNull Player player,
                                           String skillName,
                                           double currentExp,
                                           double addedExp,
                                           int currentLevel) {

        double hourlyExp = DigitUtils.getPreciseValue(addedExp * 240, 2);
        String formattedEXP = DigitUtils.getFormatted(currentExp / 1000000);

        Component bossBarText = Component.text()
                .append(Component.text(skillName + " Lv. " + currentLevel, NamedTextColor.WHITE))
                .append(Component.text(" | ", NamedTextColor.GRAY))
                .append(Component.text(formattedEXP + "M EXP", NamedTextColor.YELLOW))
                .append(Component.text(" | ", NamedTextColor.GRAY))
                .append(Component.text("+" + addedExp + " EXP", NamedTextColor.GREEN))
                .append(Component.text(" | ", NamedTextColor.GRAY))
                .append(Component.text(DigitUtils.getFormatted(hourlyExp) + " XP/HR", NamedTextColor.RED))
                .build();

        BossBar bossBar = BossBar.bossBar(
                bossBarText,
                1.0f,
                BossBar.Color.YELLOW,
                BossBar.Overlay.PROGRESS
        );

        player.showBossBar(bossBar);
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> player.hideBossBar(bossBar), 100L);
    }

    /**
     * Displays an action bar to the player showing current experience progress and recent gains.
     *
     * @param player        the player to display the action bar to
     * @param currentExp    the player's current experience in the skill
     * @param expGain       the amount of experience recently gained
     * @param bonusXP       any bonus experience gained in addition to base EXP
     * @param nextLevelExp  the experience required to reach the next level
     */
    public static void showEXPActionBar(@NotNull Player player,
                                        double currentExp,
                                        double expGain,
                                        double bonusXP,
                                        int nextLevelExp) {

        if (nextLevelExp == 0) {
            showEXPActionBarMaxed(player, currentExp, expGain, bonusXP);
            return;
        }

        currentExp /= 100000;
        nextLevelExp /= 100000;

        if (bonusXP > 0) {
            Component actionBarText = Component.text()
                    .append(Component.text("+" + expGain + " EXP (+" + bonusXP + " Bonus EXP) ", NamedTextColor.GREEN))
                    .append(Component.text("(" + DigitUtils.getFormatted(currentExp) + "K", NamedTextColor.GRAY))
                    .append(Component.text("/" + DigitUtils.getFormatted(nextLevelExp) + "K EXP)", NamedTextColor.GRAY))
                    .build();
            player.sendActionBar(actionBarText);
        } else {
            Component actionBarText = Component.text()
                    .append(Component.text("+" + expGain + " EXP ", NamedTextColor.GREEN))
                    .append(Component.text("(" + DigitUtils.getFormatted(currentExp) + "K", NamedTextColor.GRAY))
                    .append(Component.text("/" + DigitUtils.getFormatted(nextLevelExp) + "K EXP)", NamedTextColor.GRAY))
                    .build();
            player.sendActionBar(actionBarText);
        }
    }

    /**
     * Displays an action bar to the player for a maxed-out skill level, showing current EXP and recent gains.
     *
     * @param player     the player to display the action bar to
     * @param currentExp the player's total experience in the skill
     * @param expGain    the amount of experience recently gained
     * @param bonusXP    any bonus experience gained in addition to base EXP
     */
    public static void showEXPActionBarMaxed(@NotNull Player player,
                                             double currentExp,
                                             double expGain,
                                             double bonusXP
    ) {

        currentExp /= 1000000;
        if (bonusXP > 0) {
            Component actionBarText = Component.text()
                    .append(Component.text("+" + expGain + " EXP (+" + bonusXP + " Bonus EXP) ", NamedTextColor.GREEN))
                    .append(Component.text("(" + DigitUtils.getFormatted(currentExp) + " M", NamedTextColor.GRAY))
                    .build();
            player.sendActionBar(actionBarText);
        } else {
            Component actionBarText = Component.text()
                    .append(Component.text("+" + expGain + " EXP ", NamedTextColor.GREEN))
                    .append(Component.text("(" + DigitUtils.getFormatted(currentExp) + " M)", NamedTextColor.GRAY))
                    .build();
            player.sendActionBar(actionBarText);
        }
    }
}
