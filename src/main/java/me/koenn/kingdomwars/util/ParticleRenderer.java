package me.koenn.kingdomwars.util;

import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.effect.LineEffect;
import de.slikey.effectlib.util.DynamicLocation;
import de.slikey.effectlib.util.ParticleEffect;
import me.koenn.kingdomwars.KingdomWars;
import me.koenn.kingdomwars.game.Game;
import org.bukkit.Location;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential Written by Koen Willemse, April 2017
 */
public final class ParticleRenderer {

    public static void renderLine(Location pos1, Location pos2, boolean red, Game game) {
        createLineEffect(ParticleEffect.FLAME, pos1, pos2);
    }

    private static void createLineEffect(ParticleEffect particleEffect, Location pos1, Location pos2) {
        final LineEffect lineEffect = new LineEffect(new EffectManager(KingdomWars.getInstance()));
        lineEffect.particle = particleEffect;
        lineEffect.setDynamicOrigin(new DynamicLocation(pos1));
        lineEffect.setDynamicTarget(new DynamicLocation(pos2));
        lineEffect.iterations = 1;
        lineEffect.particles = 30;
        lineEffect.duration = 2;
        lineEffect.start();
    }
}
