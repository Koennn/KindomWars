package me.koenn.kindomwars.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public final class ItemHelper {

    public static boolean areSimilar(ItemStack item1, ItemStack item2) {
        return item1.getType().equals(item2.getType()) &&
                item1.getAmount() == item2.getAmount() &&
                item1.getDurability() == item2.getDurability() &&
                areEqual(item1.getEnchantments(), item2.getEnchantments());
    }

    public static boolean areEqual(Map<Enchantment, Integer> enchants1, Map<Enchantment, Integer> enchants2) {
        if (enchants1.isEmpty() || enchants2.isEmpty()) {
            return enchants1.isEmpty() && enchants2.isEmpty();
        }
        for (Enchantment enchantment : enchants1.keySet()) {
            if (!enchants2.containsKey(enchantment)) {
                return false;
            } else {
                if (!enchants1.get(enchantment).equals(enchants2.get(enchantment))) {
                    return false;
                }
            }
        }
        return true;
    }

    public static ItemStack makeItemStack(Material type, int amount, short meta, String displayName, List<String> lore) {
        ItemStack itemStack = new ItemStack(type, amount, meta);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RESET + displayName);
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static String itemToString(ItemStack i) {
        if (i == null || i.getType() == null || i.getType() == Material.AIR) {
            return null;
        }
        String type = i.getType().toString();
        String amount = String.valueOf(i.getAmount());
        String data = String.valueOf(i.getDurability());
        String enchants = "enchants:";
        if (!i.getEnchantments().isEmpty()) {
            for (Enchantment ench : i.getEnchantments().keySet()) {
                enchants = enchants + ench.getName() + "-" + String.valueOf(i.getEnchantments().get(ench)) + "%";
            }
        } else {
            enchants = "";
        }
        String lore = "lore:";
        if (i.getItemMeta().hasLore()) {
            for (String l : i.getItemMeta().getLore()) {
                lore = lore + l.replace(" ", "_") + "%";
            }
        } else {
            lore = "";
        }
        String name = "name:";
        if (i.getItemMeta().hasDisplayName()) {
            name = name + i.getItemMeta().getDisplayName().replace(" ", "_");
        } else {
            name = "";
        }
        return (type + " " + amount + " " + data + " " + enchants + " " + lore + " " + name).replace("§", "&").trim();
    }

    public static ItemStack stringToItem(String s) {
        String[] components = s.split(" ");
        Material material = Material.valueOf(components[0].toUpperCase());
        int amount = Integer.parseInt(components[1]);
        short data = Short.parseShort(components[2]);
        ItemStack item = new ItemStack(material, amount, data);
        ItemMeta meta = item.getItemMeta();
        HashMap<Enchantment, Integer> enchantments = getEnchantments(components);
        if (enchantments != null) {
            for (Enchantment e : enchantments.keySet()) {
                meta.addEnchant(e, enchantments.get(e), true);
            }
        }
        List<String> lore = getLore(components);
        if (lore != null) {
            meta.setLore(lore);
        }
        String name = getDisplayName(components);
        if (name != null) {
            meta.setDisplayName(name);
        }
        item.setItemMeta(meta);
        return item;
    }

    private static HashMap<Enchantment, Integer> getEnchantments(String[] components) {
        HashMap<Enchantment, Integer> enchantments = new HashMap<>();
        for (int i = 3; i < components.length; i++) {
            if (components[i].contains("enchants:")) {
                components[i] = components[i].replace("enchants:", "");
                String[] enchants = components[i].split("%");
                for (String e : enchants) {
                    if (e.equalsIgnoreCase("")) {
                        continue;
                    }
                    String[] enchantment = e.split("-");
                    int level = Integer.parseInt(enchantment[1]);
                    enchantments.put(Enchantment.getByName(enchantment[0].toUpperCase()), level);
                }
            }
        }
        if (enchantments.isEmpty()) {
            return null;
        }
        return enchantments;
    }

    private static List<String> getLore(String[] components) {
        List<String> lore = new ArrayList<>();
        for (int i = 3; i < components.length; i++) {
            if (components[i].contains("lore:")) {
                components[i] = components[i].replace("lore:", "");
                String[] lores = components[i].split("%");
                for (String l : lores) {
                    if (l.equalsIgnoreCase("")) {
                        continue;
                    }
                    lore.add(ChatColor.translateAlternateColorCodes('&', l.replace("_", " ")));
                }
            }
        }
        if (lore.isEmpty()) {
            return null;
        }
        return lore;
    }

    private static String getDisplayName(String[] components) {
        String name = null;
        for (int i = 3; i < components.length; i++) {
            if (components[i].contains("name:")) {
                components[i] = components[i].replace("name:", "");
                name = components[i].replace("_", " ");
            }
        }
        return ChatColor.translateAlternateColorCodes('&', name);
    }


}
