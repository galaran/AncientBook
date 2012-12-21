package mccity.plugins.ancientbook.commands;

import com.google.common.collect.ImmutableList;
import com.sk89q.minecraft.util.commands.ancientbook.Command;
import com.sk89q.minecraft.util.commands.ancientbook.CommandContext;
import com.sk89q.minecraft.util.commands.ancientbook.CommandPermissions;
import mccity.plugins.ancientbook.AncientBookPlugin;
import me.galaran.bukkitutils.ancientbook.ItemUtils;
import me.galaran.bukkitutils.ancientbook.text.McEncoding;
import me.galaran.bukkitutils.ancientbook.text.Messaging;
import me.galaran.bukkitutils.ancientbook.text.StringUtils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
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
            Messaging.send(sender, "reload.ok");
        } else {
            Messaging.send(sender, "reload.error");
        }
    }

    @SuppressWarnings("deprecation")
    @Command(aliases = { "unsign" }, desc = "Unsign written book in the hand", min = 0, max = 0)
    @CommandPermissions("ancientbook.command")
    public void unsignHandBook(CommandContext args, CommandSender sender) {
        if (!Messaging.isPlayer(sender)) return;
        Player player = (Player) sender;

        ItemStack handStack = player.getItemInHand();
        if (handStack != null && handStack.getType() == Material.WRITTEN_BOOK) {
            handStack.setType(Material.BOOK_AND_QUILL);
            player.updateInventory();
            Messaging.send(player, "unsign.success");
        } else {
            Messaging.send(player, "unsign.not-written-book");
        }
    }

    @Command(aliases = { "save", "add" }, desc = "Add or replace book template with specified data",
            usage = "[-d book_data] [-a \"author\"] [-t \"title\"]", min = 0, max = -1)
    @CommandPermissions("ancientbook.command")
    public void addBook(CommandContext args, CommandSender sender) {
        if (!Messaging.isPlayer(sender)) return;
        Player player = (Player) sender;

        ItemStack handStack = player.getItemInHand();
        if (handStack == null || !handStack.hasItemMeta()) {
            Messaging.send(sender, "add.not-a-book");
            return;
        }
        ItemMeta meta = handStack.getItemMeta();
        if (!(meta instanceof BookMeta)) {
            Messaging.send(sender, "add.not-a-book");
            return;
        }

        BookMeta book = (BookMeta) meta;
        short data = handStack.getDurability();

        try {
            Map<String, String> params = StringUtils.parseParameters(args.getSlice(1));
            if (params.containsKey("d")) {
                data = Short.parseShort(params.get("d"));
            }
            if (params.containsKey("a")) {
                book.setAuthor(StringUtils.colorizeAmps(params.get("a")));
            }
            if (params.containsKey("t")) {
                book.setTitle(StringUtils.colorizeAmps(params.get("t")));
            }
        } catch (IllegalArgumentException ex) {
            Messaging.send(sender, "add.illegal-params");
            return;
        }

        if (!book.hasAuthor()) {
            Messaging.send(sender, "add.no-author");
            return;
        } else if (!book.hasTitle()) {
            Messaging.send(sender, "add.no-title");
            return;
        }

        McEncoding.fixItemMeta(book);
        plugin.getBooksManager().addBook(data, book, sender);
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
        Map<Short, BookMeta> mapping = plugin.getBooksManager().getMapping();
        Messaging.send(sender, "list.header", mapping.size());
        for (Map.Entry<Short, BookMeta> entry : mapping.entrySet()) {
            String title = entry.getValue().getTitle();
            int maxLength = ChatPaginator.GUARANTEED_NO_WRAP_CHAT_PAGE_WIDTH - 10;
            String shortedTitle = title.length() > maxLength ? title.substring(0, maxLength) + "..." : title;
            Messaging.send(sender, "list.entry", entry.getKey(), shortedTitle);
        }
    }

    @Command(aliases = { "give" }, desc = "Give signed book to player",
            usage = "<book_data> <player> [is_unsigned]", min = 2, max = 3)
    @CommandPermissions("ancientbook.command")
    public void giveBook(CommandContext args, CommandSender sender) {
        short data = (short) args.getInteger(0);
        BookMeta bookMeta = plugin.getBooksManager().getBook(data);
        if (bookMeta == null) {
            Messaging.send(sender, "book.no-such", data);
            return;
        }

        Player target = Messaging.getPlayer(args.getString(1), sender);
        if (target == null) return;

        boolean isUnsigned = false;
        if (args.argsLength() == 3) {
            isUnsigned = Boolean.parseBoolean(args.getString(2));
        }
        
        ItemStack bookStack = new ItemStack(isUnsigned ? Material.BOOK_AND_QUILL : Material.WRITTEN_BOOK);
        bookStack.setDurability(data);
        bookStack.setItemMeta(bookMeta.clone());
        
        if (!ItemUtils.giveStacks(target, true, ImmutableList.of(bookStack))) {
            Messaging.send(target, "give.not-fit", bookMeta.getTitle());
        }
    }
}
