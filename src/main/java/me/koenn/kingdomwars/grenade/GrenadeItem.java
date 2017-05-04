package me.koenn.kingdomwars.grenade;

import me.koenn.core.cgive.CItem;
import me.koenn.core.misc.ItemHelper;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public class GrenadeItem implements CItem {

    private final String type;

    public GrenadeItem(String type) {
        this.type = type;
    }

    @Override
    public ItemStack getItem() {
        return ItemHelper.makeItemStack(Material.SNOW_BALL, 1, (short) 0, ChatColor.WHITE + this.type + " Grenade", new ArrayList<>());
    }

    @Override
    public String getName() {
        return this.type.toLowerCase().replace(" ", "_");
    }
}
