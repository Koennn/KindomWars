package me.koenn.kingdomwars.grenade;

import org.bukkit.Bukkit;
import org.bukkit.entity.Snowball;
import org.jnbt.CompoundTag;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public class BaseGrenade {

    public final String type;
    public final CompoundTag grenadeInfo;
    public Snowball projectile;
    public int task;

    protected BaseGrenade(String type, CompoundTag grenadeInfo) {
        this.type = type;
        this.grenadeInfo = grenadeInfo;
    }

    public void start() {
        GrenadeLoader.startGrenadeScript(this);
    }

    @SuppressWarnings("unused")
    public void remove() {
        Bukkit.getScheduler().cancelTask(this.task);
    }

    public void setProjectile(Snowball projectile) {
        this.projectile = projectile;
    }

    public String getType() {
        return type;
    }
}
