package asia.virtualmc.vLibrary.utilities.text;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class SetUtils {

    /**
     * Serializes a Set of Integer values into a single comma-separated String.
     *
     * @param set the Set of Integer to serialize; may be null or empty
     * @return a comma-separated String representation of the set; an empty string if the set is null or empty
     */
    public static String serialize(Set<Integer> set) {
        if (set == null || set.isEmpty()) {
            return "";
        }
        return set.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    /**
     * Deserializes a comma-separated String into a Set of Integer values.
     *
     * @param data the comma-separated String to parse; may be null or empty
     * @return a Set of Integer parsed from the input; an empty Set if the input is null, empty, or only contains whitespace
     * @throws NumberFormatException if any token in the string is not a valid integer
     */
    public static Set<Integer> deserialize(String data) {
        Set<Integer> result = new HashSet<>();
        if (data == null || data.trim().isEmpty()) {
            return result;
        }
        String[] tokens = data.split(",");
        for (String token : tokens) {
            String trimmed = token.trim();
            if (!trimmed.isEmpty()) {
                result.add(Integer.parseInt(trimmed));
            }
        }
        return result;
    }
}
