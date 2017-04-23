package me.koenn.kindomwars.game;

import me.koenn.kindomwars.util.Messager;
import me.koenn.kindomwars.util.References;
import org.bukkit.Bukkit;
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

    private static GameCreator instance = new GameCreator();

    private final List<Game> activeGames = new ArrayList<>();

    private GameCreator() {
        instance = this;
    }

    public static void join(Player player) {
        Game game = instance.getFreeGame();
        game.getPlayers().add(player);
        Messager.playerMessage(player, References.JOIN_MESSAGE);

        if (game.isFull()) {
            game.load();
        }
    }

    private Game getFreeGame() {
        for (Game game : this.activeGames) {
            if (!game.isFull()) {
                return game;
            }
        }
        Bukkit.getLogger().info(Map.getRandomMap().getName());
        Game game = new Game(Map.getRandomMap());
        this.activeGames.add(game);
        return game;
    }
}
