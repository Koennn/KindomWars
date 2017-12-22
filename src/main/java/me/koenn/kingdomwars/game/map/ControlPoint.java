package me.koenn.kingdomwars.game.map;

import me.koenn.core.misc.ActionBar;
import me.koenn.core.misc.LocationHelper;
import me.koenn.core.misc.ProgressBar;
import me.koenn.kingdomwars.KingdomWars;
import me.koenn.kingdomwars.game.Game;
import me.koenn.kingdomwars.tracker.PointTracker;
import me.koenn.kingdomwars.util.JSONSerializable;
import me.koenn.kingdomwars.util.PlayerHelper;
import me.koenn.kingdomwars.util.Team;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public class ControlPoint implements JSONSerializable {

    private final Vector min;
    private final Vector max;
    private final PointTracker tracker;

    public Location[] corners;
    public Team owningTeam;
    public int captureProgress = 0;
    private boolean frozen = false;
    private int cooldown = 0;

    public ControlPoint(JSONObject json) {
        this.owningTeam = Team.valueOf((String) json.get("owningTeam"));
        JSONArray corners = (JSONArray) json.get("corners");
        this.corners = new Location[corners.size()];
        for (int i = 0; i < corners.size(); i++) {
            this.corners[i] = LocationHelper.fromString((String) corners.get(i)).add(0.5, 0.5, 0.5);
        }

        Location[] edges = new Location[2];
        edges[0] = this.corners[0];
        for (Location corner : this.corners) {
            if (corner.getX() != edges[0].getX() && corner.getZ() != edges[0].getZ()) {
                edges[1] = corner;
            }
        }

        this.min = Vector.getMinimum(edges[0].toVector(), edges[1].toVector());
        this.max = Vector.getMaximum(edges[0].toVector(), edges[1].toVector());

        this.tracker = new PointTracker(this);
    }

    public void load(Game game) {
        this.tracker.enable(game);
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("team", this.owningTeam.name());
        JSONArray corners = new JSONArray();
        for (Location corner : this.corners) {
            corners.add(LocationHelper.getString(corner));
        }
        json.put("corners", corners);
        return json;
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
        return isInRange(location.getX(), min.getX(), max.getX()) &&
                isInRange(location.getZ(), min.getZ(), max.getZ()) &&
                isInYRange(location, min.getY()) &&
                player.getGameMode().equals(GameMode.SURVIVAL);
    }

    public List<Player>[] getPlayersOnPoint(Game game) {
        List<Player>[] players = new List[2];
        for (Team team : Team.values()) {
            players[team.getIndex()] = new ArrayList<>();
            game.getTeam(team).stream().filter(this::isInRange).filter(player -> player.getGameMode().equals(GameMode.SURVIVAL)).forEach(player -> players[team.getIndex()].add(player));
        }
        return players;
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
