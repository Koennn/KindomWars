package me.koenn.kindomwars.util;

import me.koenn.core.player.CPlayerRegistry;
import me.koenn.kindomwars.game.Game;
import me.koenn.kindomwars.game.classes.Class;
import me.koenn.kindomwars.game.classes.ClassLoader;
import me.koenn.kindomwars.game.classes.Kit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
        return game.getTeam(Team.BLUE).contains(player) ? Team.BLUE : Team.RED;
    }

    public static boolean canDamage(Player player1, Player player2) {
        return getTeam(player1) != getTeam(player2);
    }

    public static boolean isInGame(Player player) {
        return getGame(player) != null;
    }

    public static Class getMostPreferredClass(Player player) {
        return ClassLoader.getClass(CPlayerRegistry.getCPlayer(player.getUniqueId()).get("most-preferred-class"));
    }

    public static Class getLeastPreferredClass(Player player) {
        return ClassLoader.getClass(CPlayerRegistry.getCPlayer(player.getUniqueId()).get("least-preferred-class"));
    }

    public static void giveKit(Player player, Kit kit) {
        for (ItemStack item : kit.getItems()) {
            player.getInventory().addItem(item);
        }
    }
}
