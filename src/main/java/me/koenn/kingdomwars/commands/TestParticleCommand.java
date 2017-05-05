package me.koenn.kingdomwars.commands;

import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.effect.AnimatedBallEffect;
import de.slikey.effectlib.effect.LineEffect;
import de.slikey.effectlib.effect.SphereEffect;
import de.slikey.effectlib.util.DynamicLocation;
import de.slikey.effectlib.util.ParticleEffect;
import me.koenn.core.command.Command;
import me.koenn.core.player.CPlayer;
import me.koenn.kingdomwars.KingdomWars;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public class TestParticleCommand extends Command {

    public TestParticleCommand() {
        super("testparticle", "/testparticle");
    }

    @Override
    public boolean execute(CPlayer cPlayer, String[] strings) {
        DynamicLocation location = new DynamicLocation(new Location(Bukkit.getWorld("world"), 0.5, 72.0, 0.5));
        DynamicLocation location2 = new DynamicLocation(new Location(Bukkit.getWorld("world"), 0.5, 72.0, 0.5));
        DynamicLocation location3 = new DynamicLocation(new Location(Bukkit.getWorld("world"), 0.5, 72.0, 0.5));
        DynamicLocation location4 = new DynamicLocation(new Location(Bukkit.getWorld("world"), 0.5, 72.0, 0.5));
        DynamicLocation location5 = new DynamicLocation(new Location(Bukkit.getWorld("world"), 0.5, 72.0, 0.5));
        DynamicLocation location6 = new DynamicLocation(new Location(Bukkit.getWorld("world"), 0.5, 72.0, 0.5));

        DynamicLocation corner1 = new DynamicLocation(new Location(Bukkit.getWorld("world"), 28.5, 90.0, -28.5));
        DynamicLocation corner2 = new DynamicLocation(new Location(Bukkit.getWorld("world"), -28.5, 90.0, -28.5));
        DynamicLocation corner3 = new DynamicLocation(new Location(Bukkit.getWorld("world"), -28.5, 90.0, 28.5));
        DynamicLocation corner4 = new DynamicLocation(new Location(Bukkit.getWorld("world"), 28.5, 90.0, 28.5));

        SphereEffect sphere = new SphereEffect(new EffectManager(KingdomWars.getInstance()));
        sphere.setDynamicTarget(location);
        sphere.setDynamicOrigin(location);
        sphere.color = Color.AQUA;
        sphere.radius = 1.2F;
        sphere.particles = 60;
        sphere.infinite();
        sphere.start();

        AnimatedBallEffect effect = new AnimatedBallEffect(new EffectManager(KingdomWars.getInstance()));
        effect.setDynamicOrigin(location2);
        effect.setDynamicTarget(location2);
        effect.particle = ParticleEffect.SPELL_WITCH;
        effect.size = 3.0F;
        effect.xFactor = 2F;
        effect.yFactor = 2F;
        effect.zFactor = 2F;
        effect.particlesPerIteration = 300;
        effect.infinite();
        effect.start();

        LineEffect line1 = new LineEffect(new EffectManager(KingdomWars.getInstance()));
        line1.setDynamicOrigin(corner1);
        line1.setDynamicTarget(location3);
        line1.infinite();
        line1.start();

        LineEffect line2 = new LineEffect(new EffectManager(KingdomWars.getInstance()));
        line2.setDynamicOrigin(corner2);
        line2.setDynamicTarget(location4);
        line2.infinite();
        line2.start();

        LineEffect line3 = new LineEffect(new EffectManager(KingdomWars.getInstance()));
        line3.setDynamicOrigin(corner3);
        line3.setDynamicTarget(location5);
        line3.infinite();
        line3.start();

        LineEffect line4 = new LineEffect(new EffectManager(KingdomWars.getInstance()));
        line4.setDynamicOrigin(corner4);
        line4.setDynamicTarget(location6);
        line4.infinite();
        line4.start();

        /*Bukkit.getScheduler().scheduleSyncRepeatingTask(KingdomWars.getInstance(), () -> {
            effect2.pitchOffset += 1;
            effect2.yawOffset -= 1;

            if (effect2.pitchOffset == 360) {
                effect2.pitchOffset = 0;
            }

            if (effect2.yawOffset == -360) {
                effect2.yawOffset = 0;
            }
        }, 0, 10);*/
        return true;
    }
}
