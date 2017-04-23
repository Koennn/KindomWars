package me.koenn.kindomwars.game.classes;

import me.koenn.kindomwars.KingdomWars;
import me.koenn.kindomwars.util.ItemHelper;
import me.koenn.kindomwars.util.JSONManager;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public final class ClassLoader {

    private static final List<Class> classes = new ArrayList<>();

    public static void loadClasses() {
        for (File classFile : KingdomWars.getInstance().getDataFolder().listFiles()) {
            if (classFile == null) {
                continue;
            }

            if (classFile.getName().toLowerCase().endsWith("class.json")) {
                Bukkit.getLogger().info("Loading class file " + classFile.getName());
                JSONManager jsonManager = new JSONManager(KingdomWars.getInstance(), classFile.getName());
                String className = (String) jsonManager.getFromBody("name");
                JSONArray kits = (JSONArray) jsonManager.getFromBody("kits");
                List<Kit> classKits = new ArrayList<>();
                for (Object kitObj : kits) {
                    JSONArray items = (JSONArray) kitObj;
                    List<ItemStack> kitItems = new ArrayList<>();
                    for (Object itemObj : items) {
                        kitItems.add(ItemHelper.stringToItem((String) itemObj));
                    }
                    ItemStack[] itemStacks = new ItemStack[kitItems.size()];
                    for (int i = 0; i < kitItems.size(); i++) {
                        itemStacks[i] = kitItems.get(i);
                    }
                    classKits.add(new Kit(itemStacks));
                }
                Kit[] kitArray = new Kit[classKits.size()];
                for (int i = 0; i < classKits.size(); i++) {
                    kitArray[i] = classKits.get(i);
                }
                classes.add(new Class(className, kitArray));
                Bukkit.getLogger().info("Successfully loaded class file " + classFile.getName());
            }
        }

        Bukkit.getLogger().info(Arrays.toString(classes.toArray()));
    }

    public static List<Class> getClasses() {
        return classes;
    }
}
