package me.koenn.kindomwars.game;

import me.koenn.core.misc.Timer;
import me.koenn.kindomwars.KingdomWars;
import me.koenn.kindomwars.util.Messager;
import me.koenn.kindomwars.util.PlayerHelper;
import me.koenn.kindomwars.util.References;
import me.koenn.kindomwars.util.Team;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
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

    private final List<Player> players = new ArrayList<>();
    private final List<Player> teamBlue = new ArrayList<>();
    private final List<Player> teamRed = new ArrayList<>();
    private final List<Player>[] teams = new List[2];
    private final Random random = new Random(System.nanoTime());
    private final Map map;
    private final TeamBalancer[] balancedTeams = new TeamBalancer[2];
    private GamePhase currentPhase;
    private int bluePointPercentage = 0;
    private int redPointPercentage = 0;
    private int gameTask;

    public Game(Map map) {
        this.currentPhase = GamePhase.LOADING;
        this.map = map;
        gameRegistry.add(this);
        teams[0] = teamBlue;
        teams[1] = teamRed;
    }

    public void load() {
        this.currentPhase = GamePhase.STARTING;
        Collections.shuffle(this.players);
        this.balanceTeams();

        Messager.gameMessage(this, References.GAME_ABOUT_TO_START);

        for (Player player : this.teamBlue) {
            player.teleport(this.map.getBlueSpawn());
            player.setBedSpawnLocation(this.map.getBlueSpawn(), true);
            player.setGameMode(GameMode.SURVIVAL);
            Messager.playerMessage(player, References.YOUR_TEAM.replace("%team%", "&l&1Blue"));
            Messager.playerMessage(player, References.CLASS.replace("%class%", balancedTeams[0].getBalancedTeam().get(player).getName()));

            player.getInventory().clear();
            PlayerHelper.giveKit(player, this.balancedTeams[0].getBalancedTeam().get(player).getKits()[0]);
        }
        for (Player player : this.teamRed) {
            player.teleport(this.map.getRedSpawn());
            player.setBedSpawnLocation(this.map.getRedSpawn(), true);
            player.setGameMode(GameMode.SURVIVAL);
            Messager.playerMessage(player, References.YOUR_TEAM.replace("%team%", "&l&cRed"));
            Messager.playerMessage(player, References.CLASS.replace("%class%", balancedTeams[1].getBalancedTeam().get(player).getName()));

            player.getInventory().clear();
            PlayerHelper.giveKit(player, this.balancedTeams[1].getBalancedTeam().get(player).getKits()[0]);
        }

        new Timer(References.GAME_START_DELAY * 20, KingdomWars.getInstance()).start(this::start);
    }

    private void start() {
        this.currentPhase = GamePhase.STARTED;
        this.map.startRendering();

        this.gameTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(KingdomWars.getInstance(), () -> {
            if (!this.map.getRedControlPoint().captureNeutral(this)) {
                Team team = this.map.getRedControlPoint().getCurrentlyCapturing(this);
                switch (team) {
                    case BLUE:
                        if (redPointPercentage < 100) {
                            redPointPercentage++;
                        }
                        break;
                    case RED:
                        if (redPointPercentage > 0) {
                            redPointPercentage--;
                        }
                        break;
                }
                this.map.getRedControlPoint().showProgressToPlayers(this, redPointPercentage);
            }
            if (!this.map.getBlueControlPoint().captureNeutral(this)) {
                Team team = this.map.getBlueControlPoint().getCurrentlyCapturing(this);
                switch (team) {
                    case BLUE:
                        if (bluePointPercentage > 0) {
                            bluePointPercentage--;
                        }
                        break;
                    case RED:
                        if (bluePointPercentage < 100) {
                            bluePointPercentage++;
                        }
                        break;
                }
                this.map.getBlueControlPoint().showProgressToPlayers(this, bluePointPercentage);
            }
        }, 0, 10);

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

        for (int i = 0; i < 2; i++) {
            Collections.shuffle(teams[i], random);
            TeamBalancer balancer = new TeamBalancer(teams[i]);
            balancer.balance();
            balancedTeams[i] = balancer;
        }
    }

    public void cancel() {
        //this.map.unload();
        this.players.clear();
        gameRegistry.remove(this);
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

    public Map getMap() {
        return map;
    }

    public boolean isFull() {
        return players.size() == References.TEAM_SIZE;
    }
}
