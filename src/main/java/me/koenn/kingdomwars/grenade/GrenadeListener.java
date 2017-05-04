package me.koenn.kingdomwars.grenade;

import me.koenn.kingdomwars.grenade.grenades.FragGrenade;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public class GrenadeListener implements Listener {

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (!event.getEntityType().equals(EntityType.SNOWBALL) || !(event.getEntity().getShooter() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity().getShooter();
        ItemStack snowball = player.getItemInHand();

        BaseGrenade grenade = new FragGrenade();
        BaseGrenade.grenadeRegistry.register(grenade);
        Bukkit.getLogger().info(grenade.getType());

        grenade.setProjectile((Snowball) event.getEntity());
        grenade.start();
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!event.getEntityType().equals(EntityType.SNOWBALL) || !(event.getEntity().getShooter() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity().getShooter();


    }
}
