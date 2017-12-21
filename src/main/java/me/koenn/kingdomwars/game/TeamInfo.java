package me.koenn.kingdomwars.game;

import me.koenn.kingdomwars.characters.Character;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
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

    private final HashMap<Player, Character> players;

    public TeamInfo(HashMap<Player, Character> players) {
        this.players = players;
    }

    public Character getCharacter(Player player) {
        return players.get(player);
    }

    public List<Player> getPlayers() {
        return new ArrayList<>(this.players.keySet());
    }

    @Override
    public String toString() {
        return "TeamInfo{players=" + Arrays.toString(this.getPlayers().toArray(new Player[this.getPlayers().size()])) + "}";
    }
}
