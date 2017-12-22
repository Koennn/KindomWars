package me.koenn.kingdomwars.listeners;

import me.koenn.kingdomwars.game.Game;
import me.koenn.kingdomwars.util.PlayerHelper;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;

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
        game.getDeployables().forEach(deployable -> deployable.getDeployableBlocks().stream().filter(location -> location.equals(clicked.getLocation())).forEach(location -> deployable.damage(5, player)));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerItemDamage(PlayerItemDamageEvent event) {
        final Player player = event.getPlayer();
        if (PlayerHelper.isInGame(player)) {
            event.setCancelled(true);
        }
    }
}
