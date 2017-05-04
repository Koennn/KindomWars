package me.koenn.kingdomwars.game;

import me.koenn.core.misc.Timer;
import me.koenn.kingdomwars.KingdomWars;
import me.koenn.kingdomwars.game.classes.Class;
import me.koenn.kingdomwars.util.Messager;
import me.koenn.kingdomwars.util.PlayerHelper;
import me.koenn.kingdomwars.util.References;
import me.koenn.kingdomwars.util.Team;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public final class GameHelper {

    public static void loadPlayers(Game game) {
        for (Player player : game.getPlayers()) {
            Team team = PlayerHelper.getTeam(player);
            Map map = game.getMap();
            Location spawn = getSpawn(map, team);
            Class cl = getClass(player, team, game);

            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            player.setGameMode(GameMode.SURVIVAL);
            player.setBedSpawnLocation(spawn, true);

            teleportPlayers(game);

            Messager.clickableMessage(player, References.MAP.replace("%map%", map.getName()), "Click to open the map page", "http://blockgaming.org/staff/forums/index.php?threads/map-specific-lore.13/");
            Messager.playerMessage(player, References.CLASS.replace("%class%", cl.getName()));

            PlayerHelper.giveKit(player, cl.getKits()[0]);
        }
    }

    public static void capture(ControlPoint point, Game game) {
        Team lost = point.owningTeam;
        Team won = point.owningTeam.getOpponent();

        Messager.teamTitle(References.CAPTURE_WIN_TITLE, References.CAPTURE_WIN_SUBTITLE, won, game);
        Messager.teamTitle(References.CAPTURE_LOSS_TITLE, References.CAPTURE_LOSS_SUBTITLE, lost, game);

        game.getMap().renderCapture(lost);

        for (ControlPoint controlPoint : game.getMap().getControlPoints()) {
            controlPoint.reset();
        }

        new Timer(20, KingdomWars.getInstance()).start(() -> teleportPlayers(game));
    }

    public static void teleportPlayers(Game game) {
        for (Player player : game.getPlayers()) {
            player.teleport(getSpawn(game.getMap(), PlayerHelper.getTeam(player)));
        }
    }

    public static Location getSpawn(Map map, Team team) {
        Location spawn = map.getSpawn(team);
        return spawn.getBlock() == null ? spawn : spawn.add(0, 1, 0);
    }

    public static Class getClass(Player player, Team team, Game game) {
        TeamInfo teamInfo = game.teams[team.getIndex()];
        return teamInfo.getClass(player);
    }
}
