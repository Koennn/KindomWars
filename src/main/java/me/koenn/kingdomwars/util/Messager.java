package me.koenn.kingdomwars.util;

import me.koenn.core.misc.ActionBar;
import me.koenn.core.misc.ColorHelper;
import me.koenn.core.misc.Title;
import me.koenn.kingdomwars.KingdomWars;
import me.koenn.kingdomwars.game.Game;
import mkremins.fanciful.FancyMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

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
            player.sendMessage(ColorHelper.readColor(message));
        }
    }

    public static void gameMessage(Game game, String message) {
        for (Player player : game.getPlayers()) {
            player.sendMessage(ColorHelper.readColor(message));
        }
    }

    public static void playerMessage(Player player, String message) {
        player.sendMessage(ColorHelper.readColor(message));
    }

    public static void clickableMessage(Player player, String message, String tooltip, String url) {
        FancyMessage fancyMessage = new FancyMessage(ColorHelper.readColor(message));
        fancyMessage.tooltip(ColorHelper.readColor(tooltip)).link(url);
        fancyMessage.send(player);
    }

    public static void teamTitle(String title, String subtitle, Team team, Game game) {
        List<Player> players = game.getTeam(team);
        Title titleObj = new Title(title, subtitle).setFade(1);
        players.forEach(titleObj::send);
    }

    public static void playerTitle(String title, String subtitle, Player player) {
        new Title(title, subtitle).setFade(1).send(player);
    }

    public static void playerActionBar(Player player, String message) {
        new ActionBar(message, KingdomWars.getInstance()).send(player);
    }

    public static void clearChat(Player player) {
        for (int i = 0; i < 40; i++) {
            player.sendMessage("");
        }
    }
}
