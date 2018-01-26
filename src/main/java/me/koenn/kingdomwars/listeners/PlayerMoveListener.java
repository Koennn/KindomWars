package me.koenn.kingdomwars.listeners;

import me.koenn.kingdomwars.game.Game;
import me.koenn.kingdomwars.game.GamePhase;
import me.koenn.kingdomwars.game.map.Door;
import me.koenn.kingdomwars.util.PlayerHelper;
import net.minecraft.server.v1_12_R1.EnumParticle;
import net.minecraft.server.v1_12_R1.PacketPlayOutWorldParticles;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

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
        final Player player = event.getPlayer();
        if (!PlayerHelper.isInGame(player)) {
            return;
        }

        final Game game = PlayerHelper.getGame(player.getUniqueId());
        if (game == null) {
            return;
        }

        Door door;
        if (game.getCurrentPhase().equals(GamePhase.STARTING)) {
            door = game.getMap().getDoor(PlayerHelper.getTeam(player));
        } else {
            return;
        }

        final double currentLocation = door.getType() == Door.DoorType.X ? Math.round(event.getTo().getX()) : Math.round(event.getTo().getZ());
        if (currentLocation == Math.round(door.getLocation())) {
            Vector position = event.getFrom().toVector().add(player.getLocation().getDirection().normalize());
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutWorldParticles(EnumParticle.BARRIER, true, (float) position.getX(), (float) position.getY() + 1.8F, (float) position.getZ(), 0, 0, 0, 0, 1));
            event.setTo(event.getFrom());
        }
    }
}
