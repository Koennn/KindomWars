package me.koenn.kingdomwars.grenade;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.jnbt.CompoundTag;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public final class GrenadeHelper {

    public static CompoundTag getGrenade(ItemStack item) {
        String name = ChatColor.stripColor(item.getItemMeta().getDisplayName()).toLowerCase().replace(" grenade", "");
        return GrenadeLoader.grenadeRegistry.get(name);
    }
}
