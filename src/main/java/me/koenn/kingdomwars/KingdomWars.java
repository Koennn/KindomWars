package me.koenn.kingdomwars;

import me.koenn.core.KoennCore;
import me.koenn.core.cgive.CGiveAPI;
import me.koenn.core.command.Command;
import me.koenn.core.command.CommandAPI;
import me.koenn.core.pluginmanager.PluginManager;
import me.koenn.kingdomwars.characters.CharacterLoader;
import me.koenn.kingdomwars.commands.*;
import me.koenn.kingdomwars.deployables_OLD.DeployableLoader;
import me.koenn.kingdomwars.discord.ChannelManager;
import me.koenn.kingdomwars.discord.DiscordBot;
import me.koenn.kingdomwars.game.Game;
import me.koenn.kingdomwars.game.GameCreator;
import me.koenn.kingdomwars.game.MapLoader;
import me.koenn.kingdomwars.game.map.Map;
import me.koenn.kingdomwars.listeners.*;
import me.koenn.kingdomwars.logger.EventLogger;
import me.koenn.kingdomwars.party.PartyCommand;
import me.koenn.kingdomwars.party.PartyCreateCommand;
import me.koenn.kingdomwars.party.PartyInviteCommand;
import me.koenn.kingdomwars.util.References;
import me.koenn.kingdomwars.util.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;

import java.io.File;
import java.util.Arrays;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential Written by Koen Willemse, April 2017
 */
public final class KingdomWars extends JavaPlugin implements Listener {

    public static Command command;
    private static KingdomWars instance;
    private static EventLogger eventLogger;
    private static DiscordBot discord;
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
            Bukkit.getPluginManager().registerEvents(new EmeraldSpeedListener(), this);
            Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);

            this.getLogger().info("Registering commands...");
            command = new KingdomWarsCommand();
            CommandAPI.registerCommand(command, this);
            CommandAPI.registerSubCommand(command, new HelpCommand(), this);
            CommandAPI.registerSubCommand(command, new CharacterCommand(), this);
            CommandAPI.registerSubCommand(command, new EditGameCommand(), this);
            CommandAPI.registerSubCommand(command, new MapCommand(), this);
            CommandAPI.registerSubCommand(command, new LinkCommand(), this);

            Command party = new PartyCommand();
            CommandAPI.registerCommand(party, this);
            CommandAPI.registerSubCommand(party, new PartyCreateCommand(), this);
            CommandAPI.registerSubCommand(party, new PartyInviteCommand(), this);

            this.getLogger().info("Registering custom items...");
            CGiveAPI.registerCItem(References.MAPSTAFF, this);

            this.getLogger().info("Loading maps...");
            this.reloadMaps();

            this.getLogger().info("Loading deployables...");
            DeployableLoader.load();

            this.getLogger().info("Loading characters...");
            CharacterLoader.load();

            this.getLogger().info("Connecting to Discord...");
            discord = new DiscordBot();
            ChannelManager.loadChannels();

            this.getLogger().info("Finalizing...");
            Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
            Arrays.stream(Team.values())
                    .filter(team -> scoreboard.getTeam(team.name()) == null)
                    .forEach(team ->
                            scoreboard.registerNewTeam(team.name()).setPrefix((team == Team.RED ? ChatColor.RED : ChatColor.BLUE).toString())
                    );

            this.getLogger().info("Loading signs & creating games...");
            //NEEDS TO BE THE LAST THING LOADED IN, CREATES GAME INSTANCES.
            GameCreator.instance.loadSigns();
        } catch (Exception ex) {
            this.getLogger().severe("An error occurred while initializing: " + ex);
            ex.printStackTrace();
            this.getLogger().severe("Disabling plugin...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        enabled = true;
        this.getLogger().info("+=========== Load successful! ===========+");
    }

    public void reloadMaps() {
        Map.maps.clear();

        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdir();
        }

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

            discord.shutdown();
        } catch (Exception ex) {
            if (enabled) {
                this.getLogger().severe("An error occurred while disabling: " + ex);
                ex.printStackTrace();
            } else {
                this.getLogger().info("+=========== Disabled with errors! ===========+");
            }
            return;
        } finally {
            if (eventLogger != null) {
                this.getLogger().info("Disabling event logger...");
                eventLogger.disable();
            }
        }

        this.getLogger().info("+=========== Successfully disabled! ===========+");
    }
}
