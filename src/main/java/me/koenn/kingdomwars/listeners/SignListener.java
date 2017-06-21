package me.koenn.kingdomwars.listeners;

import de.slikey.effectlib.Effect;
import de.slikey.effectlib.effect.AnimatedBallEffect;
import de.slikey.effectlib.util.DynamicLocation;
import de.slikey.effectlib.util.ParticleEffect;
import me.koenn.core.misc.EffectBuilder;
import me.koenn.kingdomwars.KingdomWars;
import me.koenn.kingdomwars.game.GameCreator;
import me.koenn.kingdomwars.util.PlayerHelper;
import me.koenn.kingdomwars.util.References;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public class SignListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (PlayerHelper.isInGame(player)) {
            return;
        }

        Block clicked = event.getClickedBlock();
        if (clicked == null) {
            return;
        }
        if (!clicked.getType().equals(Material.WALL_SIGN) && !clicked.getType().equals(Material.SIGN_POST)) {
            return;
        }

        Sign sign = (Sign) clicked.getState();
        if (!sign.getLine(0).contains(References.SIGN_PREFIX)) {
            return;
        }

        GameCreator.instance.signClick(sign, player);
        Effect effect = new EffectBuilder(AnimatedBallEffect.class, KingdomWars.getInstance())
                .particleEffect(ParticleEffect.FIREWORKS_SPARK)
                .iterations(5)
                .speed(0.1F)
                .property("yFactor", 1.0F)
                .property("yOffset", -1.0F)
                .location(new DynamicLocation(player))
                .build();
        effect.start();
        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Sign sign = (Sign) event.getBlock().getState();
        if (!event.getLine(0).contains("[KingdomWars]") || !event.getPlayer().isOp()) {
            return;
        }

        GameCreator.instance.registerSign(sign, event.getLine(1));
        event.setCancelled(true);
    }
}
