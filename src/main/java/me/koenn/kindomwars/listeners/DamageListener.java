package me.koenn.kindomwars.listeners;

import me.koenn.kindomwars.KingdomWars;
import me.koenn.kindomwars.game.Game;
import me.koenn.kindomwars.game.GamePhase;
import me.koenn.kindomwars.util.Messager;
import me.koenn.kindomwars.util.PlayerHelper;
import me.koenn.kindomwars.util.References;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.HashMap;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public class DamageListener implements Listener {

    private final HashMap<Player, Integer> respawnCooldown = new HashMap<>();

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) {
            return;
        }

        Player damager = (Player) event.getDamager();
        Player damaged = (Player) event.getEntity();

        if (!PlayerHelper.isInGame(damager) || !PlayerHelper.isInGame(damaged)) {
            return;
        }

        Game game = PlayerHelper.getGame(damager);
        if (game == null) {
            return;
        }

        if (game.getCurrentPhase() != GamePhase.STARTED) {
            event.setCancelled(true);
            return;
        }

        if (!PlayerHelper.canDamage(damager, damaged)) {
            event.setCancelled(true);
            Messager.playerMessage(damager, References.DONT_SHOOT_ALLY);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getEntity().getKiller() == null) {
            return;
        }

        Player killer = event.getEntity().getKiller();
        Player killed = event.getEntity();
        Game game = PlayerHelper.getGame(killed);

        if (!PlayerHelper.isInGame(killed) || game == null) {
            return;
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(KingdomWars.getInstance(), () -> {
            respawnCooldown.put(killed, 0);
            killed.setGameMode(GameMode.SURVIVAL);
            killed.teleport(game.getMap().getSpawn(PlayerHelper.getTeam(killed)));
            Messager.playerMessage(killed, References.RESPAWN);
        }, References.RESPAWN_COOLDOWN * 20);

        if (!PlayerHelper.isInGame(killer)) {
            return;
        }

        Messager.playerMessage(killer, References.KILL);
        Messager.playerMessage(killed, References.DEATH);
        respawnCooldown.put(killed, References.RESPAWN_COOLDOWN);
        killed.setGameMode(GameMode.SPECTATOR);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (event.getPlayer() == null) {
            return;
        }

        Player player = event.getPlayer();
        Game game = PlayerHelper.getGame(player);

        if (!PlayerHelper.isInGame(player) || game == null) {
            return;
        }

        if (respawnCooldown.get(player) != 0) {
            player.setGameMode(GameMode.SPECTATOR);
            event.setRespawnLocation(player.getKiller().getEyeLocation());
            if (player.getKiller() != null) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(KingdomWars.getInstance(), () -> player.teleport(player.getKiller()), 40);
            }
        }
    }
}
