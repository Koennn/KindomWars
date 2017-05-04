package me.koenn.kingdomwars.grenade;

import me.koenn.core.misc.FancyString;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public final class GrenadeHelper {

    public static BaseGrenade getGrenade(ItemStack item) {
        String name = ChatColor.stripColor(item.getItemMeta().getDisplayName()).toLowerCase().replace(" grenade", "");
        Bukkit.getLogger().info(name);
        return BaseGrenade.grenadeRegistry.get(name);
    }

    public enum Type {
        FRAG;

        @Override
        public String toString() {
            return new FancyString(this.name()).toString();
        }
    }
}
