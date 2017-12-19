package me.koenn.kingdomwars.util;

import me.koenn.core.misc.Timer;
import me.koenn.kingdomwars.KingdomWars;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential Written by Koen Willemse, September 2017
 */
public final class FireworkHelper {

    public static void endGameFireworks(Player player) {
        Random random = ThreadLocalRandom.current();

        for (int i = 0; i < 5; i++) {
            new Timer(i * 5, KingdomWars.getInstance()).start(() -> {
                Firework firework = player.getWorld().spawn(player.getLocation(), Firework.class);
                FireworkMeta meta = firework.getFireworkMeta();
                meta.addEffect(FireworkEffect.builder()
                        .with(FireworkEffect.Type.values()[random.nextInt(FireworkEffect.Type.values().length)])
                        .withColor(Color.fromRGB(random.nextInt(255), random.nextInt(255), random.nextInt(255)))
                        .build()
                );
                firework.setVelocity(new Vector(0, 0.5, 0));
                firework.setFireworkMeta(meta);
            });
        }
    }
}
