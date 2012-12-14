package me.galaran.bukkitutils.ancientbook.nms;

import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NmsUtils {

    /** Bukkit's World.dropItem() drops stack with null NBT tag, this method not */
    public static void dropStackSafe(ItemStack stack, Location loc) {
        net.minecraft.server.World nmsWorld = ((CraftWorld) loc.getWorld()).getHandle();
        net.minecraft.server.ItemStack nmsStack = CraftItemStack.createNMSItemStack(stack);

        net.minecraft.server.EntityItem entity =
                new net.minecraft.server.EntityItem(nmsWorld, loc.getX(), loc.getY(), loc.getZ(), nmsStack);
        entity.pickupDelay = 10;
        nmsWorld.addEntity(entity, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    /**
     * Gives stacks to player
     * @return all stacks fit
     */
    @SuppressWarnings("deprecation")
    public static boolean giveStacks(Player player, boolean dropNearbyIfNotFit, Iterable<ItemStack> stacks) {
        List<ItemStack> stacksCopy = new ArrayList<ItemStack>();
        for (ItemStack curStack : stacks) {
            stacksCopy.add(curStack.clone());
        }

        Inventory inv = player.getInventory();
        HashMap<Integer, ItemStack> ungiven = inv.addItem(stacksCopy.toArray(new ItemStack[stacksCopy.size()]));
        player.updateInventory();
        if (ungiven != null && !ungiven.isEmpty()) {
            if (dropNearbyIfNotFit) {
                for (ItemStack ungivenStack : ungiven.values()) {
                    dropStackSafe(ungivenStack, player.getEyeLocation());
                }
            }
            return false;
        }
        return true;
    }
}
