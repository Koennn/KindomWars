package me.koenn.kingdomwars.game;

import me.koenn.core.misc.Timer;
import me.koenn.kingdomwars.KingdomWars;
import me.koenn.kingdomwars.logger.EventLogger;
import me.koenn.kingdomwars.logger.Message;
import me.koenn.kingdomwars.util.Messager;
import me.koenn.kingdomwars.util.PlayerHelper;
import me.koenn.kingdomwars.util.References;
import me.koenn.kingdomwars.util.Team;
import org.bukkit.entity.Player;

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
    private final Map map;

    private int[] points = new int[2];
    private GamePhase currentPhase;
    private Timer gameTimer;

    public Game(Map map) {
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

        //Load the players and fakeblocks.
        GameHelper.loadPlayers(this);
        //GameHelper.loadFakeBlocks(this);

        //Start the game start timer.
        new Timer(References.GAME_START_DELAY * 20, KingdomWars.getInstance()).start(this::start);
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
    }

    private void update() {
        //Loop over all control points.
        for (ControlPoint point : this.map.getControlPoints()) {

            //Continue to the next point if the current point is empty.
            if (point.isEmpty(this)) {
                continue;
            }

            //Show the capture progress to all players on the point.
            point.showProgressToPlayers(this);

            //Check if the point is captured and handle point capture if so.
            //TODO: Inline variable?
            final int captureProgress = point.captureProgress;
            if (captureProgress == 100) {
                GameHelper.capture(point, this);
                this.points[point.owningTeam.getIndex()]++;
            }

            //Update the capture progress to the team that's currently capturing.
            //TODO: ?
            Team capturing = point.getCurrentlyCapturing(this);
            point.updateCaptureProgress(capturing);
        }

        //Check if a team has 3 points, if so, finish the game.
        for (int i = 0; i < this.points.length; i++) {
            if (points[i] == 3) {
                this.finish(Team.getTeam(i));
            }
        }
    }

    private void finish(Team winner) {

    }

    private void balanceTeams() {
        for (int i = 0; i < 2; i++) {
            this.rawTeams[i] = new ArrayList<>();
        }

        for (int i = 0; i < this.players.size(); i++) {
            this.rawTeams[i % 2 == 0 ? 0 : 1].add(this.players.get(i));
        }

        for (int i = 0; i < 2; i++) {
            Collections.shuffle(rawTeams[i], random);
            TeamBalancer balancer = new TeamBalancer(rawTeams[i], i == 1 ? Team.BLUE : Team.RED);
            balancer.balance();
            teams[i] = balancer.getTeamInfo();
        }
    }

    public void cancel() {
        EventLogger.log(new Message("info", "Cancelling game " + Integer.toHexString(this.hashCode())));
        EventLogger.log(this, new Message("phase", GamePhase.ENDING.name()));
        GameHelper.resetFakeBlocks(this);
        this.gameTimer.stop();
        this.map.stopRendering();
        this.map.reset();
        this.players.clear();
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
}
