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
    public static String getFormatted(String string) {
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
     * Splits lines of lore into smaller lines with a maximum character count per line,
     * ensuring words are not split in the middle.
     *
     * @param stringList      the original list of strings to be split
     * @param charCount the maximum number of characters allowed per line
     * @return a new list of lore strings, formatted to fit within the specified character count
     */
    public static List<String> divideStringList(List<String> stringList, int charCount) {
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
}
