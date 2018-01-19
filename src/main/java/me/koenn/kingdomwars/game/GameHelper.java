package me.koenn.kingdomwars.game;

import me.koenn.core.misc.FancyString;
import me.koenn.core.misc.Timer;
import me.koenn.kingdomwars.KingdomWars;
import me.koenn.kingdomwars.characters.Character;
import me.koenn.kingdomwars.game.events.GamePointCapEvent;
import me.koenn.kingdomwars.game.map.ControlPoint;
import me.koenn.kingdomwars.game.map.Map;
import me.koenn.kingdomwars.grenade.EMPGrenade;
import me.koenn.kingdomwars.grenade.Grenade;
import me.koenn.kingdomwars.traits.CloakingArmor;
import me.koenn.kingdomwars.traits.ElectricBow;
import me.koenn.kingdomwars.traits.Robot;
import me.koenn.kingdomwars.util.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential Written by Koen Willemse, April 2017
 */
@SuppressWarnings("deprecation")
public final class GameHelper implements Listener {

    public static void loadPlayers(Game game) {
        for (final Player player : game.getPlayers()) {
            final Team team = PlayerHelper.getTeam(player);
            final Map map = game.getMap();
            final Location spawn = getSpawn(map, team);
            final Character character = getCharacter(player, team, game);

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
                            "http://kingdomwarsmc.net/"
                    );
                } else {
                    Messager.playerMessage(player, line
                            .replace("%map%", game.getMap().getName())
                            .replace("%desc%", "-- DESCRIPTION COMING SOON --")
                            .replace("%class%", character.getName())
                            .replace("%color%", team.equals(Team.RED) ? "&c" : "&9")
                            .replace("%team%", new FancyString(team.name()).toString())
                    );
                }
            }

            PlayerHelper.giveKit(player, character.getKit());

            //TODO: FSS KOENN THIS IS SO MESSY AND DUMB...
            if (character.getTrait() != null) {
                if (character.getTrait().equals(CloakingArmor.class)) {
                    game.activeTraits.add(new CloakingArmor(player));
                } else if (character.getTrait().equals(ElectricBow.class)) {
                    game.activeTraits.add(new ElectricBow(player));
                } else if (character.getTrait().equals(Robot.class)) {
                    game.activeTraits.add(new Robot(player));
                }
            }

            if (character.getGrenade() != null) {
                if (character.getGrenade().equals("GRENADE_EMP")) {
                    Grenade grenade = new EMPGrenade();
                    game.grenades.add(grenade);
                    ItemStack item = grenade.getItem();
                    item.setAmount(3);
                    player.getInventory().addItem(item);
                }
            }
        }

        teleportPlayers(game);
    }

    public static void capture(ControlPoint point, Game game) {
        final Team lost = point.owningTeam;
        final Team won = point.owningTeam.getOpponent();

        Bukkit.getPluginManager().callEvent(new GamePointCapEvent(game, won, point.getPlayersOnPoint(game)));

        Messager.teamTitle(References.CAPTURE_WIN_TITLE, References.CAPTURE_WIN_SUBTITLE, won, game);
        Messager.teamTitle(References.CAPTURE_LOSS_TITLE, References.CAPTURE_LOSS_SUBTITLE, lost, game);
        SoundSystem.gameSound(game, Sound.ENTITY_PLAYER_LEVELUP, 1.5F, 0.5F);

        game.getMap().renderCapture(lost);

        for (ControlPoint controlPoint : game.getMap().getPoints()) {
            controlPoint.reset(game);
        }

        if (!game.isAlmostOverFor(won)) {
            new Timer(20, KingdomWars.getInstance()).start(() -> teleportPlayers(game));
        }
    }

    public static void teleportPlayers(Game game) {
        game.getPlayers().forEach(player -> {
            player.teleport(getSpawn(game.getMap(), PlayerHelper.getTeam(player)));
            player.setHealth(player.getMaxHealth());
            player.setSaturation(20.0F);
        });
    }

    public static Location getSpawn(Map map, Team team) {
        Location spawn = map.getSpawn(team);
        return spawn.getBlock() == null ? spawn : spawn.clone().add(0.5, 1.0, 0.5);
    }

    public static Character getCharacter(Player player, Team team, Game game) {
        TeamInfo teamInfo = game.teams[team.getIndex()];
        return teamInfo.getCharacter(player);
    }
}
