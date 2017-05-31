package me.koenn.kingdomwars.game;

import me.koenn.core.misc.Timer;
import me.koenn.kingdomwars.KingdomWars;
import me.koenn.kingdomwars.util.Messager;
import me.koenn.kingdomwars.util.References;
import me.koenn.kingdomwars.util.Team;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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
    private GamePhase currentPhase;
    private int gameTask;

    public Game(Map map) {
        this.currentPhase = GamePhase.LOADING;
        this.map = map;
        gameRegistry.add(this);

        for (int i = 0; i < 2; i++) {
            this.rawTeams[i] = new ArrayList<>();
        }
    }

    public void load() {
        this.currentPhase = GamePhase.STARTING;
        Collections.shuffle(this.players);
        this.balanceTeams();
        this.map.load(this);

        Messager.gameMessage(this, References.GAME_ABOUT_TO_START);

        GameHelper.loadPlayers(this);

        GameHelper.loadFakeBlocks(this);

        new Timer(References.GAME_START_DELAY * 20, KingdomWars.getInstance()).start(this::start);
    }

    private void start() {
        this.currentPhase = GamePhase.STARTED;
        this.map.startRendering(this);

        this.gameTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(KingdomWars.getInstance(), this::update, 0, References.UPDATE_RATE);

        Messager.gameMessage(this, References.GAME_STARTED);
    }

    private void update() {
        for (ControlPoint point : this.map.getControlPoints()) {
            if (point.isEmpty(this)) {
                continue;
            }

            point.showProgressToPlayers(this);

            final int captureProgress = point.captureProgress;
            if (captureProgress == 100) {
                GameHelper.capture(point, this);
            }

            Team capturing = point.getCurrentlyCapturing(this);
            point.updateCaptureProgress(capturing);
        }
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
        GameHelper.resetFakeBlocks(this);
        Bukkit.getScheduler().cancelTask(this.gameTask);
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
