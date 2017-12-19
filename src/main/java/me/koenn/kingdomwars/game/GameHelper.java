package me.koenn.kingdomwars.game;

import me.koenn.core.misc.Timer;
import me.koenn.kingdomwars.KingdomWars;
import me.koenn.kingdomwars.game.classes.Class;
import me.koenn.kingdomwars.game.map.ControlPoint;
import me.koenn.kingdomwars.game.map.Map;
import me.koenn.kingdomwars.util.Messager;
import me.koenn.kingdomwars.util.PlayerHelper;
import me.koenn.kingdomwars.util.References;
import me.koenn.kingdomwars.util.Team;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential Written by Koen Willemse, April 2017
 */
public final class GameHelper implements Listener {

    public static void loadPlayers(Game game) {
        for (final Player player : game.getPlayers()) {
            final Team team = PlayerHelper.getTeam(player);
            final Map map = game.getMap();
            final Location spawn = getSpawn(map, team);
            final Class cl = getClass(player, team, game);

            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            player.setGameMode(GameMode.SURVIVAL);
            player.setBedSpawnLocation(spawn, true);

            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 0, true, false));

            Messager.clearChat(player);

            for (String line : References.GAME_JOIN_MESSAGE) {
                if (line.contains("%clickable%")) {
                    Messager.clickableMessage(player,
                            line.replace("%clickable%", ""),
                            "Click to open the map lore page",
                            "http://blockgaming.org/staff/forums/index.php?forums/maplore/"
                    );
                } else {
                    Messager.playerMessage(player, line
                            .replace("%map%", game.getMap().getName())
                            .replace("%desc%", "-- DESCRIPTION COMING SOON --")
                            .replace("%class%", cl.getName())
                    );
                }
            }

            PlayerHelper.giveKit(player, cl.getKits()[0]);
        }

        teleportPlayers(game);
    }

    public static void capture(ControlPoint point, Game game) {
        final Team lost = point.owningTeam;
        final Team won = point.owningTeam.getOpponent();

        Messager.teamTitle(References.CAPTURE_WIN_TITLE, References.CAPTURE_WIN_SUBTITLE, won, game);
        Messager.teamTitle(References.CAPTURE_LOSS_TITLE, References.CAPTURE_LOSS_SUBTITLE, lost, game);
        game.getPlayers().forEach(player -> player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.5F, 0.5F));

        game.getMap().renderCapture(lost);

        for (ControlPoint controlPoint : game.getMap().getPoints()) {
            controlPoint.reset(game);
        }

        if (!game.isAlmostOverFor(won)) {
            new Timer(20, KingdomWars.getInstance()).start(() -> teleportPlayers(game));
        }
    }

    public static void teleportPlayers(Game game) {
        game.getPlayers().forEach(player -> player.teleport(getSpawn(game.getMap(), PlayerHelper.getTeam(player))));
    }

    public static Location getSpawn(Map map, Team team) {
        Location spawn = map.getSpawn(team);
        return spawn.getBlock() == null ? spawn : spawn.clone().add(0.5, 1.0, 0.5);
    }

    public static Class getClass(Player player, Team team, Game game) {
        TeamInfo teamInfo = game.teams[team.getIndex()];
        return teamInfo.getClass(player);
    }
}
