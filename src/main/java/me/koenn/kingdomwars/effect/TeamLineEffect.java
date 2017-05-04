package me.koenn.kingdomwars.effect;

import de.slikey.effectlib.Effect;
import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.EffectType;
import de.slikey.effectlib.util.ParticleEffect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, May 2017
 */
public class TeamLineEffect extends Effect {

    public ParticleEffect particle;
    public boolean isZigZag;
    public int zigZags;
    public Vector zigZagOffset;
    public int particles;
    public double length;
    protected boolean zag;
    protected int step;
    protected List<Player> team;

    public TeamLineEffect(EffectManager effectManager, List<Player> team) {
        super(effectManager);
        this.particle = ParticleEffect.FLAME;
        this.isZigZag = false;
        this.zigZags = 10;
        this.zigZagOffset = new Vector(0.0D, 0.1D, 0.0D);
        this.particles = 100;
        this.length = 0.0D;
        this.zag = false;
        this.step = 0;
        this.type = EffectType.REPEATING;
        this.period = 1;
        this.iterations = 1;
        this.team = team;
    }

    @Override
    public void onRun() {
        Location location = this.getLocation();
        Location target;
        if (this.length > 0.0D) {
            target = location.clone().add(location.getDirection().normalize().multiply(this.length));
        } else {
            target = this.getTarget();
        }

        double amount = (double) (this.particles / this.zigZags);
        if (target == null) {
            this.cancel();
        } else {
            Vector link = target.toVector().subtract(location.toVector());
            float length = (float) link.length();
            link.normalize();
            float ratio = length / (float) this.particles;
            Vector v = link.multiply(ratio);
            Location loc = location.clone().subtract(v);

            for (int i = 0; i < this.particles; ++i) {
                if (this.isZigZag) {
                    if (this.zag) {
                        loc.add(this.zigZagOffset);
                    } else {
                        loc.subtract(this.zigZagOffset);
                    }
                }

                if ((double) this.step >= amount) {
                    this.zag = !this.zag;
                    this.step = 0;
                }

                ++this.step;
                loc.add(v);
                (new ParticleEffect.ParticlePacket(this.particle, (float) this.particleOffsetX, (float) this.particleOffsetY, (float) this.particleOffsetZ, this.speed, this.particleCount, true, null)).sendTo(loc, this.team);
            }

        }
    }
}
