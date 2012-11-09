package mccity.plugins.ancientbook;

import me.galaran.bukkitutils.ancientbook.Book;
import me.galaran.bukkitutils.ancientbook.GUtils;
import me.galaran.bukkitutils.ancientbook.YamlUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class BooksManager {

    private final File booksFile;

    private final Map<Short, Book> bookTemplates = new TreeMap<Short, Book>();

    public BooksManager(File dataDir) {
        booksFile = new File(dataDir, "books.yml");
    }

    public void addBook(short data, Book book, CommandSender sender) {
        if (data < Settings.startData) {
            GUtils.sendTranslated(sender, "add.data-too-low", data, Settings.startData);
            return;
        }

        // fix encoding
        book.setAuthor(Settings.fixEncoding(book.getAuthor()));
        book.setTitle(Settings.fixEncoding(book.getTitle()));
        String[] pages = book.getPages();
        for (int i = 0; i < pages.length; i++) {
            pages[i] = Settings.fixEncoding(pages[i]);
        }

        if (bookTemplates.put(data, book) != null) {
            GUtils.sendTranslated(sender, "add.replaced", data);
        } else {
            GUtils.sendTranslated(sender, "add.added", data);
        }
        saveBooks();
    }

    public void removeBook(short data, CommandSender sender) {
        Book removed = bookTemplates.remove(data);
        if (removed != null) {
            GUtils.sendTranslated(sender, "remove.ok", removed.getTitle(), data);
            saveBooks();
        } else {
            GUtils.sendTranslated(sender, "book.no-such", data);
        }
    }

    public Book getBook(short data) {
        return bookTemplates.get(data);
    }

    public Map<Short, Book> getMapping() {
        return Collections.unmodifiableMap(bookTemplates);
    }

    private void saveBooks() {
        YamlConfiguration root = new YamlConfiguration();
        for (Map.Entry<Short, Book> entry : bookTemplates.entrySet()) {
            root.set(String.valueOf(entry.getKey()), entry.getValue().serialize());
        }
        YamlUtils.saveYml(root, booksFile);
    }

    public boolean reloadBooks() {
        bookTemplates.clear();

        YamlUtils.createFileIfNotExists(booksFile);
        YamlConfiguration root = YamlConfiguration.loadConfiguration(booksFile);
        try {
            Set<String> keys = root.getKeys(false);
            for (String key : keys) {
                bookTemplates.put(Short.parseShort(key), new Book(root.getConfigurationSection(key)));
            }
            GUtils.log(bookTemplates.size() + " book templates");
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
