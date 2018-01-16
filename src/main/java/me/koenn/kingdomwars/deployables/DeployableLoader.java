package me.koenn.kingdomwars.deployables;

import de.slikey.effectlib.util.ParticleEffect;
import me.koenn.core.cgive.CGiveAPI;
import me.koenn.core.cgive.CItem;
import me.koenn.core.misc.ActionBar;
import me.koenn.core.misc.ItemHelper;
import me.koenn.core.misc.LoreHelper;
import me.koenn.core.misc.Timer;
import me.koenn.core.registry.Registry;
import me.koenn.kingdomwars.KingdomWars;
import me.koenn.kingdomwars.js.JSReader;
import me.koenn.kingdomwars.util.*;
import org.bukkit.*;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jnbt.CompoundTag;
import org.jnbt.NBTInputStream;
import org.jnbt.ShortTag;
import org.jnbt.StringTag;

import javax.script.ScriptEngine;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

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
        File deployablesFolder = new File(KingdomWars.getInstance().getDataFolder(), References.DEPLOYABLE_FOLDER);
        if (!deployablesFolder.exists()) {
            deployablesFolder.mkdir();
        }
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

    public static CompoundTag getDeployableInfo(ItemStack item) {
        for (CompoundTag deployableTag : deployables.getRegisteredObjects()) {
            CompoundTag properties = NBTUtil.getChildTag(deployableTag.getValue(), "properties", CompoundTag.class);
            if (item.isSimilar(getDeployableItem(properties))) {
                return deployableTag;
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
        List<String> lore = LoreHelper.makeLore(NBTUtil.getChildTag(itemTag.getValue(), "lore", StringTag.class).getValue().split("%n"));
        for (int i = 0; i < lore.size(); i++) {
            lore.set(i, ChatColor.YELLOW + lore.get(i));
        }
        return ItemHelper.makeItemStack(type, 1, damage, name, lore);
    }

    public static ScriptEngine loadDeployableScript(Deployable deployable) {
        return JSReader.read(NBTUtil.getChildTag(deployable.getDeployableInfo().getValue(), "script", StringTag.class).getValue(),
                Entity.class, Location.class, World.class, Effect.class, Deployable.class, Timer.class, KingdomWars.class, Runnable.class, List.class,
                Vector.class, PlayerHelper.class, Team.class, Messager.class, References.class, ActionBar.class, Damageable.class, ParticleRenderer.class,
                ParticleEffect.class
        );
    }
}
