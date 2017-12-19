package me.koenn.kingdomwars.game;

import me.koenn.core.misc.ActionBar;
import me.koenn.core.misc.ProgressBar;
import me.koenn.kingdomwars.KingdomWars;
import me.koenn.kingdomwars.util.PlayerHelper;
import me.koenn.kingdomwars.util.Team;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
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
    private Vector min;
    private Vector max;
    private boolean frozen = false;
    private int cooldown = 0;

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

        this.min = Vector.getMinimum(edges[0].toVector(), edges[1].toVector());
        this.max = Vector.getMaximum(edges[0].toVector(), edges[1].toVector());
    }

    public void showProgressToPlayers(Game game) {
        final String progressString = new ProgressBar(60).get(this.captureProgress);
        final ActionBar actionBar = new ActionBar(progressString, KingdomWars.getInstance()).setStay(1);
        final float pitch = calculateScaledProgress(this.captureProgress, 1.0F) + 0.5F;

        if (this.frozen) {
            game.getPlayers().stream().filter(this::isInRange).forEach(actionBar::send);
        } else {
            game.getPlayers().stream().filter(this::isInRange).forEach(player -> {
                actionBar.send(player);
                if (this.cooldown == 0) {
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, pitch);
                }
            });
        }

        if (this.cooldown > 0) {
            this.cooldown--;
        } else if (this.cooldown == 0) {
            this.cooldown = 10 - Math.round(this.captureProgress / 10);
        }
    }

    private float calculateScaledProgress(float current, float maxSize) {
        return current < 100.0F ? (current > 0.0F ? current * maxSize / 100.0F : 0.0F) : maxSize;
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

    public boolean isNeutral(Game game) {
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
        return owningTeam == opposingTeam;
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
        final Location location = player.getLocation();
        return isInRange(location.getX(), min.getX(), max.getX()) && isInRange(location.getZ(), min.getZ(), max.getZ()) && isInYRange(location, min.getY());
    }

    private boolean isInRange(double coord, double min, double max) {
        return coord > min && coord < max;
    }

    private boolean isInYRange(Location location, double y) {
        return location.getY() > y - 1 && location.getY() < y + 1;
    }

    public void updateCaptureProgress(Game game, Team capturing) {
        if (this.frozen) {
            return;
        }
        if (this.isNeutral(game) && !this.isEmpty(game)) {
            return;
        }
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

    public void reset(Game game) {
        game.freezePoints();
        this.captureProgress = 0;
        Bukkit.getScheduler().scheduleSyncDelayedTask(KingdomWars.getInstance(), game::unFreezePoints, 25);
    }

    public void forceReset(Game game) {
        this.captureProgress = 0;
        game.unFreezePoints();
    }

    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
    }
}
