package me.koenn.kindomwars.game;

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
public class GameCreator {

    private static GameCreator instance;

    public static GameCreator getInstance() {
        return instance;
    }

    private final List<Game> activeGames = new ArrayList<>();

    public GameCreator() {
        instance = this;
    }

    private Game getFreeGame() {
        for (Game game : this.activeGames) {
            if (!game.isFull()) {
                return game;
            }
        }
        Game game = new Game(null);
        this.activeGames.add(game);
        return game;
    }

    public static void join(Player player) {
        Game game = instance.getFreeGame();
        game.getPlayers().add(player);

        if (game.isFull()) {
            game.load();
        }
    }
}
