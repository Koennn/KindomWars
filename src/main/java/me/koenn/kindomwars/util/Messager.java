package me.koenn.kindomwars.util;

import me.koenn.kindomwars.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public final class Messager {

    public static void globalMessage(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }

    public static void gameMessage(Game game, String message) {
        for (Player player : game.getPlayers()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }

    public static void playerMessage(Player player, String message) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }
}
