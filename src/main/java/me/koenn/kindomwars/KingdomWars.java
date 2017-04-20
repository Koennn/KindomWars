package me.koenn.kindomwars;

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

    public static KingdomWars getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        this.getLogger().info("All credits for this plugin go to Koenn");
        instance = this;
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
    }
}
