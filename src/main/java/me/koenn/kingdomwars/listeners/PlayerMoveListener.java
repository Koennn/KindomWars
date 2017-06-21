package me.koenn.kingdomwars.listeners;

import me.koenn.kingdomwars.game.Game;
import me.koenn.kingdomwars.game.GamePhase;
import me.koenn.kingdomwars.util.Door;
import me.koenn.kingdomwars.util.PlayerHelper;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
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

        if (!game.getCurrentPhase().equals(GamePhase.STARTING)) {
            return;
        }

        Door door = game.getMap().getDoor(PlayerHelper.getTeam(player));
        double currentLocation = door.getType() == Door.DoorType.X ? Math.round(event.getTo().getX()) : Math.round(event.getTo().getZ());
        if (currentLocation == Math.round(door.getLocation())) {
            PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.BARRIER, true, (float) event.getFrom().getX(), (float) event.getFrom().getY() + 1.8F, (float) event.getFrom().getZ(), 0, 0, 0, 0, 1);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
            event.setTo(event.getFrom());
        }
    }
}
