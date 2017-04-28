package me.koenn.kindomwars.commands;

import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.effect.TornadoEffect;
import de.slikey.effectlib.util.DynamicLocation;
import de.slikey.effectlib.util.ParticleEffect;
import me.koenn.core.command.Command;
import me.koenn.core.player.CPlayer;
import me.koenn.kindomwars.KingdomWars;
import org.bukkit.Bukkit;
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
        TornadoEffect effect = new TornadoEffect(new EffectManager(KingdomWars.getInstance()));
        effect.setDynamicTarget(new DynamicLocation(new Location(Bukkit.getWorld("Build"), -99987.5, 5.0, -83.5)));
        effect.setDynamicOrigin(new DynamicLocation(new Location(Bukkit.getWorld("Build"), -99987.5, 5.0, -83.5)));
        effect.tornadoParticle = ParticleEffect.SPELL_WITCH;
        effect.cloudParticle = ParticleEffect.CLOUD;
        effect.yOffset = -0.8;
        effect.visibleRange = 128.0F;
        effect.infinite();
        effect.start();
        return true;
    }
}
