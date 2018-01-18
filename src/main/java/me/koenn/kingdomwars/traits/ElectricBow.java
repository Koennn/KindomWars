package me.koenn.kingdomwars.traits;

import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.util.DynamicLocation;
import me.koenn.core.misc.ItemHelper;
import me.koenn.kingdomwars.KingdomWars;
import me.koenn.kingdomwars.effect.SparkEffect;
import me.koenn.kingdomwars.util.ElectricMeta;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ElectricBow extends Trait {

    public static final ItemStack ELECTRIC_BOW = ItemHelper.makeItemStack(
            Material.BOW,
            1, (short) 0,
            ChatColor.BOLD + "Electric Bow",
            null
    );

    public static final ItemStack ELECTRIC_ARROW = ItemHelper.makeItemStack(
            Material.ARROW,
            1, (short) 0,
            ChatColor.BOLD + "Electric Arrow",
            null
    );

    private final Player player;

    public ElectricBow(Player player) {
        this.player = player;

        ItemMeta meta = ELECTRIC_BOW.getItemMeta();
        meta.addEnchant(Enchantment.ARROW_INFINITE, 1, false);
        ELECTRIC_BOW.setItemMeta(meta);

        player.getInventory().addItem(ELECTRIC_BOW);
        player.getInventory().addItem(ELECTRIC_ARROW);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityShootBow(EntityShootBowEvent event) {
        if (event.getBow().isSimilar(ELECTRIC_BOW) && event.getEntity().equals(this.player)) {
            event.getProjectile().setMetadata("electric", new ElectricMeta());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!event.getEntity().getMetadata("electric").isEmpty()) {
            Block block = event.getHitBlock();
            if (block != null) {
                new SparkEffect(new EffectManager(KingdomWars.getInstance()), new DynamicLocation(block.getLocation())).start();

                block.getWorld().getNearbyEntities(block.getLocation(), 3, 3, 3).stream()
                        .filter(entity -> entity instanceof LivingEntity)
                        .forEach(entity -> ((LivingEntity) entity).addPotionEffect(
                                new PotionEffect(PotionEffectType.BLINDNESS, 30, 0, true, false)
                        ));
            } else {
                Entity hitEntity = event.getHitEntity();
                new SparkEffect(new EffectManager(KingdomWars.getInstance()), new DynamicLocation(hitEntity)).start();
                hitEntity.setMetadata("electric", new ElectricMeta());
                Bukkit.getScheduler().scheduleSyncDelayedTask(KingdomWars.getInstance(), () ->
                        hitEntity.removeMetadata("electric", KingdomWars.getInstance()), 200
                );

                hitEntity.getWorld().getNearbyEntities(hitEntity.getLocation(), 3, 3, 3).stream()
                        .filter(entity -> entity instanceof LivingEntity)
                        .forEach(entity -> ((LivingEntity) entity).addPotionEffect(
                                new PotionEffect(PotionEffectType.BLINDNESS, 30, 0, true, false)
                        ));
            }
        }
    }

    @Override
    protected void disable() {

    }

    @Override
    public void run() {

    }
}
