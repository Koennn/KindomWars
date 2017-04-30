package me.koenn.kindomwars.game;

import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.effect.CloudEffect;
import de.slikey.effectlib.util.DynamicLocation;
import de.slikey.effectlib.util.ParticleEffect;
import me.koenn.kindomwars.KingdomWars;
import me.koenn.kindomwars.util.ParticleRenderer;
import me.koenn.kindomwars.util.Team;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.*;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public class Map {

    public static List<Map> maps = new ArrayList<>();

    private final String name;
    private final Location blueSpawn;
    private final Location redSpawn;
    private final int blueXBorder;
    private final int blueZBorder;
    private final int redXBorder;
    private final int redZBorder;
    private final Location[] redCapturePointCorners;
    private final Location[] blueCapturePointCorners;
    private final ControlPoint[] controlPoints = new ControlPoint[2];
    private final HashMap<String, Object> properties;
    private int renderTask;

    public Map(String name, Location blueSpawn, Location redSpawn, int blueXBorder, int blueZBorder, int redXBorder, int redZBorder, Location[] redCapturePointCorners, Location[] blueCapturePointCorners, HashMap<String, Object> properties) {
        this.name = name;
        this.blueSpawn = blueSpawn;
        this.redSpawn = redSpawn;
        this.blueXBorder = blueXBorder;
        this.blueZBorder = blueZBorder;
        this.redXBorder = redXBorder;
        this.redZBorder = redZBorder;
        this.redCapturePointCorners = redCapturePointCorners;
        this.blueCapturePointCorners = blueCapturePointCorners;
        this.controlPoints[Team.BLUE.getIndex()] = new ControlPoint(blueCapturePointCorners, Team.BLUE);
        this.controlPoints[Team.RED.getIndex()] = new ControlPoint(redCapturePointCorners, Team.RED);
        this.properties = properties;
    }

    public static Map getRandomMap() {
        Bukkit.getLogger().info(Arrays.toString(maps.toArray()));
        return maps.get(new Random().nextInt(maps.size()));
    }

    public void reset() {
        for (ControlPoint controlPoint : this.controlPoints) {
            controlPoint.reset();
        }
    }

    public void load(Game game) {
        //TODO: This is fucking laggy, needs fix!
        /*for (Chunk chunk : blueSpawn.getWorld().getLoadedChunks()) {
            for (int x = 0; x < 16; x++) {
                for (int y = 40; y < 256; y++) {
                    for (int z = 0; z < 16; z++) {
                        Block block = chunk.getBlock(x, y, z);
                        if (block.getType().equals(Material.WOOL) || block.getType().equals(Material.STAINED_CLAY)) {
                            for (Player player : game.getTeamBlue()) {
                                FakeBlockAPI.fakeBlockRegistry.register(new FakeBlock(block.getLocation(), block.getType(), (short) 3, player));
                            }
                            for (Player player : game.getTeamRed()) {
                                FakeBlockAPI.fakeBlockRegistry.register(new FakeBlock(block.getLocation(), block.getType(), (short) 14, player));
                            }
                        }
                    }
                }
            }
        }*/
    }

    public void renderCapture(Team team) {
        for (Location location : team == Team.RED ? redCapturePointCorners : blueCapturePointCorners) {
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
            ParticleRenderer.renderLine(redCapturePointCorners[0], redCapturePointCorners[1], true);
            ParticleRenderer.renderLine(redCapturePointCorners[1], redCapturePointCorners[2], true);
            ParticleRenderer.renderLine(redCapturePointCorners[2], redCapturePointCorners[3], true);
            ParticleRenderer.renderLine(redCapturePointCorners[3], redCapturePointCorners[0], true);

            ParticleRenderer.renderLine(blueCapturePointCorners[0], blueCapturePointCorners[1], false);
            ParticleRenderer.renderLine(blueCapturePointCorners[1], blueCapturePointCorners[2], false);
            ParticleRenderer.renderLine(blueCapturePointCorners[2], blueCapturePointCorners[3], false);
            ParticleRenderer.renderLine(blueCapturePointCorners[3], blueCapturePointCorners[0], false);
        }, 0, 2);
    }

    public void stopRendering() {
        Bukkit.getScheduler().cancelTask(this.renderTask);
    }

    public Location getBlueSpawn() {
        return blueSpawn;
    }

    public Location getRedSpawn() {
        return redSpawn;
    }

    public int getBlueXBorder() {
        return blueXBorder;
    }

    public int getRedXBorder() {
        return redXBorder;
    }

    public int getBlueZBorder() {
        return blueZBorder;
    }

    public int getRedZBorder() {
        return redZBorder;
    }

    public ControlPoint[] getControlPoints() {
        return controlPoints;
    }

    public String getName() {
        return name;
    }

    public Object getProperty(String name) {
        return this.properties.get(name);
    }

    public Location getSpawn(Team team) {
        return team == Team.RED ? redSpawn : blueSpawn;
    }
}
