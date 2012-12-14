package mccity.plugins.ancientbook;

import com.sk89q.minecraft.util.commands.ancientbook.*;
import mccity.plugins.ancientbook.commands.AncientBookCommands;
import mccity.plugins.ancientbook.commands.RootCommand;
import me.galaran.bukkitutils.ancientbook.text.Messaging;
import me.galaran.bukkitutils.ancientbook.text.TranslationLang;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class AncientBookPlugin extends JavaPlugin {

    private TranslationLang lang;

    private CommandsManager<CommandSender> commands;
    private BooksManager booksManager;

    @Override
    public void onEnable() {
        lang = new TranslationLang(this, "english");
        Messaging.init(getLogger(), ChatColor.GRAY + "[AncientBook] ", lang);

        booksManager = new BooksManager(getDataFolder());
        reloadSettings();

        commands = new CommandsManager<CommandSender>() {
            @Override
            public boolean hasPermission(CommandSender sender, String perm) {
                return sender.hasPermission(perm);
            }
        };
        commands.setInjector(new SimpleInjector(this));
        commands.register(RootCommand.class);
        commands.register(AncientBookCommands.class);

        Messaging.log("AncientBook enabled");
    }

    public boolean reloadSettings() {
        File configFile = new File(getDataFolder(), "config.yml");
        saveDefaultConfig();
        Settings.load(configFile);
        lang.reload(Settings.lang);
        return booksManager.reloadBooks();
    }

    public BooksManager getBooksManager() {
        return booksManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            commands.execute(command.getName(), args, sender, sender);
        } catch (CommandPermissionsException e) {
            Messaging.send(sender, "utils.no-perm");
        } catch (MissingNestedCommandException e) {
            Messaging.sendRaw(sender, ChatColor.RED + e.getUsage());
        } catch (CommandUsageException e) {
            Messaging.sendRaw(sender, ChatColor.RED + e.getMessage());
            Messaging.sendRaw(sender, ChatColor.RED + e.getUsage());
        } catch (WrappedCommandException e) {
            if (e.getCause() instanceof NumberFormatException) {
                Messaging.send(sender, "command.not-a-number");
            } else {
                Messaging.send(sender, "command.error-console");
                e.printStackTrace();
            }
        } catch (CommandException e) {
            Messaging.sendRaw(sender, ChatColor.RED + e.getMessage());
        }
        return true;
    }
}
