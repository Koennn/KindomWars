package me.koenn.kingdomwars.game;

import me.koenn.core.misc.Timer;
import me.koenn.kingdomwars.KingdomWars;
import me.koenn.kingdomwars.deployables_OLD.Deployable;
import me.koenn.kingdomwars.discord.ChannelManager;
import me.koenn.kingdomwars.discord.DiscordBot;
import me.koenn.kingdomwars.discord.Lobby;
import me.koenn.kingdomwars.game.counters.PrepareCounter;
import me.koenn.kingdomwars.game.events.GameFinishEvent;
import me.koenn.kingdomwars.game.events.GameLoadEvent;
import me.koenn.kingdomwars.game.events.GameStartEvent;
import me.koenn.kingdomwars.game.map.ControlPoint;
import me.koenn.kingdomwars.game.map.Map;
import me.koenn.kingdomwars.game.map.MedKit;
import me.koenn.kingdomwars.game.map.SpeedPack;
import me.koenn.kingdomwars.grenade.Grenade;
import me.koenn.kingdomwars.logger.EventLogger;
import me.koenn.kingdomwars.logger.Message;
import me.koenn.kingdomwars.party.Party;
import me.koenn.kingdomwars.tracker.GameTracker;
import me.koenn.kingdomwars.traits.Trait;
import me.koenn.kingdomwars.util.*;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
//TODO: This file is a giant mess, and I should slap myself for writing it...
public class Game {

    public static final List<Game> gameRegistry = new ArrayList<>();
    public static final Random random = ThreadLocalRandom.current();

    public final TeamInfo[] teams = new TeamInfo[2];
    public final List<Trait> activeTraits = new ArrayList<>();
    public final List<Grenade> grenades = new ArrayList<>();

    private final List<UUID> players = new ArrayList<>();
    private final List<UUID>[] rawTeams = new List[2];
    private final List<Deployable> deployables = new ArrayList<>();
    private final Map map;
    private final GameTracker tracker;
    private final Lobby lobby;

    private boolean debug;
    private int[] points = new int[2];
    private GamePhase currentPhase;
    private Timer gameTimer;

    public Game(final Map map) {
        //Set the current phase.
        this.currentPhase = GamePhase.LOADING;

        //Set the map.
        this.map = map;

        //Register this game.
        gameRegistry.add(this);

        //Initialize the teams.
        for (int i = 0; i < 2; i++) {
            this.rawTeams[i] = new ArrayList<>();
        }

        ChannelManager.registerGame(this);
        this.lobby = ChannelManager.gameLobbies.get(this);

        this.tracker = new GameTracker(this);
    }

    public void load() {
        try {
            //Set current phase and log messages.
            this.currentPhase = GamePhase.STARTING;
            EventLogger.log(new Message("info", "Loading game " + Integer.toHexString(this.hashCode())));
            EventLogger.log(this, new Message(new String[]{"phase", "players"}, new String[]{this.currentPhase.name(), Arrays.toString(PlayerHelper.usernameArray(this.players))}));

            this.tracker.enable();
            this.map.load(this);

            //Shuffle and balance teams.
            Collections.shuffle(this.players, random);
            this.balanceTeams();

            //Log teams.
            EventLogger.log(this, new Message(new String[]{"teamBlue", "teamRed"}, new String[]{
                    Arrays.toString(PlayerHelper.usernameArray(this.teams[Team.RED.getIndex()].getPlayers())),
                    Arrays.toString(PlayerHelper.usernameArray(this.teams[Team.BLUE.getIndex()].getPlayers()))
            }));

            this.players.stream()
                    .filter(player -> Bukkit.getPlayer(player) != null)
                    .forEach(player -> {
                        Team team = PlayerHelper.getTeam(player);
                        DiscordBot.attemptMovePlayer(Bukkit.getPlayer(player), team == Team.RED ? this.lobby.getRedId() : this.lobby.getBlueId());
                    });

            //Send game starting message.
            Messager.gameMessage(this, References.GAME_ABOUT_TO_START);
            SoundSystem.gameSound(this, Sound.ENTITY_PLAYER_LEVELUP, 2.0F, 1.0F);

            //Load the players.
            GameHelper.loadPlayers(this);

            this.map.getSpawn(Team.RED).getWorld().setDifficulty(Difficulty.NORMAL);
            this.map.getSpawn(Team.RED).getWorld().setGameRuleValue("doMobSpawning", "false");

            //Start the game prepare timer.
            new PrepareCounter(this.debug, this).start(this::start);

            //Call the GameLoadEvent.
            Bukkit.getPluginManager().callEvent(new GameLoadEvent(this));
        } catch (Exception ex) {
            EventLogger.log(this, new Message("error", ex.toString()));
            ex.printStackTrace();
        }
    }

