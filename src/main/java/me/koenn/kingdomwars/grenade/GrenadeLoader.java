package me.koenn.kingdomwars.grenade;

import me.koenn.core.cgive.CGiveAPI;
import me.koenn.core.registry.Registry;
import me.koenn.kingdomwars.KingdomWars;
import me.koenn.kingdomwars.js.JSReader;
import me.koenn.kingdomwars.util.NBTUtil;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitScheduler;
import org.jnbt.CompoundTag;
import org.jnbt.NBTInputStream;
import org.jnbt.StringTag;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileInputStream;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, May 2017
 */
public class GrenadeLoader {

    public static final Registry<CompoundTag> grenadeRegistry = new Registry<>(compoundTag -> NBTUtil.getChildTag(compoundTag.getValue(), "name", StringTag.class).getValue());

    public static void registerGrenades() {
        File grenadeFolder = new File(KingdomWars.getInstance().getDataFolder(), "grenades");
        for (File file : grenadeFolder.listFiles()) {
            if (file.getName().endsWith(".grenade")) {
                try {
                    NBTInputStream stream = new NBTInputStream(new FileInputStream(file));
                    CompoundTag tag = (CompoundTag) stream.readTag();

                    grenadeRegistry.register(tag);
                    CGiveAPI.registerCItem(new GrenadeItem(NBTUtil.getChildTag(tag.getValue(), "name", StringTag.class).getValue()), KingdomWars.getInstance());
                } catch (Exception e) {
                    Bukkit.getLogger().severe("Unable to load \'" + file.getName() + "\'");
                }
            }
        }
    }

    public static void startGrenadeScript(BaseGrenade grenade) {
        ScriptEngine script = JSReader.read(NBTUtil.getChildTag(grenade.grenadeInfo.getValue(), "script", StringTag.class).getValue(),
                Entity.class, Location.class, World.class, Effect.class, BaseGrenade.class, BukkitScheduler.class, KingdomWars.class, Runnable.class
        );

        try {
            ((Invocable) script).invokeFunction("start", grenade.projectile, grenade);
        } catch (ScriptException | NoSuchMethodException e) {
            Bukkit.getLogger().severe("Error while running start function for \'" + grenade.type + "\' grenade: \'" + e.toString() + "\'");
            return;
        }

        grenade.task = Bukkit.getScheduler().scheduleSyncRepeatingTask(KingdomWars.getInstance(), () -> {
            try {
                ((Invocable) script).invokeFunction("tick");
            } catch (ScriptException | NoSuchMethodException e) {
                Bukkit.getLogger().severe("Error while running tick function for \'" + grenade.type + "\' grenade: \'" + e.toString() + "\'");
            }
        }, 0, 1);
    }
}
