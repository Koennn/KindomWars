package me.koenn.kindomwars.game;

import me.koenn.core.misc.ActionBar;
import me.koenn.core.misc.ProgressBar;
import me.koenn.kindomwars.KingdomWars;
import me.koenn.kindomwars.util.PlayerHelper;
import me.koenn.kindomwars.util.Team;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

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
    public int captureProgress = 0;
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
    }

    public void showProgressToPlayers(Game game) {
        ProgressBar progressBar = new ProgressBar(60);
        String progressString = progressBar.get(this.captureProgress);
        for (Player player : game.getPlayers()) {
            if (isInRange(player)) {
                new ActionBar(progressString, KingdomWars.getInstance()).setStay(1).send(player);
            }
        }
    }

    public Team getCurrentlyCapturing(Game game) {
        int owningTeam = 0, opposingTeam = 0;
        for (Player player : game.getPlayers()) {
            if (isInRange(player)) {
                if (PlayerHelper.getTeam(player) == this.owningTeam) {
                    owningTeam++;
                } else {
                    opposingTeam++;
                }
            }
        }
        return opposingTeam > owningTeam ? this.owningTeam.getOpponent() : this.owningTeam;
    }

    public boolean isEmpty(Game game) {
        for (Player player : game.getPlayers()) {
            if (isInRange(player)) {
                return false;
            }
        }
        return true;
    }

    public boolean isInRange(Player player) {
        Location location = player.getLocation();
        return location.getX() > min.getX() && location.getX() < max.getX() && location.getZ() > min.getZ() && location.getZ() < max.getZ() && isInYRange(location, min.getY());
    }

    private boolean isInYRange(Location location, double y) {
        return location.getY() > y - 1 && location.getY() < y + 1;
    }

    public void updateCaptureProgress(Team capturing) {
        if (capturing == this.owningTeam) {
            if (this.captureProgress > 0) {
                this.captureProgress--;
            }
        } else {
            if (this.captureProgress < 100) {
                this.captureProgress++;
            }
        }
    }

    public void reset() {
        this.captureProgress = 0;
    }
}
