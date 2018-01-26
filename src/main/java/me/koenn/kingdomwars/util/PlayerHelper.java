package me.koenn.kingdomwars.util;

import me.koenn.core.player.CPlayer;
import me.koenn.core.player.CPlayerRegistry;
import me.koenn.kingdomwars.characters.Character;
import me.koenn.kingdomwars.characters.CharacterKit;
import me.koenn.kingdomwars.characters.CharacterLoader;
import me.koenn.kingdomwars.game.Game;
import me.koenn.kingdomwars.game.map.ControlPoint;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public final class PlayerHelper {

    @Deprecated
    public static Game getGame(Player player) {
        return getGame(player.getUniqueId());
    }

    public static Game getGame(UUID player) {
        for (Game game : Game.gameRegistry) {
            for (UUID gamePlayer : game.getPlayers()) {
                if (gamePlayer.equals(player)) {
                    return game;
                }
            }
        }
        return null;
    }

    @Deprecated
    public static Team getTeam(Player player) {
        return getTeam(player.getUniqueId());
    }

    public static Team getTeam(UUID player) {
        final Game game = getGame(player);
        if (game == null) {
            return null;
        }
        return game.getTeam(Team.BLUE).contains(player) ? Team.BLUE : Team.RED;
    }

    public static boolean canDamage(UUID player1, UUID player2) {
        return getTeam(player1) != getTeam(player2);
    }

    public static boolean isInGame(Player player) {
        return isInGame(player.getUniqueId());
    }

    public static boolean isInGame(UUID player) {
        return getGame(player) != null;
    }

    public static Character getSelectedCharacter(UUID player) {
        CPlayer cPlayer = CPlayerRegistry.getCPlayer(player);
        if (cPlayer != null && cPlayer.hasField("character")) {
            return CharacterLoader.CHARACTER_REGISTRY.get(cPlayer.get("character"));
        } else {
            return CharacterLoader.CHARACTER_REGISTRY.getRandom();
        }
    }

    public static String getPreviousCharacter(UUID player) {
        return CPlayerRegistry.getCPlayer(player).get("character");
    }

    public static void giveKit(Player player, CharacterKit kit) {
        kit.getItems().forEach(itemStack -> player.getInventory().addItem(itemStack));
    }

    public static String[] usernameArray(List<UUID> playerList) {
        final String[] players = new String[playerList.size()];
        for (int i = 0; i < players.length; i++) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(playerList.get(i));
            players[i] = player.getName() + ": " + PlayerHelper.getPreviousCharacter(player.getUniqueId());
        }
        return players;
    }

    public static boolean isCapturing(UUID player, Game game) {
        for (ControlPoint controlPoint : game.getMap().getPoints()) {
            if (controlPoint.isInRange(player)) {
                return true;
            }
        }
        return false;
    }
}
