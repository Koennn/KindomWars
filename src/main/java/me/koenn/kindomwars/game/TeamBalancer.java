package me.koenn.kindomwars.game;

import me.koenn.kindomwars.game.classes.Class;
import me.koenn.kindomwars.game.classes.ClassLoader;
import me.koenn.kindomwars.util.PlayerHelper;
import me.koenn.kindomwars.util.Team;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public class TeamBalancer {

    private final HashMap<Player, Class> balancedTeam = new HashMap<>();
    private final List<Player> players;
    private final int classSize;
    private final Team team;
    private int cycles = 0;

    public TeamBalancer(List<Player> players, Team team) {
        this.team = team;
        this.players = players;
        this.classSize = (players.size() - 1) / 4;
    }

    public void balance() {
        Bukkit.getLogger().info(String.format("Starting teambalance with %s players...", players.size()));
        Collections.shuffle(this.players);

        for (Player player : this.players) {
            this.balancedTeam.put(player, PlayerHelper.getMostPreferredClass(player));
        }

        if (checkDone()) {
            if (classSize == 0) {
                Bukkit.getLogger().info("Not enough players, aborting balance!");
            } else {
                Bukkit.getLogger().info("Perfect teambalance found!");
            }
            return;
        }

        Bukkit.getLogger().info("Starting to fill required classes...");
        fillNeededClasses();
    }

    private void fillNeededClasses() {
        cycles++;
        Class needed = getNeededClass();
        Class overfilled = getOverfilledClass();
        if (overfilled == null) {
            Bukkit.getLogger().info("Unable to balance teams, stopping balance after " + cycles + " cycle(s)!");
            return;
        }

        Player move = getRandomPlayerFromClass(overfilled, needed);
        this.balancedTeam.put(move, needed);

        if (checkDone()) {
            Bukkit.getLogger().info("Teambalance complete!");
            return;
        }

        fillNeededClasses();
    }

    private Class getOverfilledClass() {
        for (Class cl : ClassLoader.getClasses()) {
            int size = getClassSize(cl);
            if (size > classSize) {
                return cl;
            }
        }
        return null;
    }

    private int getClassSize(Class cl) {
        int size = 0;
        for (Class cl2 : balancedTeam.values()) {
            if (cl2.equals(cl)) {
                size++;
            }
        }
        return size;
    }

    private boolean checkDone() {
        boolean done = true;
        for (Class cl : ClassLoader.getClasses()) {
            int size = getClassSize(cl);
            if (size < classSize - 1) {
                done = false;
            }
        }
        return done;
    }

    private Class getNeededClass() {
        for (Class cl : ClassLoader.getClasses()) {
            int size = getClassSize(cl);
            if (size < classSize - 1) {
                return cl;
            }
        }
        return null;
    }

    private Player getRandomPlayerFromClass(Class cl, Class moveTo) {
        for (Player player : this.balancedTeam.keySet()) {
            if (this.balancedTeam.get(player).equals(cl)) {
                if (PlayerHelper.getLeastPreferredClass(player).equals(moveTo)) {
                    continue;
                }
                return player;
            }
        }
        return null;
    }

    public TeamInfo getTeamInfo() {
        return new TeamInfo(this.team, this.balancedTeam);
    }
}
