package me.koenn.kingdomwars.listeners;

import me.koenn.kingdomwars.game.Game;
import me.koenn.kingdomwars.util.PlayerHelper;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Objects;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, June 2017
 */
public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            return;
        }

        final Player player = event.getPlayer();
        if (!PlayerHelper.isInGame(player)) {
            return;
        }

        final Game game = PlayerHelper.getGame(player);
        final Block clicked = event.getClickedBlock();
        game.getDeployables().stream()
                .filter(Objects::nonNull)
                .forEach(deployable -> deployable.getDeployableBlocks().stream()
                        .filter(Objects::nonNull)
                        .filter(location -> location.equals(clicked.getLocation()))
                        .forEach(location -> deployable.damage(3, player)));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player)) {
            return;
        }

        final Player player = (Player) event.getEntity().getShooter();
        if (!PlayerHelper.isInGame(player)) {
            return;
        }

        int damage = 3;
        if (!event.getEntity().getMetadata("electric").isEmpty()) {
            damage *= 2;
        }

        final int finalDamage = damage;
        final Game game = PlayerHelper.getGame(player);
        final Block hit = event.getHitBlock();
        game.getDeployables().stream()
                .filter(Objects::nonNull)
                .forEach(deployable -> deployable.getDeployableBlocks().stream()
                        .filter(Objects::nonNull)
                        .filter(location -> location.equals(hit.getLocation()))
                        .forEach(location -> deployable.damage(finalDamage, player)));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerItemDamage(PlayerItemDamageEvent event) {
        final Player player = event.getPlayer();
        if (PlayerHelper.isInGame(player)) {
            event.setCancelled(true);
            event.getItem().setDurability(event.getItem().getType().getMaxDurability());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().teleport(Bukkit.getWorlds().get(0).getSpawnLocation().clone().add(0.5, 0, 0.5));
    }
}
