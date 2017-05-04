package me.koenn.kingdomwars.grenade.grenades;

import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.util.DynamicLocation;
import me.koenn.core.misc.Timer;
import me.koenn.kingdomwars.KingdomWars;
import me.koenn.kingdomwars.effect.GrenadeEffect;
import me.koenn.kingdomwars.grenade.BaseGrenade;
import me.koenn.kingdomwars.grenade.GrenadeExecutor;
import org.bukkit.Location;

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
            private GrenadeEffect effect;
            private DynamicLocation grenade;

            @Override
            public void start() {
                this.grenade = new DynamicLocation(getProjectile());

                this.effect = new GrenadeEffect(new EffectManager(KingdomWars.getInstance()), this.grenade);
                this.effect.infinite();
                this.effect.start();

                new Timer(60, KingdomWars.getInstance()).start(() -> {
                    Location location = getProjectile().getLocation();
                    getProjectile().getWorld().createExplosion(location.getX(), location.getY(), location.getZ(), 4, false, false);

                    if (!getProjectile().isDead()) {
                        getProjectile().remove();
                    }

                    this.effect.cancel();
                });
            }

            @Override
            public void land() {

            }
        });
    }


}
