package me.koenn.kingdomwars.effect;

import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.EffectType;
import de.slikey.effectlib.effect.AnimatedBallEffect;
import de.slikey.effectlib.util.DynamicLocation;
import de.slikey.effectlib.util.ParticleEffect;

public class SparkEffect extends AnimatedBallEffect {

    public SparkEffect(EffectManager effectManager, DynamicLocation location) {
        super(effectManager);
        this.particle = ParticleEffect.FIREWORKS_SPARK;
        this.type = EffectType.INSTANT;
        this.period = 1;
        this.iterations = 40;
        this.speed = 0.2F;
        this.yOffset = 1.0F;
        this.size = 0.3F;

        this.setDynamicOrigin(location);
        this.setDynamicTarget(location);
    }
}
