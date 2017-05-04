package me.koenn.kingdomwars.effect;

import de.slikey.effectlib.Effect;
import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.EffectType;
import de.slikey.effectlib.util.DynamicLocation;
import de.slikey.effectlib.util.ParticleEffect;
import de.slikey.effectlib.util.RandomUtils;
import org.bukkit.Location;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, May 2017
 */
public class GrenadeEffect extends Effect {

    public ParticleEffect particle;
    public int height;

    public GrenadeEffect(EffectManager effectManager, DynamicLocation location) {
        super(effectManager);
        this.particle = ParticleEffect.SMOKE_NORMAL;
        this.height = 5;
        this.type = EffectType.REPEATING;
        this.period = 1;
        this.iterations = 300;

        this.setDynamicOrigin(location);
        this.setDynamicTarget(location);
    }

    @Override
    public void onRun() {
        Location location = this.getLocation();

        for (int i = 0; i < 5; ++i) {
            location.add(RandomUtils.getRandomCircleVector().multiply(RandomUtils.random.nextDouble() * 0.2D));
            location.add(0.0D, (double) (RandomUtils.random.nextFloat() * 0.6F), 0.0D);
            this.display(this.particle, location);
        }
    }
}
