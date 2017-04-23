package me.koenn.kindomwars.game;

import me.koenn.kindomwars.util.ActionBar;
import me.koenn.kindomwars.util.PlayerHelper;
import me.koenn.kindomwars.util.References;
import me.koenn.kindomwars.util.Team;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Arrays;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public class ControlPoint {

    public final Location[] corners;
    public final Team owningTeam;
    private Vector min = new Vector(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
    private Vector max = new Vector(0.0, 0.0, 0.0);

    public ControlPoint(Location[] corners, Team owningTeam) {
        this.corners = corners;
        this.owningTeam = owningTeam;
        Location[] edges = new Location[2];
        edges[0] = corners[0];
        for (Location corner : this.corners) {
            if (corner.getX() != edges[0].getX() && corner.getZ() != edges[0].getZ()) {
                edges[1] = corner;
            }
        }
        min = Vector.getMinimum(edges[0].toVector(), edges[1].toVector());
        max = Vector.getMaximum(edges[0].toVector(), edges[1].toVector());
        Bukkit.getLogger().info(owningTeam.name() + " " + Arrays.toString(corners));
        Bukkit.getLogger().info(min.toString());
    }

    public void showProgressToPlayers(Game game, int progress) {
        for (Player player : game.getPlayers()) {
            if (isInRange(player)) {
                new ActionBar(References.CAPTURE_PROGRESS.replace("%progress%", String.valueOf(progress))).setStay(1).send(player);
            }
        }
    }

    public boolean captureNeutral(Game game) {
        int red = 0, blue = 0;
        for (Player player : game.getPlayers()) {
            if (isInRange(player)) {
                if (PlayerHelper.getTeam(player) == Team.RED) {
                    red++;
                } else {
                    blue++;
                }
            }
        }
        return red == blue;
    }

    public Team getCurrentlyCapturing(Game game) {
        int red = 0, blue = 0;
        for (Player player : game.getPlayers()) {
            if (isInRange(player)) {
                if (PlayerHelper.getTeam(player) == Team.RED) {
                    red++;
                } else {
                    blue++;
                }
            }
        }
        return red > blue ? Team.RED : Team.BLUE;
    }

    public boolean isInRange(Player player) {
        Location location = player.getLocation();
        return location.getX() > min.getX() && location.getX() < max.getX() && location.getZ() > min.getZ() && location.getZ() < max.getZ() && isInYRange(location, min.getY());
    }

    private boolean isInYRange(Location location, double y) {
        return location.getY() > y - 1 && location.getY() < y + 1;
    }

    public void unload() {
        /*min.getBlock().setType(Material.AIR);
        max.getBlock().setType(Material.AIR);*/
    }
}
