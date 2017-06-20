package me.koenn.kingdomwars.game;

import me.koenn.kingdomwars.game.classes.Class;
import me.koenn.kingdomwars.util.PlayerHelper;
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

    public TeamBalancer(List<Player> players) {
        this.players = players;
    }

    public void balance() {
        Collections.shuffle(this.players);

        for (Player player : this.players) {
            this.balancedTeam.put(player, PlayerHelper.getMostPreferredClass(player));
        }
    }

    public TeamInfo getTeamInfo() {
        return new TeamInfo(this.balancedTeam);
    }
}
