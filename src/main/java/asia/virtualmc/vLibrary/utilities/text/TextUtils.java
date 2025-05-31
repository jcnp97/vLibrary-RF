package asia.virtualmc.vLibrary.utilities.text;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TextUtils {

    /**
     * Formats a string by replacing underscores with spaces, converting all characters to lowercase,
     * and capitalizing the first letter of each word.
     *
     * @param string The input string to format (e.g., "HELLO_WORLD" becomes "Hello World").
     * @return A human-readable version of the string with proper capitalization, or the original string if null or empty.
     */
    public static String format(String string) {
        if (string == null || string.isEmpty()) return string;

        String formatted = string.replace("_", " ").toLowerCase();
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = true;

        for (char c : formatted.toCharArray()) {
            if (capitalizeNext && Character.isLetter(c)) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                result.append(c);
            }
            if (c == ' ') {
                capitalizeNext = true;
            }
        }

        return result.toString();
    }

    /**
     * Converts a string to key format by:
     * - Removing all non-letter characters (only a–z and A–Z allowed),
     * - Replacing whitespace with underscores,
     * - Converting to lowercase.
     * Example: "Player's Data 123!" -> "players_data"
     *
     * @param string The input string.
     * @return A lowercase, underscore-separated string with only a-z characters.
     */
    public static String toKeyFormat(String string) {
        if (string == null || string.isEmpty()) return "";

        // Remove everything except letters and spaces
        String cleaned = string.replaceAll("[^a-zA-Z\\s]", "").trim();

        // Replace all whitespace with underscore, then convert to lowercase
        return cleaned.replaceAll("\\s+", "_").toLowerCase();
    }

    /**
     * Splits lines of lore into smaller lines with a maximum character count per line,
     * ensuring words are not split in the middle.
     *
     * @param stringList      the original list of strings to be split
     * @param charCount the maximum number of characters allowed per line
     * @return a new list of lore strings, formatted to fit within the specified character count
     */
    public static List<String> divide(List<String> stringList, int charCount) {
        List<String> formattedLore = new ArrayList<>();

        for (String line : stringList) {
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

    /**
     * Converts a single string to a new stylized font using a custom character mapping.
     *
     * @param input the string to convert
     * @return the converted string with stylized characters
     */
    public static String toSmallFont(String input) {
        String NORMAL = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String CONVERTED = "ᴀʙᴄᴅᴇғɢʜɪᴊᴋʟᴍɴᴏᴘǫʀsᴛᴜᴠᴡxʏᴢABCDEFGHIJKLMNOPQRSTUVWXYZ";

        StringBuilder converted = new StringBuilder();
        for (char ch : input.toCharArray()) {
            int index = NORMAL.indexOf(ch);
            converted.append(index != -1 ? CONVERTED.charAt(index) : ch);
        }
        return converted.toString();
    }

    /**
     * Converts each string in the input list to a new stylized font using a custom character mapping.
     *
     * @param inputList the list of strings to convert
     * @return a new list with each string converted to the custom font style
     */
    public static List<String> convertListToNewFont(List<String> inputList) {
        return inputList.stream()
                .map(TextUtils::convertString)
                .collect(Collectors.toList());
    }

    /**
     * Converts a string to a custom font by replacing normal alphabet characters with their stylized equivalents.
     *
     * @param input the original string to convert
     * @return the converted string with stylized characters
     */
    private static String convertString(String input) {
        String NORMAL = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String CONVERTED = "ᴀʙᴄᴅᴇғɢʜɪᴊᴋʟᴍɴᴏᴘǫʀsᴛᴜᴠᴡxʏᴢABCDEFGHIJKLMNOPQRSTUVWXYZ";

        StringBuilder converted = new StringBuilder();
        for (char ch : input.toCharArray()) {
            int index = NORMAL.indexOf(ch);
            converted.append(index != -1 ? CONVERTED.charAt(index) : ch);
        }
        return converted.toString();
    }

    /**
     * Generates a text-based progress bar using the specified filled and empty symbols.
     * The progress bar visualizes the proportion of {@code value} relative to {@code maxValue},
     * distributed across {@code totalBars}.
     *
     * <p>For example, if {@code filled = "<dark_green>❙"}, {@code empty = "<dark_gray>❙"},
     * {@code value = 10.0}, {@code maxValue = 20.0}, and {@code totalBars = 20}, the method
     * will return a string with 10 filled bars and 10 empty bars to represent 50% progress.</p>
     *
     * <p>If {@code value} exceeds {@code maxValue}, the progress bar will be fully filled.
     * If {@code maxValue} is zero or negative, the bar will be fully empty to avoid division by zero.</p>
     *
     * @param filled     The string to represent a filled segment of the progress bar.
     * @param empty      The string to represent an empty segment of the progress bar.
     * @param value      The current value representing progress.
     * @param maxValue   The maximum possible value (total progress threshold).
     * @param totalBars  The total number of segments in the progress bar.
     * @return A string that visually represents the progress bar.
     */
    public static String getProgressBar(String filled, String empty, double value, double maxValue, int totalBars) {
        if (maxValue <= 0) {
            return empty.repeat(totalBars);
        }

        double progress = Math.max(0.0, Math.min(1.0, value / maxValue));
        int filledBars = (int) Math.round(progress * totalBars);
        int emptyBars = totalBars - filledBars;

        return filled.repeat(filledBars) + empty.repeat(emptyBars);
    }

    /**
     * Converts a comma-separated string to an array of integers.
     *
     * @param input the input string (e.g., "1, 2, 3, 4, 5")
     * @return an array of integers parsed from the input string
     */
    public static int[] toIntArray(String input) {
        if (input == null || input.isBlank()) {
            return new int[0];
        }

        String[] tokens = input.split(",");
        List<Integer> tempList = new ArrayList<>();

        for (String token : tokens) {
            try {
                tempList.add(Integer.parseInt(token.trim()));
            } catch (NumberFormatException e) {
                // Skip invalid entries
            }
        }

        int[] result = new int[tempList.size()];
        for (int i = 0; i < tempList.size(); i++) {
            result[i] = tempList.get(i);
        }

        return result;
    }
}
