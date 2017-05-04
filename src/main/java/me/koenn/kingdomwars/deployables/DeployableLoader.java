package me.koenn.kingdomwars.deployables;

import me.koenn.core.cgive.CGiveAPI;
import me.koenn.core.cgive.CItem;
import me.koenn.core.misc.ItemHelper;
import me.koenn.core.registry.Registry;
import me.koenn.kingdomwars.KingdomWars;
import me.koenn.kingdomwars.util.NBTUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jnbt.CompoundTag;
import org.jnbt.NBTInputStream;
import org.jnbt.ShortTag;
import org.jnbt.StringTag;

import java.io.File;
import java.io.FileInputStream;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public final class DeployableLoader {

    public static final Registry<CompoundTag> deployables = new Registry<>(compoundTag -> NBTUtil.getChildTag(NBTUtil.getChildTag(compoundTag.getValue(), "properties", CompoundTag.class).getValue(), "name", StringTag.class).getValue());

    public static void load() {
        File deployablesFolder = new File(KingdomWars.getInstance().getDataFolder(), "deployables");
        for (File file : deployablesFolder.listFiles()) {
            if (file.getName().endsWith(".dpl")) {
                try {
                    NBTInputStream stream = new NBTInputStream(new FileInputStream(file));
                    CompoundTag tag = (CompoundTag) stream.readTag();

                    deployables.register(tag);
                    loadItem(NBTUtil.getChildTag(tag.getValue(), "properties", CompoundTag.class));
                } catch (Exception e) {
                    Bukkit.getLogger().severe("Unable to load \'" + file.getName() + "\'");
                }
            }
        }
    }

    public static Class<? extends Deployable> getDeployableClass(ItemStack item) {
        for (CompoundTag deployableTag : deployables.getRegisteredObjects()) {
            CompoundTag properties = NBTUtil.getChildTag(deployableTag.getValue(), "properties", CompoundTag.class);
            if (item.isSimilar(getDeployableItem(properties))) {
                String className = NBTUtil.getChildTag(properties.getValue(), "class", StringTag.class).getValue();
                try {
                    return (Class<? extends Deployable>) Class.forName("me.koenn.kingdomwars.deployables." + className);
                } catch (ClassNotFoundException e) {
                    throw new IllegalArgumentException("Couldn't find deployable class \'" + className + "\'");
                }
            }
        }
        return null;
    }

    private static void loadItem(final CompoundTag properties) {
        CGiveAPI.registerCItem(new CItem() {
            @Override
            public ItemStack getItem() {
                return getDeployableItem(properties);
            }

            @Override
            public String getName() {
                return NBTUtil.getChildTag(properties.getValue(), "name", StringTag.class).getValue();
            }
        }, KingdomWars.getInstance());
    }

    private static ItemStack getDeployableItem(CompoundTag properties) {
        CompoundTag itemTag = NBTUtil.getChildTag(properties.getValue(), "item", CompoundTag.class);
        short damage = NBTUtil.getChildTag(itemTag.getValue(), "damage", ShortTag.class).getValue();
        Material type = Material.valueOf(NBTUtil.getChildTag(itemTag.getValue(), "type", StringTag.class).getValue());
        String name = ChatColor.WHITE + NBTUtil.getChildTag(itemTag.getValue(), "name", StringTag.class).getValue();
        return ItemHelper.makeItemStack(type, 1, damage, name, null);
    }
}
