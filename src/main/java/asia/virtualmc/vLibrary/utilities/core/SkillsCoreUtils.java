package asia.virtualmc.vLibrary.utilities.core;

import asia.virtualmc.vLibrary.enums.EnumsLib;
import asia.virtualmc.vLibrary.utilities.messages.MessageUtils;
import asia.virtualmc.vLibrary.utilities.minecraft.EffectUtils;
import asia.virtualmc.vLibrary.utilities.minecraft.SoundUtils;
import asia.virtualmc.vLibrary.utilities.text.DigitUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SkillsCoreUtils {
    private static final int MIN_LEVEL = 1;
    private static final int MAX_LEVEL = 120;
    private static final int MAX_EXP = 2_147_483_647;

    public void levelingEffects(@NotNull Player player, String skillName,
                                int previousLevel, int newLevel, int traitPoints) {

        String command = "";

        if (skillName.equalsIgnoreCase("Fishing")) {
            command = "/vfish traits";
        } else if (skillName.equalsIgnoreCase("Mining")) {
            command = "/vmine traits";
        }

        MessageUtils.sendTitleMessage(player,
                "<gradient:#ebd197:#a2790d>" + skillName + "</gradient>",
                "<#00FFA2>Level " + previousLevel + " ➛ " + newLevel);
        MessageUtils.sendPlayerMessage(player,"<gradient:#FFE6A3:#FFD06E>You have </gradient><#00FFA2>" +
                traitPoints + " trait points <gradient:#FFE6A3:#FFD06E>that you can spend on " + command + ".</gradient>");

        if (newLevel == 99 || newLevel == MAX_LEVEL) {
            EffectUtils.spawnFireworks(player, 12, 3);
            SoundUtils.playSound(player, "cozyvanilla.all.master_levelup");
        } else {

            EffectUtils.spawnFireworks(player, 5, 5);
            SoundUtils.playSound(player, "cozyvanilla." + skillName.toLowerCase() + ".default_levelup");
        }
    }

    public double getNewEXP(@NotNull EnumsLib.UpdateType type, double currentEXP, double value) {
        if (value <= 0) return currentEXP;

        value = DigitUtils.getPreciseValue(value, 2);
        switch (type) {
            case ADD -> { return Math.min(MAX_EXP, currentEXP + value); }
            case SUBTRACT -> { return Math.max(0, currentEXP - value); }
            case SET -> { return Math.max(0, Math.min(value, MAX_EXP)); }
            default -> { return currentEXP; }
        }
    }

    public int getNewLevel(@NotNull EnumsLib.UpdateType type, int currentLevel, int value) {
        if (value <= 0) return currentLevel;

        switch (type) {
            case ADD -> { return Math.min(MAX_LEVEL, currentLevel + value); }
            case SUBTRACT -> { return Math.max(MIN_LEVEL, currentLevel - value); }
            case SET -> { return Math.min(value, MAX_LEVEL); }
            default -> { return currentLevel; }
        }
    }

    public double getNewXPM(@NotNull EnumsLib.UpdateType type, double currentXPM, double value) {
        if (value < 0) return currentXPM;

        value = DigitUtils.getPreciseValue(value, 2);
        switch (type) {
            case ADD -> { return currentXPM + value; }
            case SUBTRACT -> { return Math.max(0, currentXPM - value); }
            case SET -> { return Math.max(0, value); }
            default -> { return currentXPM; }
        }
    }

    public double getNewBXP(@NotNull EnumsLib.UpdateType type, double currentBXP, double value) {
        if (value <= 0) return currentBXP;

        value = DigitUtils.getPreciseValue(value, 2);
        switch (type) {
            case ADD -> { return currentBXP + value; }
            case SUBTRACT -> { return Math.max(0, currentBXP - value); }
            case SET -> { return Math.max(0, value); }
            default -> { return currentBXP; }
        }
    }

    public int getNewTraitPoints(@NotNull EnumsLib.UpdateType type, int currentTP, int value) {
        if (value <= 0) return currentTP;

        switch (type) {
            case ADD -> { return currentTP + value; }
            case SUBTRACT -> { return Math.max(0, currentTP - value); }
            case SET -> { return value; }
            default -> { return currentTP; }
        }
    }

    public int getNewTalentPoints(@NotNull EnumsLib.UpdateType type, int currentTP, int value) {
        if (value <= 0) return currentTP;

        switch (type) {
            case ADD -> { return currentTP + value; }
            case SUBTRACT -> { return Math.max(0, currentTP - value); }
            case SET -> { return value; }
            default -> { return currentTP; }
        }
    }

    public int getNewLuck(@NotNull EnumsLib.UpdateType type, int luck, int value) {
        if (value <= 0) return luck;

        switch (type) {
            case ADD -> { return luck + value; }
            case SUBTRACT -> { return Math.max(0, luck - value); }
            case SET -> { return value; }
            default -> { return luck; }
        }
    }
}
