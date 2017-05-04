package me.koenn.kingdomwars.game.classes;

import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public class Class {

    private final String name;
    private final Kit[] kits;
    private final ItemStack icon;
    private final String description;

    protected Class(String name, Kit[] kits, ItemStack icon, String description) {
        this.name = name;
        this.kits = kits;
        this.icon = icon;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public Kit[] getKits() {
        return kits;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Class{Name: " + name + ", Kits: " + Arrays.toString(kits) + "}";
    }
}
