package me.koenn.kingdomwars.game;

import me.koenn.core.misc.Timer;
import me.koenn.kingdomwars.KingdomWars;
import me.koenn.kingdomwars.deployables.Deployable;
import me.koenn.kingdomwars.logger.EventLogger;
import me.koenn.kingdomwars.logger.Message;
import me.koenn.kingdomwars.util.Messager;
import me.koenn.kingdomwars.util.PlayerHelper;
import me.koenn.kingdomwars.util.References;
import me.koenn.kingdomwars.util.Team;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

import java.util.*;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public class Game {

    public static final List<Game> gameRegistry = new ArrayList<>();
    public static final Random random = new Random(System.nanoTime());

    public final TeamInfo[] teams = new TeamInfo[2];
    private final List<Player> players = new ArrayList<>();
    private final List<Player>[] rawTeams = new List[2];
    private final List<Deployable> deployables = new ArrayList<>();
    private final Map map;

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
    }

    public void load() {
        try {
            //Set current phase and log messages.
            this.currentPhase = GamePhase.STARTING;
            EventLogger.log(new Message("info", "Loading game " + Integer.toHexString(this.hashCode())));
            EventLogger.log(this, new Message(new String[]{"phase", "players"}, new String[]{this.currentPhase.name(), Arrays.toString(PlayerHelper.usernameArray(this.players))}));

            //Shuffle and balance teams.
            Collections.shuffle(this.players);
            this.balanceTeams();

            //Log teams.
            EventLogger.log(this, new Message(new String[]{"teamBlue", "teamRed"}, new String[]{
                    Arrays.toString(PlayerHelper.usernameArray(this.teams[Team.RED.getIndex()].getPlayers())),
                    Arrays.toString(PlayerHelper.usernameArray(this.teams[Team.BLUE.getIndex()].getPlayers()))
            }));

            //Load the map.
            this.map.load(this);

            //Send game starting message.
            Messager.gameMessage(this, References.GAME_ABOUT_TO_START);
            this.players.forEach(player -> player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2.0F, 1.0F));

            //Load the players.
            GameHelper.loadPlayers(this);

            //Start the game start timer.
            new Timer(References.GAME_START_DELAY * (this.debug ? 1 : 20), KingdomWars.getInstance()).start(this::start);
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
        this.map.startRendering(this);

        //Start the game update timer.
        this.gameTimer = new Timer(References.UPDATE_RATE, true, KingdomWars.getInstance());
        this.gameTimer.start(this::update);

        //Send the game started message.
        Messager.gameMessage(this, References.GAME_STARTED);

        //Play game started sound.
        players.forEach(player -> player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0F, 1.0F));
    }

    private void update() {
        //Loop over all control points.
        for (final ControlPoint point : this.map.getControlPoints()) {

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
            }
        }

        final int blueProgress = this.map.getControlPoints()[Team.BLUE.getIndex()].captureProgress;
        final int redProgress = this.map.getControlPoints()[Team.RED.getIndex()].captureProgress;
        for (int i = 0; i < 2; i++) {
            final Team team = Team.getTeam(i);
            this.teams[i].getPlayers().stream().filter(player -> !PlayerHelper.isCapturing(player, this)).forEach(player -> Messager.playerActionBar(player,
                    References.CAPTURE_PROGRESS_LAYOUT
                            .replace("%blue%", String.valueOf(team == Team.BLUE ? blueProgress : redProgress))
                            .replace("%red%", String.valueOf(team == Team.BLUE ? redProgress : blueProgress))
                            .replace("%bluepoints%", String.valueOf(this.points[team.getIndex()]))
                            .replace("%redpoints%", String.valueOf(this.points[team.getOpponent().getIndex()]))
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

        //Loop over all players.
        this.players.forEach(player -> {

            //Play the finish sound.
            player.playSound(player.getLocation(), Sound.ENTITY_WITHER_DEATH, 1.0F, 1.5F);

            if (PlayerHelper.getTeam(player).equals(loser)) {
                return;
            }

            //Create and launch the firework.
            for (int i = 0; i < 5; i++) {
                new Timer(i * 5, KingdomWars.getInstance()).start(() -> {
                    Firework firework = player.getWorld().spawn(player.getLocation(), Firework.class);
                    FireworkMeta meta = firework.getFireworkMeta();
                    meta.addEffect(FireworkEffect.builder()
                            .with(FireworkEffect.Type.values()[random.nextInt(FireworkEffect.Type.values().length)])
                            .withColor(Color.fromRGB(random.nextInt(255), random.nextInt(255), random.nextInt(255)))
                            .build()
                    );
                    firework.setVelocity(new Vector(0, 0.5, 0));
                    firework.setFireworkMeta(meta);
                });
            }
        });

        //Stop the game after 60 ticks.
        new Timer(60, KingdomWars.getInstance()).start(this::stop);
    }

    private void balanceTeams() {
        for (int i = 0; i < 2; i++) {
            this.rawTeams[i] = new ArrayList<>();
        }

        for (int i = 0; i < this.players.size(); i++) {
            this.rawTeams[i % 2 == 0 ? 0 : 1].add(this.players.get(i));
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

        final Location spawn = Bukkit.getWorlds().get(0).getSpawnLocation();
        this.players.forEach(player -> {
            try {
                player.teleport(spawn);
                player.getInventory().clear();
                player.getInventory().setArmorContents(null);
                player.setBedSpawnLocation(spawn, true);
            } catch (Exception ex) {
                KingdomWars.getInstance().getLogger().severe("Error while resetting players " + ex);
            }
        });

        if (this.gameTimer != null) {
            this.gameTimer.stop();
        }

        this.deployables.forEach(Deployable::remove);
        this.deployables.clear();

        this.currentPhase = GamePhase.ENDING;
        this.map.stopRendering();
        this.map.reset();
        this.players.clear();
        this.points = new int[2];
        for (int i = 0; i < 2; i++) {
            this.rawTeams[i] = new ArrayList<>();
        }
        this.debug = false;
        EventLogger.log(this, new Message(new String[]{"phase", "players"}, new String[]{this.currentPhase.name(), "[]"}));
        EventLogger.log(this, new Message(new String[]{"teamBlue", "teamRed"}, new String[]{"[]", "[]"}));
    }

    public List<Player> getPlayers() {
        return players;
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

    public List<Player> getTeam(Team team) {
        return this.teams[team.getIndex()].getPlayers();
    }

    //TODO: What in case of a comeback?
    public boolean isAlmostOver() {
        for (int point : this.points) {
            if (point + 1 == References.WINNING_POINTS) {
                return true;
            }
        }
        return false;
    }

    public void enableDebugMode() {
        this.debug = true;
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

    @SuppressWarnings("unused")
    public void freezePoints() {
        for (ControlPoint controlPoint : this.map.getControlPoints()) {
            controlPoint.setFrozen(true);
        }
    }

    @SuppressWarnings("unused")
    public void unFreezePoints() {
        for (ControlPoint controlPoint : this.map.getControlPoints()) {
            controlPoint.setFrozen(false);
        }
    }

    public void addDeployable(Deployable deployable) {
        this.deployables.add(deployable);
    }

    public void removeDeployable(Deployable deployable) {
        this.deployables.remove(deployable);
    }

    public List<Deployable> getDeployables() {
        return deployables;
    }
}
