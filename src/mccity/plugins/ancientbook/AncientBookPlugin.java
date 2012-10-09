package mccity.plugins.ancientbook;

import com.sk89q.minecraft.util.commands.ancientbook.*;
import mccity.plugins.ancientbook.commands.AncientBookCommands;
import mccity.plugins.ancientbook.commands.RootCommand;
import me.galaran.bukkitutils.ancientbook.GUtils;
import me.galaran.bukkitutils.ancientbook.Lang;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class AncientBookPlugin extends JavaPlugin {

    private CommandsManager<CommandSender> commands;
    private BooksManager booksManager;

    @Override
    public void onEnable() {
        GUtils.init(getLogger(), "AncientBook");

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

        GUtils.log("AncientBook enabled");
    }

    public boolean reloadSettings() {
        File configFile = new File(getDataFolder(), "config.yml");
        saveDefaultConfig();

        try {
            Settings.load(configFile);
            Lang.initLang(Settings.lang, this);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

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
            GUtils.sendMessage(sender, ChatColor.RED + "You don't have permission.");
        } catch (MissingNestedCommandException e) {
            GUtils.sendMessage(sender, ChatColor.RED + e.getUsage());
        } catch (CommandUsageException e) {
            GUtils.sendMessage(sender, ChatColor.RED + e.getMessage());
            GUtils.sendMessage(sender, ChatColor.RED + e.getUsage());
        } catch (WrappedCommandException e) {
            if (e.getCause() instanceof NumberFormatException) {
                GUtils.sendMessage(sender, ChatColor.RED + "Number expected, string received instead.");
            } else {
                GUtils.sendMessage(sender, ChatColor.RED + "An error has occurred. See console.");
                e.printStackTrace();
            }
        } catch (CommandException e) {
            GUtils.sendMessage(sender, ChatColor.RED + e.getMessage());
        }

        return true;
    }
}
