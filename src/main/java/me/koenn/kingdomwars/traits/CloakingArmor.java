package me.koenn.kingdomwars.traits;

import me.koenn.core.misc.ItemHelper;
import me.koenn.core.misc.LoreHelper;
import me.koenn.kingdomwars.KingdomWars;
import me.koenn.kingdomwars.util.Messager;
import me.koenn.kingdomwars.util.PlayerHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

/**
 * Class for armor that can make you invisible.
 */
public class CloakingArmor extends Trait {

    /**
     * Maximum cloaking time in ticks.
     */
    public static final int MAX_CLOAK_TIME = 1200;
    public static final int LONG_COOLDOWN = 100;
    public static final int SHORT_COOLDOWN = 40;

    /**
     * The leather 'Cloaking Armor'
     */
    public static final ItemStack[] CLOAKING_ARMOR = new ItemStack[]{
            ItemHelper.makeItemStack(Material.LEATHER_BOOTS, 1, (short) 0, "Cloaking Boots", null),
            ItemHelper.makeItemStack(Material.LEATHER_LEGGINGS, 1, (short) 0, "Cloaking Leggings", null),
            ItemHelper.makeItemStack(Material.LEATHER_CHESTPLATE, 1, (short) 0, "Cloaking Chestplate", null),
            ItemHelper.makeItemStack(Material.LEATHER_HELMET, 1, (short) 0, "Cloaking Helmet", null),
    };

    /**
     * The 'Cloaking Device' to toggle the cloak.
     */
    public static final ItemStack CLOAKING_DEVICE = ItemHelper.makeItemStack(
            Material.WATCH,
            1, (short) 0,
            ChatColor.BOLD + "Cloaking Device",
            LoreHelper.makeLore(ChatColor.YELLOW + "Clock to toggle!")
    );

    /**
     * The uuid using the armor.
     */
    private final UUID uuid;

    /**
     * Current cloak status, true if the uuid is cloaked.
     */
    private boolean cloaked;

    /**
     * Time indicating how long the cloak has been active in ticks.
     */
    private int cloakTime;

    private int cooldown;

    /**
     * Constructor to create a new CloakingArmor object.
     *
     * @param uuid uuid who uses the armor
     */
    public CloakingArmor(UUID uuid) {
        //Set the uuid variable.
        this.uuid = uuid;

        //Give the uuid the cloaking armor and device.
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return;
        }

        player.getInventory().setArmorContents(CLOAKING_ARMOR);
        player.getInventory().addItem(CLOAKING_DEVICE);
    }

    /**
     * Toggle the cloak on or off.
     */
    public void toggleCloak() {
        //Check if the uuid is cloaked.
        if (this.cloaked) {

            //If so, decloak the uuid.
            this.deCloak();
        } else {

            //Otherwise, cloak the uuid.
            this.cloak();
        }
    }

    /**
     * Turn the cloak on.
     */
    public void cloak() {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return;
        }

        //Check if the uuid is electrified.
        if (!player.getMetadata("electric").isEmpty()) {
            Messager.playerMessage(player, "&cYou cloak device is disabled!");
            return;
        }

        if (this.cooldown > 0) {
            Messager.playerMessage(player, String.format("&cYour cloak is on cooldown for %ss", Math.round(this.cooldown / 20.0 * 10.0) / 10.0));
            return;
        }

        //Enable the cloak.
        this.cloaked = true;

        //Remove the armor from the uuid.
        player.getInventory().setArmorContents(new ItemStack[4]);
    }

    /**
     * Turn the cloak off.
     */
    public void deCloak() {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return;
        }

        //Disable the cloak.
        this.cloaked = false;

        //Give the armor back to the uuid.
        player.getInventory().setArmorContents(CLOAKING_ARMOR);

        //Reset the cloakTime to 0.
        this.cloakTime = 0;

        if (this.cooldown <= 0) {
            this.cooldown = SHORT_COOLDOWN;
        }
    }

    /**
     * Disable and unregister the cloak.
     * Makes the uuid visible again.
     */
    @Override
    protected void disable() {
        //Decloak the uuid.
        this.deCloak();

        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return;
        }

        //Remove the armor from the uuid.
        player.getInventory().setArmorContents(new ItemStack[4]);
    }

    /**
     * Check if an entity is the uuid using this armor.
     *
     * @param entity entity to check
     * @return isPlayer
     */
    private boolean isPlayer(Entity entity) {
        return entity instanceof Player && this.uuid.equals(entity.getUniqueId());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityDamage(EntityDamageEvent event) {
        //Check if the uuid who took damage is the uuid in the event and if he's cloaked.
        if (this.isPlayer(event.getEntity()) && this.cloaked) {

            //Decloak the uuid.
            this.deCloak();
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        //Check if the uuid who dealt damage is the uuid in the event and if he's cloaked.
        if (this.isPlayer(event.getDamager()) && this.cloaked) {

            //Decloak the uuid.
            this.deCloak();
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        //Check if the uuid who clocked is the uuid in the event.
        if (this.isPlayer(event.getWhoClicked())) {

            //Check if the clicked slot is an armor slot.
            if (event.getSlotType().equals(InventoryType.SlotType.ARMOR)) {

                //Cancel the event.
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        //Check if the uuid who clicked is the uuid in the event.
        if (this.isPlayer(event.getPlayer())) {

            //Check if the clicked item is the cloaking device.
            if (event.getItem() != null && event.getItem().isSimilar(CLOAKING_DEVICE)) {

                //Toggle the cloak.
                this.toggleCloak();
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPluginDisable(PluginDisableEvent event) {
        //Check if the plugin that is disabling is KingdomWars.
        if (event.getPlugin().equals(KingdomWars.getInstance())) {

            //Disable this cloaking armor.
            this.stop();
        }
    }

    @Override
    public void run() {
        if (this.cooldown > 0) {
            this.cooldown -= 5;
        }

        //Check if the uuid is cloaked.
        if (this.cloaked) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                return;
            }

            //Check if the uuid is capturing a point.
            if (PlayerHelper.isCapturing(this.uuid, PlayerHelper.getGame(this.uuid))) {

                //Decloak the uuid.
                this.deCloak();

                return;
            }

            //Check if the uuid is electrified.
            if (!player.getMetadata("electric").isEmpty()) {

                //Decloak the uuid.
                this.deCloak();

                return;
            }

            //Give the uuid the invisibility effect.
            player.addPotionEffect(
                    new PotionEffect(PotionEffectType.INVISIBILITY, 10, 1, true, true), true
            );

            //Increment the cloakTime by 5 (this method runs once every 5 ticks)
            this.cloakTime += 5;

            //Check if the cloakTime is more than the maximum.
            if (this.cloakTime > MAX_CLOAK_TIME) {

                //Decloak the uuid.
                this.deCloak();

                this.cooldown = LONG_COOLDOWN;
            }
        }
    }
}
