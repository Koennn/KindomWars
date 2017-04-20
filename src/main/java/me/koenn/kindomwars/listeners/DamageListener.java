package me.koenn.kindomwars.listeners;

import me.koenn.kindomwars.util.Messager;
import me.koenn.kindomwars.util.PlayerHelper;
import me.koenn.kindomwars.util.References;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public class DamageListener implements Listener {

    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) {
            return;
        }

        Player damager = (Player) event.getDamager();
        Player damaged = (Player) event.getEntity();

        if (!PlayerHelper.isInGame(damager) || !PlayerHelper.isInGame(damaged)) {
            return;
        }

        if (!PlayerHelper.canDamage(damager, damaged)) {
            event.setCancelled(true);
            Messager.playerMessage(damager, References.DONT_SHOOT_ALLY);
        }
    }
}
