package me.koenn.kingdomwars.grenade;

import me.koenn.kingdomwars.util.NBTUtil;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.jnbt.CompoundTag;
import org.jnbt.StringTag;

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

        final Player player = (Player) event.getEntity().getShooter();
        final ItemStack snowball = player.getInventory().getItemInMainHand();

        CompoundTag grenadeInfo = GrenadeHelper.getGrenade(snowball);
        if (grenadeInfo == null) {
            return;
        }

        final BaseGrenade grenade = new BaseGrenade(NBTUtil.getChildTag(grenadeInfo.getValue(), "name", StringTag.class).getValue(), grenadeInfo);
        grenade.setProjectile((Snowball) event.getEntity());
        grenade.start();
    }
}
