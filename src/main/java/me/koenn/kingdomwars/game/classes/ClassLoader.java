package me.koenn.kingdomwars.game.classes;

import me.koenn.core.cgive.CGiveAPI;
import me.koenn.core.data.JSONManager;
import me.koenn.core.misc.ItemHelper;
import me.koenn.kingdomwars.KingdomWars;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.json.simple.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public final class ClassLoader {

    private static final List<Class> classes = new ArrayList<>();
    private static boolean test;

    public static void load() {
        Plugin kingdomWars = KingdomWars.getInstance();
        for (File classFile : kingdomWars.getDataFolder().listFiles()) {
            if (classFile == null) {
                continue;
            }

            if (classFile.getName().toLowerCase().endsWith("class.json")) {
                JSONManager jsonManager = new JSONManager(kingdomWars, classFile.getName());
                String className = (String) jsonManager.getFromBody("name");
                JSONArray kits = (JSONArray) jsonManager.getFromBody("kits");
                List<Kit> classKits = new ArrayList<>();
                if (!test) {
                    for (Object kitObj : kits) {
                        JSONArray items = (JSONArray) kitObj;
                        List<ItemStack> kitItems = new ArrayList<>();
                        for (Object itemObj : items) {
                            String item = (String) itemObj;
                            if (item.startsWith("citem:")) {
                                String cItemName = item.split(":")[1].trim();
                                int cItemAmount = Integer.parseInt(item.split(":")[2]);
                                ItemStack stack = CGiveAPI.getCItem(cItemName).getItem();
                                stack.setAmount(cItemAmount);
                                kitItems.add(stack);
                            } else {
                                kitItems.add(ItemHelper.stringToItem(item));
                            }

                        }
                        ItemStack[] itemStacks = new ItemStack[kitItems.size()];
                        for (int i = 0; i < kitItems.size(); i++) {
                            itemStacks[i] = kitItems.get(i);
                        }
                        classKits.add(new Kit(itemStacks));
                    }
                }
                Kit[] kitArray = new Kit[classKits.size()];
                for (int i = 0; i < classKits.size(); i++) {
                    kitArray[i] = classKits.get(i);
                }
                classes.add(new Class(className, kitArray, test ? null : ItemHelper.stringToItem((String) jsonManager.getFromBody("icon")), (String) jsonManager.getFromBody("description")));
                kingdomWars.getLogger().info("Successfully loaded class file " + classFile.getName());
            }
        }
    }

    public static List<Class> getClasses() {
        return classes;
    }

    public static Class getClass(String name) {
        for (Class cl : classes) {
            if (cl.getName().equals(name)) {
                return cl;
            }
        }
        return null;
    }

    public static Class getRandomClass() {
        return classes.get(new Random().nextInt(classes.size()));
    }

    public static void setTest(boolean test) {
        ClassLoader.test = test;
    }
}
