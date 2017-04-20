package me.koenn.kindomwars.listeners;

import me.koenn.kindomwars.game.Game;
import me.koenn.kindomwars.game.GamePhase;
import me.koenn.kindomwars.util.Messager;
import me.koenn.kindomwars.util.PlayerHelper;
import me.koenn.kindomwars.util.References;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public class DamageListener implements Listener {

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

        if (!PlayerHelper.isInGame(killer) || !PlayerHelper.isInGame(killed)) {
            return;
        }

        Messager.playerMessage(killer, References.KILL);
    }
}
