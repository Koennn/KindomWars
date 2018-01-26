package me.koenn.kingdomwars.listeners;

import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.effect.AnimatedBallEffect;
import de.slikey.effectlib.util.DynamicLocation;
import de.slikey.effectlib.util.ParticleEffect;
import me.koenn.core.player.CPlayer;
import me.koenn.core.player.CPlayerRegistry;
import me.koenn.kingdomwars.KingdomWars;
import me.koenn.kingdomwars.discord.DiscordBot;
import me.koenn.kingdomwars.game.Game;
import me.koenn.kingdomwars.util.Messager;
import me.koenn.kingdomwars.util.PlayerHelper;
import me.koenn.kingdomwars.util.References;
import me.koenn.kingdomwars.util.SoundSystem;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.*;

import java.util.Objects;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, June 2017
 */
public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            return;
        }

        final Player player = event.getPlayer();
        if (!PlayerHelper.isInGame(player)) {
            return;
        }

        final Game game = PlayerHelper.getGame(player.getUniqueId());
        final Block clicked = event.getClickedBlock();
        game.getDeployables().stream()
                .filter(Objects::nonNull)
                .forEach(deployable -> deployable.getDeployableBlocks().stream()
                        .filter(Objects::nonNull)
                        .filter(location -> location.equals(clicked.getLocation()))
                        .forEach(location -> deployable.damage(3, player)));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player)) {
            return;
        }

        final Player player = (Player) event.getEntity().getShooter();
        if (!PlayerHelper.isInGame(player)) {
            return;
        }

        int damage = 3;
        if (!event.getEntity().getMetadata("electric").isEmpty()) {
            damage *= 2;
        }

        final Block hit = event.getHitBlock();
        if (hit == null) {
            return;
        }

        final int finalDamage = damage;
        final Game game = PlayerHelper.getGame(player.getUniqueId());
        game.getDeployables().stream()
                .filter(Objects::nonNull)
                .forEach(deployable -> deployable.getDeployableBlocks().stream()
                        .filter(Objects::nonNull)
                        .filter(location -> location.equals(hit.getLocation()))
                        .forEach(location -> deployable.damage(finalDamage, player)));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerItemDamage(PlayerItemDamageEvent event) {
        final Player player = event.getPlayer();
        if (PlayerHelper.isInGame(player)) {
            event.setCancelled(true);
            event.getItem().setDurability(event.getItem().getType().getMaxDurability());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (!PlayerHelper.isInGame(player.getUniqueId()) || !player.getGameMode().equals(GameMode.SURVIVAL)) {
            return;
        }

        player.damage(Integer.MAX_VALUE);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (PlayerHelper.isInGame(player.getUniqueId())) {
            if (player.isDead()) {
                Messager.playerMessage(player, References.DEATH);
            }
            return;
        }

        player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation().clone().add(0.5, 0, 0.5));

        SoundSystem.playerSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);

        AnimatedBallEffect effect = new AnimatedBallEffect(new EffectManager(KingdomWars.getInstance()));
        effect.particle = ParticleEffect.VILLAGER_HAPPY;
        effect.iterations = 50;
        effect.yOffset = -1.0F;
        effect.setDynamicOrigin(new DynamicLocation(player));
        effect.start();

        Bukkit.getScheduler().scheduleSyncDelayedTask(KingdomWars.getInstance(), () -> {
            Messager.clearChat(player);
            for (String line : References.SERVER_JOIN_MESSAGE) {
                Messager.playerMessage(player, line);
            }
        }, 10);

        CPlayer cPlayer = CPlayerRegistry.getCPlayer(player.getUniqueId());
        String discordId = cPlayer.get("discord_id");
        if (discordId != null) {
            DiscordBot.LINKS.put(player.getUniqueId(), DiscordBot.getUser(discordId).getIdLong());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (PlayerHelper.isInGame(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }
}
