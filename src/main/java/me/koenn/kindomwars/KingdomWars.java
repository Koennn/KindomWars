package me.koenn.kindomwars;

import me.koenn.kindomwars.commands.ForceStartCommand;
import me.koenn.kindomwars.game.GameCreator;
import me.koenn.kindomwars.game.MapLoader;
import me.koenn.kindomwars.listeners.DamageListener;
import me.koenn.kindomwars.listeners.PlayerMoveListener;
import me.koenn.kindomwars.listeners.SignListener;
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
    private static GameCreator gameCreator;

    public static KingdomWars getInstance() {
        return instance;
    }

    public static GameCreator getGameCreator() {
        return gameCreator;
    }

    @Override
    public void onEnable() {
        this.getLogger().info("All credits for this plugin go to Koenn");
        instance = this;

        gameCreator = new GameCreator();

        Bukkit.getPluginManager().registerEvents(new DamageListener(), this);
        Bukkit.getPluginManager().registerEvents(new SignListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerMoveListener(), this);

        this.getCommand("forcestart").setExecutor(new ForceStartCommand());

        if (new File("testmap.json").exists()) {
            this.getLogger().info("Loading testmap.json");
            MapLoader.loadMap("testmap");
        }

        this.getLogger().info("Load successful!");
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
    }
}
