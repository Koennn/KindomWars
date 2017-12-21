package me.koenn.kingdomwars.mapcreator;

import me.koenn.kingdomwars.util.References;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential Written by Koen Willemse, September 2017
 */
public class MapTool implements Listener {

    private final Player player;

    public MapTool(Player player) {
        this.player = player;
    }

    private void leftClickBlock(PlayerInteractEvent event) {

    }

    private void rightClickBlock(PlayerInteractEvent event) {

    }

    private void leftClickAir(PlayerInteractEvent event) {

    }

    private void rightClickAir(PlayerInteractEvent event) {

    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getPlayer().equals(this.player)) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null || !item.isSimilar(References.MAPSTAFF.getItem())) {
            return;
        }

        switch (event.getAction()) {
            case LEFT_CLICK_BLOCK:
                this.leftClickBlock(event);
                break;
            case RIGHT_CLICK_BLOCK:
                this.rightClickBlock(event);
                break;
            case LEFT_CLICK_AIR:
                this.leftClickAir(event);
                break;
            case RIGHT_CLICK_AIR:
                this.rightClickAir(event);
                break;
        }
    }
}
