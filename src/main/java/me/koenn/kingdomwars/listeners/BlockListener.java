package me.koenn.kingdomwars.listeners;

import me.koenn.kingdomwars.KingdomWars;
import me.koenn.kingdomwars.deployables.Deployable;
import me.koenn.kingdomwars.deployables.DeployableLoader;
import me.koenn.kingdomwars.game.Game;
import me.koenn.kingdomwars.game.GamePhase;
import me.koenn.kingdomwars.util.Messager;
import me.koenn.kingdomwars.util.PlayerHelper;
import me.koenn.kingdomwars.util.References;
import org.bukkit.entity.Player;
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
        final CompoundTag tag = DeployableLoader.getDeployableInfo(event.getItemInHand());
        if (tag == null) {
            return;
        }

        final Player player = event.getPlayer();
        if (!PlayerHelper.isInGame(player)) {
            event.setCancelled(true);
            return;
        }

        final Game game = PlayerHelper.getGame(player);
        if (!game.getCurrentPhase().equals(GamePhase.STARTED)) {
            event.setCancelled(true);
            return;
        }

        try {
            final Deployable deployable = new Deployable(event.getBlockPlaced().getLocation(), tag);
            game.addDeployable(deployable);
            if (!deployable.construct(event.getPlayer())) {
                event.setCancelled(true);
                Messager.playerMessage(player, References.NOT_ENOUGH_SPACE);
            }
        } catch (Exception ex) {
            KingdomWars.getInstance().getLogger().severe("Error while constructing deployable: " + ex);
            ex.printStackTrace();
        }
    }
}
