package me.koenn.kindomwars.grenade.grenades;

import me.koenn.core.misc.Timer;
import me.koenn.kindomwars.KingdomWars;
import me.koenn.kindomwars.grenade.BaseGrenade;
import me.koenn.kindomwars.grenade.GrenadeExecutor;
import org.bukkit.Bukkit;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public class FragGrenade extends BaseGrenade {

    public FragGrenade() {
        super("Frag Grenade");
        this.setExecutor(new GrenadeExecutor() {
            @Override
            public void start() {
                Bukkit.getLogger().info("Start");
                new Timer(60, KingdomWars.getInstance()).start(() -> {
                    //getProjectile().getWorld().createExplosion(getProjectile().getLocation(), 4, false);
                });
            }

            @Override
            public void land() {

            }
        });
    }


}
