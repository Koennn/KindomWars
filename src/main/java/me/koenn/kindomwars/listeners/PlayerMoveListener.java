package me.koenn.kindomwars.listeners;

import me.koenn.kindomwars.game.Game;
import me.koenn.kindomwars.game.GamePhase;
import me.koenn.kindomwars.util.PlayerHelper;
import me.koenn.kindomwars.util.Team;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public class PlayerMoveListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!PlayerHelper.isInGame(player)) {
            return;
        }

        Game game = PlayerHelper.getGame(player);
        if (game == null) {
            return;
        }

        if (game.getCurrentPhase().equals(GamePhase.STARTED)) {
            return;
        }

        int border = (PlayerHelper.getTeam(player) == Team.BLUE ? game.getMap().getBlueXBorder() : game.getMap().getRedXBorder());
        if (Math.round(event.getTo().getX()) == border) {
            event.setTo(event.getFrom());
        }
    }
}
