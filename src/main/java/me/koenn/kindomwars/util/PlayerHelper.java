package me.koenn.kindomwars.util;

import me.koenn.kindomwars.game.Game;
import org.bukkit.entity.Player;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public final class PlayerHelper {

    public static Game getGame(Player player) {
        for (Game game : Game.gameRegistry) {
            for (Player gamePlayer : game.getPlayers()) {
                if (gamePlayer.getUniqueId().equals(player.getUniqueId())) {
                    return game;
                }
            }
        }
        return null;
    }

    public static Team getTeam(Player player) {
        Game game = getGame(player);
        if (game == null) {
            return null;
        }
        return game.getTeamBlue().contains(player) ? Team.BLUE : Team.RED;
    }

    public static boolean canDamage(Player player1, Player player2) {
        return getTeam(player1) != getTeam(player2);
    }

    public static boolean isInGame(Player player) {
        return getGame(player) != null;
    }
}
