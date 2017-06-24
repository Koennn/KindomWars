package me.koenn.kingdomwars.listeners;

import de.slikey.effectlib.Effect;
import de.slikey.effectlib.effect.AnimatedBallEffect;
import de.slikey.effectlib.util.DynamicLocation;
import de.slikey.effectlib.util.ParticleEffect;
import me.koenn.core.misc.EffectBuilder;
import me.koenn.core.misc.Timer;
import me.koenn.kingdomwars.KingdomWars;
import me.koenn.kingdomwars.game.Game;
import me.koenn.kingdomwars.game.GamePhase;
import me.koenn.kingdomwars.util.Messager;
import me.koenn.kingdomwars.util.PlayerHelper;
import me.koenn.kingdomwars.util.References;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.HashMap;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public class DamageListener implements Listener {

    private static final HashMap<Player, Integer> respawnCooldown = new HashMap<>();

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) {
            return;
        }

        final Player damager = (Player) event.getDamager();
        final Player damaged = (Player) event.getEntity();

        if (!PlayerHelper.isInGame(damager) || !PlayerHelper.isInGame(damaged)) {
            return;
        }

        final Game damagedGame = PlayerHelper.getGame(damaged);
        if (damagedGame == null || damagedGame == null || damagedGame != PlayerHelper.getGame(damager)) {
            return;
        }

        if (damagedGame.getCurrentPhase() != GamePhase.STARTED) {
            event.setCancelled(true);
            return;
        }

        if (!PlayerHelper.canDamage(damager, damaged)) {
            event.setCancelled(true);
            Messager.playerMessage(damager, References.DONT_HURT_ALLY);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        final Player killed = event.getEntity();
        final Game game = PlayerHelper.getGame(killed);

        if (!PlayerHelper.isInGame(killed) || game == null) {
            return;
        }

        event.setKeepInventory(true);

        respawnCooldown.put(killed, References.RESPAWN_COOLDOWN);
        new Timer(References.RESPAWN_COOLDOWN * 20, KingdomWars.getInstance()).start(() -> {
            respawnCooldown.put(killed, 0);
            killed.setGameMode(GameMode.SURVIVAL);
            killed.teleport(game.getMap().getSpawn(PlayerHelper.getTeam(killed)));
            Messager.playerMessage(killed, References.RESPAWN);
        });

        Messager.playerMessage(killed, References.DEATH);
        Messager.playerTitle(References.DEATH_TITLE, "", killed);

        killed.setGameMode(GameMode.SPECTATOR);
        killed.playSound(killed.getLocation(), Sound.ENDERDRAGON_GROWL, 0.8F, 1.0F);
        Effect effect = new EffectBuilder(AnimatedBallEffect.class, KingdomWars.getInstance())
                .particleEffect(ParticleEffect.FLAME)
                .iterations(5)
                .speed(0.1F)
                .property("yFactor", 1.0F)
                .property("yOffset", -1.0F)
                .location(new DynamicLocation(killed))
                .build();
        effect.start();

        if (event.getEntity().getKiller() == null) {
            return;
        }

        final Player killer = event.getEntity().getKiller();
        if (!PlayerHelper.isInGame(killer)) {
            return;
        }

        Messager.playerMessage(killer, References.KILL);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        final Player player = event.getPlayer();
        if (player == null || !PlayerHelper.isInGame(player)) {
            return;
        }

        if (respawnCooldown.get(player) != 0) {
            player.setGameMode(GameMode.SPECTATOR);

            final Player killer = player.getKiller();
            if (killer != null) {
                event.setRespawnLocation(killer.getEyeLocation());
            }
        }
    }
}
