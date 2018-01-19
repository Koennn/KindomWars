package me.koenn.kingdomwars.effect;

import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.effect.AnimatedBallEffect;
import de.slikey.effectlib.util.ParticleEffect;
import me.koenn.kingdomwars.KingdomWars;


public class DisabledEffect extends AnimatedBallEffect {

    public DisabledEffect() {
        super(new EffectManager(KingdomWars.getInstance()));
        this.particle = ParticleEffect.CRIT_MAGIC;
        this.yFactor = 0.8F;
        this.size = 1.2F;
    }
}
