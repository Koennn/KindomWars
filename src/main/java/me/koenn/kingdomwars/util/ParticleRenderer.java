package me.koenn.kingdomwars.util;

import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.effect.LineEffect;
import de.slikey.effectlib.util.DynamicLocation;
import de.slikey.effectlib.util.ParticleEffect;
import me.koenn.kingdomwars.KingdomWars;
import org.bukkit.Location;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential Written by Koen Willemse, April 2017
 */
public final class ParticleRenderer {

    public static void renderLine(Location pos1, Location pos2) {
        renderLine(ParticleEffect.FLAME, 30, pos1, pos2);
    }

    public static void renderLine(ParticleEffect effect, int particles, Location pos1, Location pos2) {
        createLineEffect(effect, particles, pos1, pos2);
    }

    private static void createLineEffect(ParticleEffect particleEffect, int particles, Location pos1, Location pos2) {
        final LineEffect lineEffect = new LineEffect(new EffectManager(KingdomWars.getInstance()));
        lineEffect.particle = particleEffect;
        lineEffect.setDynamicOrigin(new DynamicLocation(pos1));
        lineEffect.setDynamicTarget(new DynamicLocation(pos2));
        lineEffect.iterations = 1;
        lineEffect.particles = particles;
        lineEffect.duration = 2;
        lineEffect.start();
    }
}
