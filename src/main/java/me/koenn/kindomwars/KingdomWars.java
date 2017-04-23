package me.koenn.kindomwars;

import me.koenn.kindomwars.commands.ForceStartCommand;
import me.koenn.kindomwars.commands.MapStaffCommand;
import me.koenn.kindomwars.game.Game;
import me.koenn.kindomwars.game.MapLoader;
import me.koenn.kindomwars.game.classes.ClassLoader;
import me.koenn.kindomwars.listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

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

        Bukkit.getPluginManager().registerEvents(new DamageListener(), this);
        Bukkit.getPluginManager().registerEvents(new SignListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerMoveListener(), this);
        Bukkit.getPluginManager().registerEvents(new BlockListener(), this);
        Bukkit.getPluginManager().registerEvents(new MapCreator(), this);

        ClassLoader.loadClasses();

        this.getCommand("forcestart").setExecutor(new ForceStartCommand());
        this.getCommand("mapstaff").setExecutor(new MapStaffCommand());

        for (File file : this.getDataFolder().listFiles()) {
            if (file != null && file.getName().endsWith("map.json")) {
                MapLoader.loadMap(file.getName());
            }
        }

        this.getLogger().info("Load successful!");
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        Game.gameRegistry.forEach(Game::cancel);
    }
}
