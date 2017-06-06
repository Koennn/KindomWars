package me.koenn.kingdomwars.game;

import me.koenn.kingdomwars.KingdomWars;
import me.koenn.kingdomwars.game.classes.Class;
import me.koenn.kingdomwars.game.classes.ClassLoader;
import me.koenn.kingdomwars.util.PlayerHelper;
import me.koenn.kingdomwars.util.Team;
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
//TODO: Needs major fixes!
public class TeamBalancer {

    private final HashMap<Player, Class> balancedTeam = new HashMap<>();
    private final List<Player> players;
    private final int classSize;
    private final Team team;
    private int cycles = 0;

    public TeamBalancer(List<Player> players, Team team) {
        this.team = team;
        this.players = players;
        this.classSize = players.size() / 4;
    }

    public void balance() {
        KingdomWars.getInstance().getLogger().info(String.format("Starting teambalance with %s players...", players.size()));
        Collections.shuffle(this.players);

        for (Player player : this.players) {
            this.balancedTeam.put(player, PlayerHelper.getMostPreferredClass(player));
        }

        if (checkDone()) {
            if (classSize == 0) {
                KingdomWars.getInstance().getLogger().info("Not enough players, aborting balance!");
            } else {
                KingdomWars.getInstance().getLogger().info("Perfect teambalance found!");
            }
            return;
        }

        KingdomWars.getInstance().getLogger().info("Starting to fill required classes...");
        fillNeededClasses();
    }

    private void fillNeededClasses() {
        cycles++;
        Class needed = getNeededClass();
        Class overfilled = getOverfilledClass();
        if (overfilled == null || needed == null) {
            KingdomWars.getInstance().getLogger().info("Unable to balance teams, stopping balance after " + cycles + " cycle(s)!");
            return;
        }

        KingdomWars.getInstance().getLogger().info("Need class " + needed.getName() + ", overfilled class " + overfilled.getName());

        Player move = getRandomPlayerFromClass(overfilled, needed);
        this.balancedTeam.put(move, needed);

        if (checkDone()) {
            KingdomWars.getInstance().getLogger().info("Teambalance complete!");
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

    public int getClassSize(Class cl) {
        int size = 0;
        for (Class cl2 : balancedTeam.values()) {
            if (cl2 != null && cl2.equals(cl)) {
                size++;
            }
        }
        return size;
    }

    private boolean checkDone() {
        boolean done = true;
        for (Class cl : ClassLoader.getClasses()) {
            int size = getClassSize(cl);
            KingdomWars.getInstance().getLogger().info(cl.getName() + " now has size " + size + " and must have more than or equal to " + classSize);
            if (size < classSize) {
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