    private void start() {
        //Set the current phase and log messages.
        this.currentPhase = GamePhase.STARTED;
        EventLogger.log(this, new Message("phase", this.currentPhase.name()));

        //Start rendering the control points.
        this.map.startRendering();

        //Start the game update timer.
        this.gameTimer = new Timer(References.UPDATE_RATE, true, KingdomWars.getInstance());
        this.gameTimer.start(this::update);

        //Send the game started message.
        Messager.gameMessage(this, References.GAME_STARTED);

        //Play game started sound.
        SoundSystem.gameSound(this, Sound.ENTITY_WITHER_SPAWN, 1.0F, 1.0F);

        this.map.getMedkits().forEach(MedKit::enable);
        this.map.getSpeedPacks().forEach(SpeedPack::enable);

        this.players.stream()
                .filter(player -> Bukkit.getPlayer(player) != null)
                .forEach(player -> Bukkit.getPlayer(player).addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 1, true, true)));

        //Call the GameStartEvent.
        Bukkit.getPluginManager().callEvent(new GameStartEvent(this));
    }

    private void update() {
        //Loop over all control points.
        for (ControlPoint point : this.map.getPoints()) {

            //Update the capture progress.
            point.updateCaptureProgress(this, point.getCurrentlyCapturing(this));

            //Continue to the next point if the current point is empty.
            if (point.isEmpty(this)) {
                continue;
            }

            //Show the capture progress to all players on the point.
            point.showProgressToPlayers(this);

            //Check if the point is captured and handle point capture if so.
            if (point.captureProgress == 100 || (this.debug && point.captureProgress == 10)) {
                GameHelper.capture(point, this);
                this.points[point.owningTeam.getOpponent().getIndex()]++;
                break;
            }
        }

        final int blueProgress = this.map.getPoints()[Team.BLUE.getIndex()].captureProgress;
        final int redProgress = this.map.getPoints()[Team.RED.getIndex()].captureProgress;
        for (int i = 0; i < 2; i++) {
            this.teams[i].getPlayers().stream()
                    .filter(player -> !PlayerHelper.isCapturing(player, this))
                    .filter(player -> Bukkit.getPlayer(player) != null)
                    .forEach(player -> Messager.playerActionBar(Bukkit.getPlayer(player),
                            References.CAPTURE_PROGRESS_LAYOUT
                                    .replace("%blue%", String.valueOf(blueProgress))
                                    .replace("%red%", String.valueOf(redProgress))
                                    .replace("%bluepoints%", String.valueOf(this.points[Team.BLUE.getIndex()]))
                                    .replace("%redpoints%", String.valueOf(this.points[Team.RED.getIndex()]))
                                    .replace("&l", !Bukkit.getPlayer(player).getMetadata("electric").isEmpty() ? "&l&k" : "&l")
                    ));
        }

        //Check if a team has enough points, if so, end the game.
        for (int i = 0; i < this.points.length; i++) {
            if (this.points[i] == References.WINNING_POINTS) {
                this.gameTimer.stop();
                final int teamIndex = Team.getTeam(i).getIndex();
                new Timer(40, KingdomWars.getInstance()).start(() -> this.finish(Team.getTeam(teamIndex)));
            }
        }
    }

    private void finish(final Team winner) {
        //Get the losing team.
        final Team loser = winner.getOpponent();

        //Send the titles to the teams.
        Messager.teamTitle(References.GAME_WIN_TITLE, References.GAME_WIN_SUBTITLE, winner, this);
        Messager.teamTitle(References.GAME_LOSS_TITLE, References.GAME_LOSS_SUBTITLE, loser, this);

        //Call the GameFinishEvent.
        Bukkit.getPluginManager().callEvent(new GameFinishEvent(this, winner));

        //Loop over all players.
        this.players.stream().filter(player -> Bukkit.getPlayer(player) != null).forEach(player -> {

            //Play the finish sound.
            SoundSystem.playerSound(Bukkit.getPlayer(player), Sound.ENTITY_WITHER_DEATH, 1.0F, 1.5F);

            //Launch fireworks at all winners.
            if (PlayerHelper.getTeam(player).equals(winner)) {
                FireworkHelper.endGameFireworks(Bukkit.getPlayer(player));
            }
        });

        //Stop the game after 60 ticks.
        new Timer(60, KingdomWars.getInstance()).start(this::stop);
    }

    private void balanceTeams() {
        for (int i = 0; i < 2; i++) {
            this.rawTeams[i] = new ArrayList<>();
        }

        Collections.shuffle(this.players, ThreadLocalRandom.current());

        Party party = null;
        if (!Party.REGISTRY.getRegisteredObjects().isEmpty()) {
            party = Party.REGISTRY.getRandom();
        }

        if (party == null) {
            for (int i = 0; i < this.players.size(); i++) {
                this.rawTeams[i % 2 == 0 ? 0 : 1].add(this.players.get(i));
            }
        } else {
            int partySize = party.getMembers().size();
            for (int i = 0; i < this.players.size(); i++) {
                UUID player = this.players.get(i);
                if (party.getMembers().contains(player)) {
                    this.rawTeams[Team.BLUE.getIndex()].add(player);
                } else {
                    if (partySize > 0) {
                        this.rawTeams[Team.RED.getIndex()].add(player);
                        partySize--;
                    } else {
                        this.rawTeams[i % 2 == 0 ? 1 : 0].add(player);
                    }
                }
            }
        }

        for (int i = 0; i < 2; i++) {
            Collections.shuffle(this.rawTeams[i], random);
            TeamBalancer balancer = new TeamBalancer(this.rawTeams[i]);
            balancer.balance();
            this.teams[i] = balancer.getTeamInfo();
        }
    }

    public void stop() {
        EventLogger.log(new Message("info", "Stopping game " + Integer.toHexString(this.hashCode())));

        if (this.tracker != null) {
            this.tracker.disable();
        }

        final Location spawn = Bukkit.getWorlds().get(0).getSpawnLocation();
        this.players.forEach(p -> {
            Player player = Bukkit.getPlayer(p);
            if (player != null) {
                try {
                    player.teleport(spawn);
                    player.getInventory().clear();
                    player.getInventory().setArmorContents(null);
                    player.setBedSpawnLocation(spawn, true);

                    Bukkit.getScoreboardManager().getMainScoreboard().getTeam(PlayerHelper.getTeam(p).name()).removeEntry(player.getName());

                    DiscordBot.attemptMovePlayer(player, "403635307481923606");
                } catch (Exception ex) {
                    KingdomWars.getInstance().getLogger().severe("Error while resetting players " + ex);
                    ex.printStackTrace();
                }
            }
        });

        if (this.gameTimer != null) {
            this.gameTimer.stop();
        }

        this.deployables.forEach(this::removeDeployable);
        this.deployables.clear();

        this.currentPhase = GamePhase.ENDING;
        this.map.stopRendering();
        this.map.reset(this);
        this.players.clear();
        this.points = new int[2];
        for (int i = 0; i < 2; i++) {
            this.rawTeams[i] = new ArrayList<>();
        }
        this.debug = false;
        this.activeTraits.forEach(Trait::stop);
        this.grenades.forEach(Grenade::remove);
        this.map.getMedkits().forEach(MedKit::disable);
        this.map.getSpeedPacks().forEach(SpeedPack::disable);

        EventLogger.log(this, new Message(new String[]{"phase", "players"}, new String[]{this.currentPhase.name(), "[]"}));
        EventLogger.log(this, new Message(new String[]{"teamBlue", "teamRed"}, new String[]{"[]", "[]"}));
    }

    public List<UUID> getPlayers() {
        return this.players.stream().filter(player -> Bukkit.getPlayer(player) != null).collect(Collectors.toList());
    }

    public void addPlayer(UUID uuid) {
        this.players.add(uuid);
    }

    public GamePhase getCurrentPhase() {
        return currentPhase;
    }

    public Map getMap() {
        return map;
    }

    public boolean isFull() {
        return players.size() == Math.toIntExact((long) this.map.getProperty("maxplayers"));
    }

    public List<UUID> getTeam(Team team) {
        return this.teams[team.getIndex()].getPlayers().stream().filter(player -> Bukkit.getPlayer(player) != null).collect(Collectors.toList());
    }

    public boolean isAlmostOverFor(Team team) {
        return this.points[team.getIndex()] + 1 == References.WINNING_POINTS;
    }

    public void enableDebugMode() {
        this.debug = true;
    }

    public GameTracker getTracker() {
        return tracker;
    }

    public Lobby getLobby() {
        return lobby;
    }

    @SuppressWarnings("unused")
    public void disableDebugMode() {
        this.debug = false;
    }

    @SuppressWarnings("unused")
    public void loadInDebugMode() {
        this.enableDebugMode();
        this.load();
    }

    public void regenerateBoosts() {
        this.map.getMedkits().forEach(MedKit::regen);
        this.map.getSpeedPacks().forEach(SpeedPack::regen);
    }

    public void freezePoints() {
        for (ControlPoint controlPoint : this.map.getPoints()) {
            controlPoint.setFrozen(true);
        }
    }

    public void unFreezePoints() {
        for (ControlPoint controlPoint : this.map.getPoints()) {
            controlPoint.setFrozen(false);
        }
    }

    public void addDeployable(Deployable deployable) {
        this.deployables.add(deployable);
    }

    public void removeDeployable(Deployable deployable) {
        deployable.remove();
    }

    public List<Deployable> getDeployables() {
        return deployables;
    }
}
