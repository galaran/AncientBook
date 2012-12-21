package me.galaran.bukkitutils.ancientbook;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemUtils {

    public static boolean contains(Inventory inv, ItemStack stack) {
        return contains(inv, stack.getType(), stack.getDurability(), stack.getAmount());
    }

    public static boolean contains(Inventory inv, Material type, short data, int minAmount) {
        int amount = 0;
        for (ItemStack curInvStack : inv.getContents()) {
            if (curInvStack == null) continue;
            if (curInvStack.getType() == type && curInvStack.getDurability() == data) {
                amount += curInvStack.getAmount();
            }
        }

        return amount >= minAmount;
    }

    public static String stackToString(ItemStack stack) {
        Material type = stack.getType();

        StringBuilder sb = new StringBuilder();
        if (stack.getAmount() > 1) {
            sb.append(ChatColor.GREEN);
            sb.append(stack.getAmount());
            sb.append("x ");
        }
        sb.append(ChatColor.DARK_PURPLE);
        sb.append(type.toString().toLowerCase());
        sb.append(ChatColor.GRAY);

        if (type.getMaxDurability() > 0) {
            sb.append('(');
            sb.append(type.getMaxDurability() - stack.getDurability());
            sb.append('/');
            sb.append(type.getMaxDurability());
            sb.append(')');
        } else {
            sb.append(':');
            sb.append(stack.getData().getData());
        }
        sb.append(' ');

        Map<Enchantment, Integer> enchMap = stack.getEnchantments();
        if (!enchMap.isEmpty()) {
            sb.append(ChatColor.GOLD);
            for (Map.Entry<Enchantment, Integer> curEnch : enchMap.entrySet()) {
                sb.append(curEnch.getKey().getName());
                sb.append('-');
                sb.append(curEnch.getValue());
                sb.append(' ');
            }
        }

        return sb.toString();
    }

    /**
     * Gives stacks copy to player
     * @return all stacks fit
     */
    @SuppressWarnings("deprecation")
    public static boolean giveStacks(Player player, boolean dropIfNotFit, Iterable<ItemStack> stacks) {
        List<ItemStack> stacksCopy = new ArrayList<ItemStack>();
        for (ItemStack curStack : stacks) {
            stacksCopy.add(curStack.clone());
        }

        Inventory inv = player.getInventory();
        HashMap<Integer, ItemStack> ungiven = inv.addItem(stacksCopy.toArray(new ItemStack[stacksCopy.size()]));
        player.updateInventory();

        if (ungiven != null && !ungiven.isEmpty()) {
            if (dropIfNotFit) {
                Location dropLoc = player.getEyeLocation();
                for (ItemStack ungivenStack : ungiven.values()) {
                    dropLoc.getWorld().dropItem(dropLoc, ungivenStack);
                }
            }
            return false;
        }
        return true;
    }
    
    public static void addLore(ItemStack stack, String loreLine) {
        if (stack == null || stack.getType() == Material.AIR) return;
        
        ItemMeta meta = stack.getItemMeta();
        List<String> newLore = new ArrayList<String>();
        if (meta.hasLore()) {
            newLore.addAll(meta.getLore());
        }
        newLore.add(loreLine);
        meta.setLore(newLore);
        stack.setItemMeta(meta);
    }
}
