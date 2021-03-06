package me.koenn.kingdomwars.game.map;

import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.effect.CloudEffect;
import de.slikey.effectlib.util.DynamicLocation;
import de.slikey.effectlib.util.ParticleEffect;
import me.koenn.core.misc.LocationHelper;
import me.koenn.core.registry.Registry;
import me.koenn.kingdomwars.KingdomWars;
import me.koenn.kingdomwars.game.Game;
import me.koenn.kingdomwars.util.ParticleRenderer;
import me.koenn.kingdomwars.util.Team;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential Written by Koen Willemse, April 2017
 */
public class Map implements Listener {

    public static final Registry<Map> maps = new Registry<>(Map::getName);

    private final String name;
    private final HashMap<String, Object> properties;
    private final Location[] spawns = new Location[2];
    private final Door[] doors = new Door[2];
    private final ControlPoint[] points = new ControlPoint[2];
    private List<MedKit> medkits = new ArrayList<>();
    private List<SpeedPack> speedPacks = new ArrayList<>();
    private List<JumpPad> jumpPads = new ArrayList<>();
    private int renderTask;

    public Map(JSONObject json) {
        JSONObject mapJson = (JSONObject) json.get("map");

        this.name = (String) mapJson.get("name");
        this.properties = (JSONObject) mapJson.get("properties");

        for (Team team : Team.values()) {
            this.spawns[team.getIndex()] = LocationHelper.fromString((String) ((JSONObject) mapJson.get("spawns")).get(team.name()));
            this.doors[team.getIndex()] = new Door((JSONObject) ((JSONObject) mapJson.get("doors")).get(team.name()));
            this.points[team.getIndex()] = new ControlPoint((JSONObject) ((JSONObject) mapJson.get("points")).get(team.name()));
        }

        if (mapJson.containsKey("medkits")) {
            JSONArray medkits = (JSONArray) mapJson.get("medkits");
            medkits.forEach(medKit -> this.medkits.add(new MedKit(LocationHelper.fromString(String.valueOf(medKit)))));
        }

        if (mapJson.containsKey("speedPacks")) {
            JSONArray speedPacks = (JSONArray) mapJson.get("speedPacks");
            speedPacks.forEach(speedPack -> this.speedPacks.add(new SpeedPack(LocationHelper.fromString(String.valueOf(speedPack)))));
        }

        if (mapJson.containsKey("jumpPads")) {
            JSONArray jumpPads = (JSONArray) mapJson.get("jumpPads");
            jumpPads.forEach(jumpPad -> this.jumpPads.add(new JumpPad(LocationHelper.fromString(String.valueOf(jumpPad)))));
        }
    }

    public static Map getMap(String name) {
        return maps.get(name);
    }

    public void load(Game game) {
        for (ControlPoint point : this.points) {
            point.load(game);
        }
    }

    public void reset(Game game) {
        for (ControlPoint controlPoint : this.points) {
            controlPoint.forceReset(game);
        }
    }

    public void renderCapture(Team team) {
        for (Location location : this.points[team.getIndex()].corners) {
            CloudEffect effect = new CloudEffect(new EffectManager(KingdomWars.getInstance()));
            DynamicLocation dynamicLocation = new DynamicLocation(location);
            effect.cloudParticle = ParticleEffect.CLOUD;
            effect.mainParticle = ParticleEffect.CLOUD;
            effect.setDynamicOrigin(dynamicLocation);
            effect.setDynamicTarget(dynamicLocation);
            effect.iterations = 5;
            effect.start();
        }
    }

    public void startRendering() {
        this.renderTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(KingdomWars.getInstance(), () -> {
            for (int i = 0; i <= 1; i++) {
                Location[] corners = this.points[i].corners;
                ParticleRenderer.renderLine(corners[0], corners[1]);
                ParticleRenderer.renderLine(corners[1], corners[2]);
                ParticleRenderer.renderLine(corners[2], corners[3]);
                ParticleRenderer.renderLine(corners[3], corners[0]);
            }
        }, 0, 5);
    }

    public void stopRendering() {
        Bukkit.getScheduler().cancelTask(this.renderTask);
    }

    public Location getSpawn(Team team) {
        if (team == null) {
            return this.spawns[Team.RED.getIndex()];
        }
        return this.spawns[team.getIndex()];
    }

    public Door getDoor(Team team) {
        return this.doors[team.getIndex()];
    }

    public ControlPoint[] getPoints() {
        return this.points;
    }

    public String getName() {
        return this.name;
    }

    public Object getProperty(String name) {
        return this.properties.get(name);
    }

    public List<MedKit> getMedkits() {
        return medkits;
    }

    public List<SpeedPack> getSpeedPacks() {
        return speedPacks;
    }

    @Override
    public String toString() {
        return "Map{name=" + this.name + "}";
    }
}
