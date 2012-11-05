package mccity.plugins.ancientbook.commands;

import com.sk89q.minecraft.util.commands.ancientbook.Command;
import com.sk89q.minecraft.util.commands.ancientbook.CommandContext;
import com.sk89q.minecraft.util.commands.ancientbook.CommandPermissions;
import mccity.plugins.ancientbook.AncientBookPlugin;
import me.galaran.bukkitutils.ancientbook.*;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.ChatPaginator;

import java.util.Map;

public class AncientBookCommands {

    private final AncientBookPlugin plugin;

    public AncientBookCommands(AncientBookPlugin plugin) {
        this.plugin = plugin;
    }

    @Command(aliases = { "reload" }, desc = "Reload configuration, localization and books files", min = 0, max = 0)
    @CommandPermissions("ancientbook.command")
    public void reload(CommandContext args, CommandSender sender) {
        if (plugin.reloadSettings()) {
            GUtils.sendTranslated(sender, "reload.ok");
        } else {
            GUtils.sendTranslated(sender, "reload.error");
        }
    }

    @Command(aliases = { "unsign" }, desc = "Unsign written book in the hand", min = 0, max = 0)
    @CommandPermissions("ancientbook.command")
    public void unsignHandBook(CommandContext args, CommandSender sender) {
        if (!DoOrNotify.isPlayer(sender)) return;
        Player player = (Player) sender;

        ItemStack handStack = player.getItemInHand();
        if (handStack != null && handStack.getType() == Material.WRITTEN_BOOK) {
            Book book = new Book(handStack);
            player.setItemInHand(book.toUnsignedBook());
            GUtils.sendTranslated(player, "unsign.success", book.getTitle());
        } else {
            GUtils.sendTranslated(player, "unsign.not-written-book");
        }
    }

    @Command(aliases = { "add" }, desc = "Add or replace book template with specified data",
            usage = "[-d book_data] [-a \"author\"] [-t \"title\"]", min = 0, max = -1)
    @CommandPermissions("ancientbook.command")
    public void addBook(CommandContext args, CommandSender sender) {
        if (!DoOrNotify.isPlayer(sender)) return;
        Player player = (Player) sender;

        String author = null;
        String title = null;
        String[] pages;
        short data;
        ItemStack handStack = player.getItemInHand();
        if (handStack != null && handStack.getType() == Material.BOOK_AND_QUILL) {
            data = handStack.getDurability();
            Book book = new Book(handStack);
            pages = book.getPages();
        } else if (handStack != null && handStack.getType() == Material.WRITTEN_BOOK) {
            data = handStack.getDurability();
            Book book = new Book(handStack);
            pages = book.getPages();
            author = book.getAuthor();
            title = book.getTitle();
        } else {
            GUtils.sendTranslated(sender, "add.not-a-book");
            return;
        }

        try {
            Map<String, String> params = StringUtils.parseParameters(args.getSlice(1));
            if (params.containsKey("d")) {
                data = Short.parseShort(params.get("d"));
            }
            if (params.containsKey("a")) {
                author = StringUtils.colorizeAmps(params.get("a"));
            }
            if (params.containsKey("t")) {
                title = StringUtils.colorizeAmps(params.get("t"));
            }
        } catch (IllegalArgumentException ex) {
            GUtils.sendTranslated(sender, "add.illegal-params");
            return;
        }

        if (author == null) {
            GUtils.sendTranslated(sender, "add.no-author");
            return;
        } else if (title == null) {
            GUtils.sendTranslated(sender, "add.no-title");
            return;
        }

        plugin.getBooksManager().addBook(data, new Book(title, author, pages), sender);
    }

    @Command(aliases = { "remove", "rm" }, desc = "Remove book with given data",
            usage = "<book_data>", min = 1, max = 1)
    @CommandPermissions("ancientbook.command")
    public void removeBook(CommandContext args, CommandSender sender) {
        short bookData = Short.parseShort(args.getString(0));
        plugin.getBooksManager().removeBook(bookData, sender);
    }

    @Command(aliases = { "list" }, desc = "Book templates list", min = 0, max = 0)
    @CommandPermissions("ancientbook.command")
    public void listBooks(CommandContext args, CommandSender sender) {
        Map<Short, Book> mapping = plugin.getBooksManager().getMapping();
        GUtils.sendTranslated(sender, "list.header", mapping.size());
        for (Map.Entry<Short, Book> entry : mapping.entrySet()) {
            String title = entry.getValue().getTitle();
            int maxLength = ChatPaginator.GUARANTEED_NO_WRAP_CHAT_PAGE_WIDTH - 10;
            String shortedTitle = title.length() > maxLength ? title.substring(0, maxLength) + "..." : title;
            GUtils.sendTranslated(sender, "list.entry", entry.getKey(), shortedTitle);
        }
    }

    @Command(aliases = { "give" }, desc = "Give signed book to player",
            usage = "<book_data> <player> [is_unsigned]", min = 2, max = 3)
    @CommandPermissions("ancientbook.command")
    public void giveBook(CommandContext args, CommandSender sender) {
        short data = (short) args.getInteger(0);
        Book book = plugin.getBooksManager().getBook(data);
        if (book == null) {
            GUtils.sendTranslated(sender, "book.no-such", data);
            return;
        }

        Player target = DoOrNotify.getPlayer(args.getString(1), true, sender);
        if (target == null) return;

        boolean isUnsigned = false;
        if (args.argsLength() == 3) {
            isUnsigned = Boolean.parseBoolean(args.getString(2));
        }

        ItemStack bookStack = isUnsigned ? book.toUnsignedBook() : book.toSignedBook();
        bookStack.setDurability(data);
        if (CbUtils.giveStacksOrDrop(target, bookStack)) {
            GUtils.sendTranslated(target, "give.not-fit", book.getTitle());
        }
    }
}
