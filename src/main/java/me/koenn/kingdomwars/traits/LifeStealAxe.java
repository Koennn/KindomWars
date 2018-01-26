package me.koenn.kingdomwars.traits;

import me.koenn.core.misc.ItemHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class LifeStealAxe extends Trait {

    public static final ItemStack LIFESTEAL_AXE = ItemHelper.makeItemStack(
            Material.IRON_AXE,
            1, (short) 0,
            ChatColor.BOLD + "Lifesteal Axe",
            null
    );

    private final UUID uuid;

    public LifeStealAxe(UUID uuid) {
        this.uuid = uuid;

        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            player.getInventory().addItem(LIFESTEAL_AXE);
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager().getUniqueId().equals(this.uuid)) {
            double heal = event.getFinalDamage() * 0.4;
            Player damager = (Player) event.getDamager();
            damager.setHealth(Math.min(damager.getHealth() + heal, damager.getMaxHealth()));
        }
    }

    @Override
    protected void disable() {

    }

    @Override
    public void run() {

    }
}
