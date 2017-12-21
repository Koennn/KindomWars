package me.koenn.kingdomwars.game;

import me.koenn.kingdomwars.characters.Character;
import me.koenn.kingdomwars.util.PlayerHelper;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public class TeamBalancer {

    private final HashMap<Player, Character> balancedTeam = new HashMap<>();
    private final List<Player> players;

    public TeamBalancer(List<Player> players) {
        this.players = players;
    }

    public void balance() {
        Collections.shuffle(this.players, ThreadLocalRandom.current());

        for (Player player : this.players) {
            this.balancedTeam.put(player, PlayerHelper.getSelectedCharacter(player));
        }
    }

    public TeamInfo getTeamInfo() {
        return new TeamInfo(this.balancedTeam);
    }
}
