package me.koenn.kindomwars.grenade;

import me.koenn.core.registry.Registry;
import org.bukkit.Location;
import org.bukkit.entity.Snowball;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public abstract class BaseGrenade {

    public static final Registry<BaseGrenade> grenadeRegistry = new Registry<>(BaseGrenade::getType);

    private final String type;
    private GrenadeExecutor executor;
    private Location landLocation;
    private Snowball projectile;

    protected BaseGrenade(String type) {
        this.type = type;
    }

    public void start() {
        this.executor.start();
    }

    public void setExecutor(GrenadeExecutor executor) {
        this.executor = executor;
    }

    public Location getLandLocation() {
        return landLocation;
    }

    public void setLandLocation(Location landLocation) {
        this.landLocation = landLocation;
    }

    public Snowball getProjectile() {
        return projectile;
    }

    public void setProjectile(Snowball projectile) {
        this.projectile = projectile;
    }

    public String getType() {
        return type;
    }
}
