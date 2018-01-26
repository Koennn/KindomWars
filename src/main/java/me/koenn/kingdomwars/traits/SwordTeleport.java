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
import org.bukkit.util.Vector;

import java.util.UUID;

public class SwordTeleport extends Trait {

    public static final ItemStack UNSTABLE_SWORD = ItemHelper.makeItemStack(
            Material.IRON_SWORD,
            1, (short) 0,
            ChatColor.BOLD + "Unstable Sword",
            null
    );

    private final UUID uuid;
    private int cooldown;

    public SwordTeleport(UUID uuid) {
        this.uuid = uuid;

        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            player.getInventory().addItem(UNSTABLE_SWORD);
        }
    }

    private void teleport() {
        if (this.cooldown > 0) {
            return;
        }

        Player player = Bukkit.getPlayer(this.uuid);
        if (player == null) {
            return;
        }

        Vector direction = player.getLocation().getDirection();
        double hMultiplier = 1.2;
        double vMultiplier = 1.5;
        Vector move = direction.clone().multiply(hMultiplier).setY(Math.min(Math.max(direction.getY() * vMultiplier, 0.0) + 0.2, 0.5));
        player.setVelocity(move);
        this.cooldown = 15;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager().getUniqueId().equals(this.uuid) && ((Player) event.getDamager()).getInventory().getItemInMainHand().isSimilar(UNSTABLE_SWORD)) {
            this.teleport();
        }
    }

    @Override
    protected void disable() {

    }

    @Override
    public void run() {
        if (this.cooldown > 0) {
            this.cooldown -= 5;
        }
    }
}
