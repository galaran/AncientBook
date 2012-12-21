package mccity.plugins.ancientbook;

import me.galaran.bukkitutils.ancientbook.IOUtils;
import me.galaran.bukkitutils.ancientbook.text.Messaging;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.meta.BookMeta;

import java.io.File;
import java.util.*;

public class BooksManager {

    private final File booksFile;

    private final Map<Short, BookMeta> books = new TreeMap<Short, BookMeta>();

    public BooksManager(File dataDir) {
        booksFile = new File(dataDir, "books.yml");
    }

    public void addBook(short data, BookMeta book, CommandSender sender) {
        if (books.put(data, book) != null) {
            Messaging.send(sender, "add.replaced", data);
        } else {
            Messaging.send(sender, "add.added", data);
        }
        saveBooks();
    }

    public void removeBook(short data, CommandSender sender) {
        BookMeta removed = books.remove(data);
        if (removed != null) {
            Messaging.send(sender, "remove.ok", removed.getTitle(), data);
            saveBooks();
        } else {
            Messaging.send(sender, "book.no-such", data);
        }
    }

    public BookMeta getBook(short data) {
        return books.get(data);
    }

    public Map<Short, BookMeta> getMapping() {
        return Collections.unmodifiableMap(books);
    }

    private void saveBooks() {
        YamlConfiguration root = new YamlConfiguration();
        for (Map.Entry<Short, BookMeta> entry : books.entrySet()) {
            root.set(String.valueOf(entry.getKey()), serializeBookMeta(entry.getValue()));
        }
        IOUtils.saveYml(root, booksFile);
    }

    public boolean reloadBooks() {
        books.clear();

        IOUtils.createFileIfNotExists(booksFile);
        YamlConfiguration root = YamlConfiguration.loadConfiguration(booksFile);
        try {
            Set<String> keys = root.getKeys(false);
            for (String key : keys) {
                books.put(Short.parseShort(key), deserializeBookMeta(root.getConfigurationSection(key)));
            }
            Messaging.log(books.size() + " book templates loaded");
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private Map<String, Object> serializeBookMeta(BookMeta meta) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("title", meta.getTitle());
        map.put("author", meta.getAuthor());
        map.put("pages", meta.getPages());
        return map;
    }

    private BookMeta deserializeBookMeta(ConfigurationSection section) {
        BookMeta result = (BookMeta) Bukkit.getItemFactory().getItemMeta(Material.WRITTEN_BOOK);
        result.setTitle(section.getString("title"));
        result.setAuthor(section.getString("author"));
        result.setPages(section.getStringList("pages"));
        return result;
    }
}
