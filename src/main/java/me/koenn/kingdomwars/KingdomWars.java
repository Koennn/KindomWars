package me.koenn.kingdomwars;

import me.koenn.core.KoennCore;
import me.koenn.core.cgive.CGiveAPI;
import me.koenn.core.command.Command;
import me.koenn.core.command.CommandAPI;
import me.koenn.core.pluginmanager.PluginManager;
import me.koenn.kingdomwars.commands.EditGameCommand;
import me.koenn.kingdomwars.commands.KingdomWarsCommand;
import me.koenn.kingdomwars.commands.SelectClassCommand;
import me.koenn.kingdomwars.commands.StatsCommand;
import me.koenn.kingdomwars.deployables.DeployableLoader;
import me.koenn.kingdomwars.game.Game;
import me.koenn.kingdomwars.game.GameCreator;
import me.koenn.kingdomwars.game.Map;
import me.koenn.kingdomwars.game.MapLoader;
import me.koenn.kingdomwars.game.classes.ClassLoader;
import me.koenn.kingdomwars.grenade.GrenadeListener;
import me.koenn.kingdomwars.grenade.GrenadeLoader;
import me.koenn.kingdomwars.listeners.*;
import me.koenn.kingdomwars.logger.EventLogger;
import me.koenn.kingdomwars.mapcreator.MapCreator;
import me.koenn.kingdomwars.util.References;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public final class KingdomWars extends JavaPlugin implements Listener {

    private static KingdomWars instance;
    private static EventLogger eventLogger;
    private static boolean enabled = false;

    public static KingdomWars getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        this.getLogger().info(String.format("+=========== Starting KingdomWars v%s ===========+", this.getDescription().getVersion()));
        this.getLogger().info("All credits for this plugin go to Koenn");
        instance = this;

        try {
            KoennCore.requireVersion(1.7, this);

            this.getLogger().info("Setting up event logger...");
            eventLogger = new EventLogger();

            this.getLogger().info("Registering plugin to KoennCore...");
            PluginManager.registerPlugin(this);

            this.getLogger().info("Registering Bukkit event listeners...");
            Bukkit.getPluginManager().registerEvents(new DamageListener(), this);
            Bukkit.getPluginManager().registerEvents(new SignListener(), this);
            Bukkit.getPluginManager().registerEvents(new PlayerMoveListener(), this);
            Bukkit.getPluginManager().registerEvents(new BlockListener(), this);
            Bukkit.getPluginManager().registerEvents(new MapCreator(), this);
            Bukkit.getPluginManager().registerEvents(new EmeraldSpeedListener(), this);
            Bukkit.getPluginManager().registerEvents(new GrenadeListener(), this);

            this.getLogger().info("Registering commands...");
            Command mainCommand = new KingdomWarsCommand();
            CommandAPI.registerCommand(mainCommand, this);
            CommandAPI.registerSubCommand(mainCommand, new SelectClassCommand(), this);
            CommandAPI.registerSubCommand(mainCommand, new EditGameCommand(), this);
            CommandAPI.registerSubCommand(mainCommand, new StatsCommand(), this);

            this.getLogger().info("Registering custom items...");
            CGiveAPI.registerCItem(References.MAPSTAFF, this);

            this.getLogger().info("Loading maps...");
            this.reloadMaps();

            this.getLogger().info("Loading deployables...");
            DeployableLoader.load();

            this.getLogger().info("Loading grenades...");
            GrenadeLoader.load();

            this.getLogger().info("Loading classes...");
            ClassLoader.load();

            this.getLogger().info("Loading signs...");
            GameCreator.instance.loadSigns();
        } catch (Exception ex) {
            this.getLogger().severe("An error occurred while initializing: " + ex);
            this.getLogger().severe("Disabling plugin...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        enabled = true;
        this.getLogger().info("+=========== Load successful! ===========+");
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
        this.getLogger().info("+=========== Disabling KingdomWars ===========+");

        try {
            if (enabled) {
                this.getLogger().info("Saving signs...");
                GameCreator.instance.saveSigns();
            }

            this.getLogger().info("Cancelling and resetting active games...");
            Game.gameRegistry.forEach(Game::stop);
            Game.gameRegistry.clear();

            this.getLogger().info("Cancelling all repeating tasks...");
            Bukkit.getScheduler().cancelTasks(this);

            this.getLogger().info("Disabling event logger...");
            if (eventLogger != null) {
                eventLogger.disable();
            }
        } catch (Exception ex) {
            if (enabled) {
                this.getLogger().severe("An error occurred while disabling: " + ex);
            } else {
                this.getLogger().info("+=========== Disabled with errors! ===========+");
            }
            return;
        }

        this.getLogger().info("+=========== Successfully disabled! ===========+");
    }


}
