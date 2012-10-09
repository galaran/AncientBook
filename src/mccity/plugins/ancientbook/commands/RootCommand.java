package mccity.plugins.ancientbook.commands;

import com.sk89q.minecraft.util.commands.ancientbook.Command;
import com.sk89q.minecraft.util.commands.ancientbook.CommandContext;
import com.sk89q.minecraft.util.commands.ancientbook.CommandPermissions;
import com.sk89q.minecraft.util.commands.ancientbook.NestedCommand;
import mccity.plugins.ancientbook.AncientBookPlugin;
import org.bukkit.command.CommandSender;

public class RootCommand {

    private final AncientBookPlugin plugin;

    public RootCommand(AncientBookPlugin plugin) {
        this.plugin = plugin;
    }

    @Command(aliases = { "ancientbook", "abook" }, desc = "AncientBook main command")
    @CommandPermissions("ancientbook.command")
    @NestedCommand({ AncientBookCommands.class })
    public void ancientBook(CommandContext args, CommandSender sender) { }
}
