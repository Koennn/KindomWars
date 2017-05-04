package me.koenn.kingdomwars.game;

import me.koenn.kingdomwars.game.classes.Class;
import me.koenn.kingdomwars.util.Team;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public class TeamInfo {

    private final Team team;
    private final HashMap<Player, Class> players;

    public TeamInfo(Team team, HashMap<Player, Class> players) {
        this.team = team;
        this.players = players;
    }

    public Class getClass(Player player) {
        return players.get(player);
    }

    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();
        players.addAll(this.players.keySet());
        return players;
    }
}
