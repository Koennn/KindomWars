package me.koenn.kingdomwars.effect;

import de.slikey.effectlib.Effect;
import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.util.ParticleEffect;
import me.koenn.kingdomwars.KingdomWars;

public class GrenadeEffect extends Effect {

    private final ParticleEffect particle;

    public GrenadeEffect(ParticleEffect particle) {
        super(new EffectManager(KingdomWars.getInstance()));
        this.particle = particle;
        this.infinite();
    }

    @Override
    public void onRun() {
        this.display(this.particle, this.getLocation());
    }
}
