package me.koenn.kingdomwars.listeners;

import me.koenn.kingdomwars.deployables.Deployable;
import me.koenn.kingdomwars.deployables.DeployableLoader;
import me.koenn.kingdomwars.util.PlayerHelper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.jnbt.CompoundTag;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public class BlockListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!PlayerHelper.isInGame(event.getPlayer())) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        CompoundTag tag = DeployableLoader.getDeployableInfo(event.getItemInHand());
        if (tag == null) {
            return;
        }

        //TODO: Save somewhere for post-game removal.
        new Deployable(event.getBlockPlaced().getLocation(), tag).construct(event.getPlayer());
    }
}
