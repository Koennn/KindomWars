package me.koenn.kingdomwars.game.map;

import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.effect.WarpEffect;
import de.slikey.effectlib.util.DynamicLocation;
import de.slikey.effectlib.util.ParticleEffect;
import me.koenn.kingdomwars.KingdomWars;
import me.koenn.kingdomwars.util.SoundSystem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/**
 * Object representing a SpeedPack.
 */
public class SpeedPack implements Listener {

    /**
     * Sugar item stack.
     */
    private static final ItemStack SUGAR = new ItemStack(Material.SUGAR, 1, (short) 0);

    /**
     * Speed potion effect.
     */
    private static final PotionEffect SPEED_EFFECT = new PotionEffect(PotionEffectType.SPEED, 400, 1, true, true);

    /**
     * Location of the SpeedPack.
     */
    private final Location location;

    /**
     * Item entity on the SpeedPack.
     */
    private Item item;

    /**
     * Scheduled 'regenerate' task.
     */
    private int taskId;

    /**
     * SpeedPack constructor to initialize a SpeedPack at a specific location.
     *
     * @param location Location to initialize at
     */
    public SpeedPack(Location location) {
        this.location = location.getBlock().getLocation().add(0.5, 2.2, 0.5);
    }

    /**
     * Enable this SpeedPack, automatically regenerates the item.
     */
    public void enable() {
        this.regen();

        Bukkit.getPluginManager().registerEvents(this, KingdomWars.getInstance());
    }

    /**
     * Regenerate (respawn) the item on the SpeedPack.
     */
    public void regen() {
        if (this.item != null) {
            this.item.remove();
        }

        this.taskId = 0;

        this.item = this.location.getWorld().dropItem(this.location, SUGAR);
        this.item.teleport(this.location);
        this.item.setVelocity(new Vector());
    }

    /**
     * Disable the SpeedPack. Will automatically remove the item and stop it from regenerating.
     */
    public void disable() {
        if (this.item != null && !this.item.isDead()) {
            this.item.remove();
        }

        if (this.taskId != 0) {
            Bukkit.getScheduler().cancelTask(this.taskId);
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        if (event.getItem() == null || this.item == null) {
            return;
        }

        if (event.getItem().getUniqueId().equals(this.item.getUniqueId())) {
            event.setCancelled(true);
            event.getItem().remove();

            this.item = null;

            event.getEntity().addPotionEffect(SPEED_EFFECT, true);

            SoundSystem.locationSound(event.getItem().getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 2.0F, 1.5F);

            WarpEffect effect = new WarpEffect(new EffectManager(KingdomWars.getInstance()));
            effect.particle = ParticleEffect.SPELL;
            effect.iterations = 1;
            effect.radius = 0.5F;
            effect.particles = 5;
            effect.speed = 1.0F;
            effect.setDynamicOrigin(new DynamicLocation(event.getItem().getLocation().add(0, 1, 0)));
            effect.start();

            this.taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(KingdomWars.getInstance(), this::regen, 800);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onItemDespawn(ItemDespawnEvent event) {
        if (event.getEntity() == null || this.item == null) {
            return;
        }

        if (event.getEntity().getUniqueId().equals(this.item.getUniqueId())) {
            event.setCancelled(true);
        }
    }
}
