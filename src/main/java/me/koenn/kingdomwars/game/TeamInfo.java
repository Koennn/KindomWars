package me.koenn.kingdomwars.game;

import me.koenn.kingdomwars.characters.Character;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public class TeamInfo {

    private final HashMap<UUID, Character> players;

    public TeamInfo(HashMap<UUID, Character> players) {
        this.players = players;
    }

    public Character getCharacter(UUID player) {
        return players.get(player);
    }

    public List<UUID> getPlayers() {
        return new ArrayList<>(this.players.keySet());
    }
}
