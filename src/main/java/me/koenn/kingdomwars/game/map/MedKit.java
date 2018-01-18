package me.koenn.kingdomwars.game.map;

import me.koenn.kingdomwars.KingdomWars;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class MedKit implements Listener {

    private final Location location;
    private Item item;

    public MedKit(Location location) {
        this.location = location.getBlock().getLocation().add(0.5, 2.2, 0.5);
    }

    public void enable() {
        this.regen();

        Bukkit.getPluginManager().registerEvents(this, KingdomWars.getInstance());
    }

    public void regen() {
        if (this.item != null) {
            this.item.remove();
        }

        this.item = this.location.getWorld().dropItem(this.location, new ItemStack(Material.GOLDEN_APPLE, 1, (short) 1));
        this.item.teleport(this.location);
        this.item.setVelocity(new Vector());
    }

    public void disable() {
        if (this.item != null && !this.item.isDead()) {
            this.item.remove();
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        if (event.getItem().getUniqueId().equals(this.item.getUniqueId())) {
            event.setCancelled(true);
            event.getItem().remove();
            this.item = null;
            event.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 1, 2, false, true));
            Bukkit.getScheduler().scheduleSyncDelayedTask(KingdomWars.getInstance(), this::regen, 600);
        }
    }
}
