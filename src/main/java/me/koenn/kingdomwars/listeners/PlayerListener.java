package me.koenn.kingdomwars.listeners;

import me.koenn.kingdomwars.game.Game;
import me.koenn.kingdomwars.util.PlayerHelper;
import net.minecraft.server.v1_8_R3.AttributeModifier;
import net.minecraft.server.v1_8_R3.Enchantment;
import net.minecraft.server.v1_8_R3.EnumMonsterType;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

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

        final Game game = PlayerHelper.getGame(player);
        final Block clicked = event.getClickedBlock();
        if (event.getItem() != null) {
            getAttackDamage(event.getItem());
        }
        game.getDeployables().forEach(deployable -> deployable.getDeployableBlocks().stream().filter(location -> location.equals(clicked.getLocation())).forEach(location -> deployable.damage(5, player)));
    }

    private float getAttackDamage(ItemStack item) {
        net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
        for (AttributeModifier attributeModifier : nmsStack.B().get("generic.attackDamage")) {
            Bukkit.getLogger().info(String.valueOf(attributeModifier.d() * Enchantment.DAMAGE_ALL.a(1, EnumMonsterType.UNDEFINED)));
        }
        return 1.0F;
    }
}
