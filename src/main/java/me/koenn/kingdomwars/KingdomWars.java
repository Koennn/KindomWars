package me.koenn.kingdomwars;

import me.koenn.core.cgive.CGiveAPI;
import me.koenn.core.cgive.CItem;
import me.koenn.core.command.CommandAPI;
import me.koenn.core.pluginmanager.PluginManager;
import me.koenn.kingdomwars.commands.ForceStartCommand;
import me.koenn.kingdomwars.commands.SelectClassCommand;
import me.koenn.kingdomwars.commands.TestParticleCommand;
import me.koenn.kingdomwars.deployables.DeployableLoader;
import me.koenn.kingdomwars.game.Game;
import me.koenn.kingdomwars.game.Map;
import me.koenn.kingdomwars.game.MapLoader;
import me.koenn.kingdomwars.game.classes.ClassLoader;
import me.koenn.kingdomwars.grenade.GrenadeHelper;
import me.koenn.kingdomwars.grenade.GrenadeItem;
import me.koenn.kingdomwars.grenade.GrenadeListener;
import me.koenn.kingdomwars.listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public final class KingdomWars extends JavaPlugin {

    private static KingdomWars instance;

    public static KingdomWars getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        this.getLogger().info("All credits for this plugin go to Koenn");
        instance = this;

        PluginManager.registerPlugin(this);

        Bukkit.getPluginManager().registerEvents(new DamageListener(), this);
        Bukkit.getPluginManager().registerEvents(new SignListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerMoveListener(), this);
        Bukkit.getPluginManager().registerEvents(new BlockListener(), this);
        Bukkit.getPluginManager().registerEvents(new MapCreator(), this);
        Bukkit.getPluginManager().registerEvents(new EmeraldSpeedListener(), this);
        Bukkit.getPluginManager().registerEvents(new GrenadeListener(), this);

        ClassLoader.loadClasses();

        CommandAPI.registerCommand(new ForceStartCommand(), this);
        CommandAPI.registerCommand(new SelectClassCommand(), this);
        CommandAPI.registerCommand(new TestParticleCommand(), this);

        this.reloadMaps();

        DeployableLoader.load();

        CGiveAPI.registerCItem(new CItem() {
            @Override
            public ItemStack getItem() {
                ItemStack item = new ItemStack(Material.WOOD_HOE);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.WHITE + "Map Creation Tool");
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                ArrayList<String> lore = new ArrayList<>();
                lore.add(ChatColor.GOLD + "Mode: Spawn");
                meta.setLore(lore);
                item.setItemMeta(meta);
                return item;
            }

            @Override
            public String getName() {
                return "mapstaff";
            }
        }, this);
        CGiveAPI.registerCItem(new GrenadeItem(GrenadeHelper.Type.FRAG.toString()), this);

        this.getLogger().info("Load successful!");
    }

    public void reloadMaps() {
        Map.maps.clear();
        for (File file : this.getDataFolder().listFiles()) {
            if (file != null && file.getName().endsWith("map.json")) {
                MapLoader.loadMap(file.getName());
            }
        }
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        Game.gameRegistry.forEach(Game::cancel);
    }
}
