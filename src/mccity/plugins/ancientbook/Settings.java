package mccity.plugins.ancientbook;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Settings {

    public static String lang;

    public static int startData;

    private static boolean isFixEncoding;
    private static String wrongSymbols;
    private static String rightSymbols;

    public static boolean isConvertBookWorm;

    public static void load(File configFile) throws RuntimeException {
        FileConfiguration root = YamlConfiguration.loadConfiguration(configFile);

        lang = root.getString("lang", "english");

        startData = root.getInt("start-data", 1024);

        isFixEncoding = root.getBoolean("fix-encoding", true);
        wrongSymbols = root.getString("wrong-symbols");
        rightSymbols = root.getString("right-symbols");
        if (isFixEncoding) {
            if (wrongSymbols == null || rightSymbols == null || wrongSymbols.length() != rightSymbols.length()) {
                throw new IllegalArgumentException("Encoding strings must the same length");
            }
        }

        isConvertBookWorm = root.getBoolean("convert-bookworm", true);
    }

    public static String fixEncoding(String string) {
        if (!isFixEncoding) return string;
        if (string == null) return null;

        StringBuilder result = new StringBuilder();
        for (char curChar : string.toCharArray()) {
            int index = wrongSymbols.indexOf(curChar);
            if (index != -1) {
                result.append(rightSymbols.charAt(index));
            } else {
                result.append(curChar);
            }
        }

        return result.toString();
    }
}
