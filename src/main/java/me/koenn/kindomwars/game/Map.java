package me.koenn.kindomwars.game;

import me.koenn.kindomwars.KingdomWars;
import me.koenn.kindomwars.util.ParticleRenderer;
import me.koenn.kindomwars.util.Team;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

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
    private final ControlPoint blueControlPoint;
    private final ControlPoint redControlPoint;
    private int task;

    public Map(String name, Location blueSpawn, Location redSpawn, int blueXBorder, int blueZBorder, int redXBorder, int redZBorder, Location[] redCapturePointCorners, Location[] blueCapturePointCorners) {
        this.name = name;
        this.blueSpawn = blueSpawn;
        this.redSpawn = redSpawn;
        this.blueXBorder = blueXBorder;
        this.blueZBorder = blueZBorder;
        this.redXBorder = redXBorder;
        this.redZBorder = redZBorder;
        this.redCapturePointCorners = redCapturePointCorners;
        this.blueCapturePointCorners = blueCapturePointCorners;
        this.blueControlPoint = new ControlPoint(blueCapturePointCorners, Team.BLUE);
        this.redControlPoint = new ControlPoint(redCapturePointCorners, Team.RED);
    }

    public static Map getRandomMap() {
        Bukkit.getLogger().info(Arrays.toString(maps.toArray()));
        return maps.get(new Random().nextInt(maps.size()));
    }

    public void startRendering() {
        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(KingdomWars.getInstance(), () -> {
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
        Bukkit.getScheduler().cancelTask(task);
    }

    public void unload() {
        this.blueControlPoint.unload();
        this.redControlPoint.unload();
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

    public ControlPoint getBlueControlPoint() {
        return blueControlPoint;
    }

    public ControlPoint getRedControlPoint() {
        return redControlPoint;
    }

    public String getName() {
        return name;
    }
}
