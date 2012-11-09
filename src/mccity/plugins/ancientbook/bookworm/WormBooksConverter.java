package mccity.plugins.ancientbook.bookworm;

import me.galaran.bukkitutils.ancientbook.Book;
import me.galaran.bukkitutils.ancientbook.GUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class WormBooksConverter implements Listener {

    private final File dataFolder;
    private final Map<Short, WormBook> wormBooks = new HashMap<Short, WormBook>();

    public WormBooksConverter(File dataFolder) {
        this.dataFolder = dataFolder;
    }

    public boolean reloadBooks() {
        wormBooks.clear();

        File wormBooksDir = new File(dataFolder, "BookWorm");
        if (!wormBooksDir.isDirectory()) {
            GUtils.log(Level.WARNING, "BookWorm dir not exists");
            return false;
        }

        File[] files = wormBooksDir.listFiles();
        if (files == null) {
            GUtils.log(Level.WARNING, "BookWorm dir is empty");
            return true;
        }

        try {
            for (File curBookFile : files) {
                if (!curBookFile.isFile()) continue;

                WormBook wormBook = new WormBook(curBookFile);
                wormBooks.put(wormBook.getId(), wormBook);
            }
        } catch (IOException ex) {
            GUtils.log(Level.SEVERE, "Error loading worm book(s)");
            ex.printStackTrace();
            return false;
        }

        GUtils.log(wormBooks.size() + " Worm Books loaded");
        return true;
    }

    private Book createBook(short wormBookId) {
        WormBook wormBook = wormBooks.get(wormBookId);
        if (wormBook == null) return null;

        return new Book(wormBook.getTitle(), wormBook.getAuthor(), wormBook.getPages());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();

        ItemStack handStack = player.getItemInHand();
        if (handStack == null || handStack.getType() != Material.BOOK) return;

        short bookData = handStack.getDurability();
        if (bookData == 0) return; // not a wormbook

        Book resultBook = createBook(bookData);
        if (resultBook != null) {
            player.setItemInHand(resultBook.toSignedBook());
        }
    }
}
