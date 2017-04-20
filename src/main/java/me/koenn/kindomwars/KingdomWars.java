package me.koenn.kindomwars;

import me.koenn.kindomwars.game.GameCreator;
import me.koenn.kindomwars.listeners.DamageListener;
import me.koenn.kindomwars.listeners.SignListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

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
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
    }
}
