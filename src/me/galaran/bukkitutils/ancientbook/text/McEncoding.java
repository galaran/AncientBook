package me.galaran.bukkitutils.ancientbook.text;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class McEncoding {

    private static final String WRONG =  "ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖ×ØÙÚÛÜÝÞßàáâãäåæçèéêëìíîïðñòóôõö÷øùúûüýþÿ¸¨";
    private static final String RIGHT =  "АБВГДЕЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдежзийклмнопрстуфхцчшщъыьэюяёЁ";

    private static final Map<Character, Character> encMap = new HashMap<Character, Character>();
    static {
        for (int i = 0; i < WRONG.length(); i++) {
            encMap.put(WRONG.charAt(i), RIGHT.charAt(i));
        }
    }

    public static String fix(String str) {
        if (str == null) return null;
        if (str.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();

        for (char curChar : str.toCharArray()) {
            Character replace = encMap.get(curChar);
            sb.append(replace == null ? curChar : replace);
        }

        return sb.toString();
    }

    public static List<String> fixCollection(Collection<String> col) {
        if (col == null) return null;

        List<String> result = new ArrayList<String>(col.size());
        for (String element : col) {
            result.add(fix(element));
        }
        return result;
    }

    /**
     * @return passed ItemStack
     */
    public static ItemStack fixItem(ItemStack stack) {
        if (stack != null && stack.hasItemMeta()) {
            stack.setItemMeta(fixItemMeta(stack.getItemMeta()));
        }
        return stack;
    }

    /**
     * @return passed ItemMeta
     */
    public static ItemMeta fixItemMeta(ItemMeta meta) {
        if (meta == null) return null;

        meta.setDisplayName(fix(meta.getDisplayName()));
        meta.setLore(fixCollection(meta.getLore()));

        if (meta instanceof BookMeta) {
            BookMeta book = (BookMeta) meta;
            book.setAuthor(fix(book.getAuthor()));
            book.setTitle(fix(book.getTitle()));
            book.setPages(fixCollection(book.getPages()));
        }
        return meta;
    }
}
