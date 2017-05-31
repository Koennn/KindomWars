package me.koenn.kingdomwars.listeners;

import me.koenn.kingdomwars.game.GameCreator;
import me.koenn.kingdomwars.util.PlayerHelper;
import me.koenn.kingdomwars.util.References;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public class SignListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (PlayerHelper.isInGame(player)) {
            return;
        }

        Block clicked = event.getClickedBlock();
        if (clicked == null) {
            return;
        }
        if (!clicked.getType().equals(Material.WALL_SIGN) && !clicked.getType().equals(Material.SIGN_POST)) {
            return;
        }

        Sign sign = (Sign) clicked.getState();
        if (!sign.getLine(0).contains(References.SIGN_PREFIX)) {
            return;
        }

        GameCreator.instance.signClick(sign, player);
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Sign sign = (Sign) event.getBlock().getState();
        if (!event.getLine(0).contains("[KingdomWars]") || !event.getPlayer().isOp()) {
            return;
        }

        GameCreator.instance.registerSign(sign, event.getLine(1));
        event.setCancelled(true);
    }
}
