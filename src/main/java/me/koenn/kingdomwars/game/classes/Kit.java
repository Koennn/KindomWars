package me.koenn.kingdomwars.game.classes;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public class Kit {

    private final List<ItemStack> items;

    public Kit(ItemStack... items) {
        this.items = new ArrayList<>();
        Collections.addAll(this.items, items);
    }

    @Override
    public String toString() {
        return "Kit{" + Arrays.toString(items.toArray()) + "}";
    }

    public List<ItemStack> getItems() {
        return items;
    }
}
