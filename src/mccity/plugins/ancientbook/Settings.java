package mccity.plugins.ancientbook;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Settings {

    public static String lang;

    public static int startData;

    public static void load(File configFile) throws RuntimeException {
        FileConfiguration root = YamlConfiguration.loadConfiguration(configFile);

        lang = root.getString("lang", "english");

        startData = root.getInt("start-data", 1024);
    }
}
