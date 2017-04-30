package me.koenn.kindomwars.util;

import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.effect.LineEffect;
import de.slikey.effectlib.util.DynamicLocation;
import de.slikey.effectlib.util.ParticleEffect;
import me.koenn.kindomwars.KingdomWars;
import org.bukkit.Color;
import org.bukkit.Location;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public final class ParticleRenderer {

    public static void renderLine(Location pos1, Location pos2, boolean red) {
        LineEffect lineEffect = new LineEffect(new EffectManager(KingdomWars.getInstance()));
        lineEffect.particle = red ? ParticleEffect.DRIP_LAVA : ParticleEffect.DRIP_WATER;
        lineEffect.setDynamicOrigin(new DynamicLocation(pos1));
        lineEffect.setDynamicTarget(new DynamicLocation(pos2));
        lineEffect.iterations = 1;
        lineEffect.particles = 40;
        lineEffect.duration = 2;
        lineEffect.color = Color.BLUE;
        lineEffect.start();
    }
}
