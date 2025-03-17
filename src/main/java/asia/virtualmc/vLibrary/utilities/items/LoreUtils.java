package asia.virtualmc.vLibrary.utilities.items;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LoreUtils {

    public static List<String> applyMonospaceFont(List<String> inputList) {
        return inputList.stream()
                .map(LoreUtils::convertToMonospaceFont)
                .collect(Collectors.toList());
    }

    private static String convertToMonospaceFont(String input) {
        String NORMAL = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String CONVERTED = "ᴀʙᴄᴅᴇғɢʜɪᴊᴋʟᴍɴᴏᴘǫʀsᴛᴜᴠᴡxʏᴢABCDEFGHIJKLMNOPQRSTUVWXYZ";

        StringBuilder converted = new StringBuilder();
        for (char ch : input.toCharArray()) {
            int index = NORMAL.indexOf(ch);
            converted.append(index != -1 ? CONVERTED.charAt(index) : ch);
        }
        return converted.toString();
    }

    public static List<String> applyCharLimit(List<String> lore, int charCount) {
        List<String> formattedLore = new ArrayList<>();

        for (String line : lore) {
            String[] words = line.split(" ");
            StringBuilder currentLine = new StringBuilder();

            for (String word : words) {
                if (currentLine.length() + word.length() + 1 > charCount) {
                    formattedLore.add(currentLine.toString().trim());
                    currentLine.setLength(0);
                }

                if (!currentLine.isEmpty()) {
                    currentLine.append(" ");
                }
                currentLine.append(word);
            }

            if (!currentLine.isEmpty()) {
                formattedLore.add(currentLine.toString());
            }
        }

        return formattedLore;
    }

    public static void addLore(ItemStack item, String lore) {
        if (item == null || lore == null) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        List<String> currentLore = meta.hasLore() ? meta.getLore() : new ArrayList<>();

        lore = lore.replace("\\n", "\n");
        currentLore.addAll(Arrays.asList(lore.split("\n")));
        meta.setLore(currentLore);

        item.setItemMeta(meta);
    }
}
