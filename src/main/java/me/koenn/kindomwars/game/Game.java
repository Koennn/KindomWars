package me.koenn.kindomwars.game;

import me.koenn.kindomwars.KingdomWars;
import me.koenn.kindomwars.util.Messager;
import me.koenn.kindomwars.util.References;
import me.koenn.kindomwars.util.Timer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public class Game {

    public static final List<Game> gameRegistry = new ArrayList<>();

    private final List<Player> players = new ArrayList<>();
    private final List<Player> teamBlue = new ArrayList<>();
    private final List<Player> teamRed = new ArrayList<>();
    private final Map map;
    private GamePhase currentPhase;

    public Game(Map map) {
        this.currentPhase = GamePhase.LOADING;
        this.map = map;
        gameRegistry.add(this);
    }

    public void load() {
        this.currentPhase = GamePhase.STARTING;
        this.balanceTeams();

        Messager.gameMessage(this, References.GAME_ABOUT_TO_START);

        for (Player player : this.teamBlue) {
            player.teleport(this.map.getBlueSpawn());
        }
        for (Player player : this.teamRed) {
            player.teleport(this.map.getRedSpawn());
        }

        new Timer(References.GAME_START_DELAY * 20, KingdomWars.getInstance()).start(this::start);

        this.currentPhase = GamePhase.STARTED;
    }

    private void start() {
        this.currentPhase = GamePhase.STARTED;

        Messager.gameMessage(this, References.GAME_STARTED);
    }

    private void balanceTeams() {
        this.teamBlue.clear();
        this.teamRed.clear();
        for (int i = 0; i < this.players.size(); i++) {
            if (i % 2 == 0) {
                this.teamBlue.add(this.players.get(i));
            } else {
                this.teamRed.add(this.players.get(i));
            }
        }
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Player> getTeamBlue() {
        return teamBlue;
    }

    public List<Player> getTeamRed() {
        return teamRed;
    }

    public GamePhase getCurrentPhase() {
        return currentPhase;
    }

    public boolean isFull() {
        return players.size() == References.TEAM_SIZE;
    }
}
