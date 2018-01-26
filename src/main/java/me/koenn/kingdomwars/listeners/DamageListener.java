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
import me.koenn.kingdomwars.game.events.GameKillEvent;
import me.koenn.kingdomwars.util.Messager;
import me.koenn.kingdomwars.util.PlayerHelper;
import me.koenn.kingdomwars.util.References;
import me.koenn.kingdomwars.util.SoundSystem;
import net.minecraft.server.v1_12_R1.DataWatcherObject;
import net.minecraft.server.v1_12_R1.DataWatcherRegistry;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.HashMap;
import java.util.UUID;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public class DamageListener implements Listener {

    private static final HashMap<UUID, Integer> respawnCooldown = new HashMap<>();

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if (!(event.getDamager() instanceof Player) && !(event.getDamager() instanceof Arrow)) {
            return;
        }

        Player damager;
        if (event.getDamager() instanceof Arrow) {
            if (!(((Arrow) event.getDamager()).getShooter() instanceof Player)) {
                return;
            }

            damager = (Player) ((Arrow) event.getDamager()).getShooter();
        } else {
            damager = (Player) event.getDamager();
        }

        final Player damaged = (Player) event.getEntity();

        ((CraftPlayer) damaged).getHandle().getDataWatcher().set(new DataWatcherObject<>(10, DataWatcherRegistry.b), -1);

        if (!PlayerHelper.isInGame(damager) || !PlayerHelper.isInGame(damaged)) {
            return;
        }

        final Game game = PlayerHelper.getGame(damaged);
        if (game == null || game == null || game != PlayerHelper.getGame(damager)) {
            return;
        }

        if (game.getCurrentPhase() != GamePhase.STARTED) {
            event.setCancelled(true);
            return;
        }

        if (!PlayerHelper.canDamage(damager.getUniqueId(), damaged.getUniqueId())) {
            event.setCancelled(true);
            Messager.playerMessage(damager, References.DONT_HURT_ALLY);
        }
    }

    private void respawn(UUID uuid, Game game) {
        new Timer(References.RESPAWN_COOLDOWN * 20, KingdomWars.getInstance()).start(() -> {
            Player killed = Bukkit.getPlayer(uuid);
            if (killed == null || !killed.isOnline()) {
                this.respawn(uuid, game);
                return;
            }

            respawnCooldown.put(killed.getUniqueId(), 0);
            killed.setGameMode(GameMode.SURVIVAL);
            killed.teleport(game.getMap().getSpawn(PlayerHelper.getTeam(uuid)));
            Messager.playerMessage(killed, References.RESPAWN);
        });
    }


    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        final Player killed = event.getEntity();
        final Game game = PlayerHelper.getGame(killed.getUniqueId());

        if (!PlayerHelper.isInGame(killed) || game == null) {
            return;
        }

        event.setKeepInventory(true);

        respawnCooldown.put(killed.getUniqueId(), References.RESPAWN_COOLDOWN);
        this.respawn(killed.getUniqueId(), game);

        Messager.playerMessage(killed, References.DEATH);
        Messager.playerTitle(References.DEATH_TITLE, "", killed);

        killed.setGameMode(GameMode.SPECTATOR);
        SoundSystem.locationSound(killed.getLocation(), Sound.ENTITY_ENDERDRAGON_GROWL, 0.8F, 1.0F);
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

        Bukkit.getPluginManager().callEvent(new GameKillEvent(game, killer, killed));
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        final Player player = event.getPlayer();
        if (player == null || !PlayerHelper.isInGame(player)) {
            return;
        }

        if (respawnCooldown.get(player.getUniqueId()) != 0) {
            player.setGameMode(GameMode.SPECTATOR);

            final Player killer = player.getKiller();
            if (killer != null) {
                event.setRespawnLocation(killer.getEyeLocation());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerItemDamage(PlayerItemDamageEvent event) {
        final Player player = event.getPlayer();
        if (!PlayerHelper.isInGame(player)) {
            return;
        }

        event.setCancelled(true);
    }
}
