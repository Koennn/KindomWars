package me.koenn.kingdomwars.effect;

import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.effect.SphereEffect;
import de.slikey.effectlib.util.ParticleEffect;
import me.koenn.kingdomwars.KingdomWars;

public class EMPEffect extends SphereEffect {

    public EMPEffect() {
        super(new EffectManager(KingdomWars.getInstance()));
        this.particle = ParticleEffect.CRIT_MAGIC;
        this.period = 1;
        this.radiusIncrease = 0.3F;
        this.iterations = 20;
        this.particles = 80;
    }

    @Override
    public void onRun() {
        super.onRun();
    }
}
