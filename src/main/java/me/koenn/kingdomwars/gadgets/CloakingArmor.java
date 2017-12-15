package me.koenn.kingdomwars.gadgets;

import me.koenn.core.misc.ItemHelper;
import me.koenn.core.misc.LoreHelper;
import me.koenn.kingdomwars.KingdomWars;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Class for armor that can make you invisible.
 */
public class CloakingArmor implements Listener, Runnable {

    /**
     * Maximum cloaking time in ticks.
     */
    public static final int MAX_CLOAK_TIME = 240;

    /**
     * The leather 'Cloaking Armor'
     */
    public static final ItemStack[] CLOAKING_ARMOR = new ItemStack[]{
            ItemHelper.makeItemStack(Material.LEATHER_HELMET, 1, (short) 0, "Cloaking Helmet", null),
            ItemHelper.makeItemStack(Material.LEATHER_CHESTPLATE, 1, (short) 0, "Cloaking Chestplate", null),
            ItemHelper.makeItemStack(Material.LEATHER_LEGGINGS, 1, (short) 0, "Cloaking Leggings", null),
            ItemHelper.makeItemStack(Material.LEATHER_BOOTS, 1, (short) 0, "Cloaking Boots", null)
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
     * The player using the armor.
     */
    private final Player player;

    /**
     * The id of the scheduled run task.
     */
    private final int taskId;

    /**
     * Current cloak status, true if the player is cloaked.
     */
    private boolean cloaked;

    /**
     * Time indicating how long the cloak has been active in ticks.
     */
    private int cloakTime;

    /**
     * Constructor to create a new CloakingArmor object.
     *
     * @param player player who uses the armor
     */
    public CloakingArmor(Player player) {
        //Set the player variable.
        this.player = player;

        //Give the player the cloaking armor and device.
        this.player.getInventory().setArmorContents(CLOAKING_ARMOR);
        this.player.getInventory().addItem(CLOAKING_DEVICE);

        //Register this to Bukkit as an event listener.
        Bukkit.getPluginManager().registerEvents(this, KingdomWars.getInstance());

        //Start the repeating task for the run method.
        this.taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(KingdomWars.getInstance(), this, 0, 5);
    }

    /**
     * Toggle the cloak on or off.
     */
    public void toggleCloak() {
        //Check if the player is cloaked.
        if (this.cloaked) {

            //If so, decloak the player.
            this.deCloak();
        } else {

            //Otherwise, cloak the player.
            this.cloak();
        }
    }

    /**
     * Turn the cloak on.
     */
    public void cloak() {
        //Enable the cloak.
        this.cloaked = true;

        //Remove the armor from the player.
        this.player.getInventory().setArmorContents(new ItemStack[4]);
    }

    /**
     * Turn the cloak off.
     */
    public void deCloak() {
        //Disable the cloak.
        this.cloaked = false;

        //Give the armor back to the player.
        this.player.getInventory().setArmorContents(CLOAKING_ARMOR);

        //Reset the cloakTime to 0.
        this.cloakTime = 0;
    }

    /**
     * Disable and unregister the cloak.
     * Makes the player visible again.
     */
    public void disable() {
        //Cancel the repeating run task.
        Bukkit.getScheduler().cancelTask(this.taskId);

        //Unregister this listener from Bukkit.
        HandlerList.unregisterAll(this);

        //Decloak the player.
        this.deCloak();

        //Remove the armor from the player.
        this.player.getInventory().setArmorContents(new ItemStack[4]);
    }

    /**
     * Check if an entity is the player using this armor.
     *
     * @param entity entity to check
     * @return isPlayer
     */
    private boolean isPlayer(Entity entity) {
        return entity instanceof Player && this.player.equals(entity);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityDamage(EntityDamageEvent event) {
        //Check if the player who took damage is the player in the event and if he's cloaked.
        if (this.isPlayer(event.getEntity()) && this.cloaked) {

            //Decloak the player.
            this.deCloak();
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        //Check if the player who dealt damage is the player in the event and if he's cloaked.
        if (this.isPlayer(event.getDamager()) && this.cloaked) {

            //Decloak the player.
            this.deCloak();
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        //Check if the player who clocked is the player in the event.
        if (this.isPlayer(event.getWhoClicked())) {

            //Check if the clicked slot is an armor slot.
            if (event.getSlotType().equals(InventoryType.SlotType.ARMOR)) {

                //Cancel the event.
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        //Check if the player who clicked is the player in the event.
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
            this.disable();
        }
    }

    @Override
    public void run() {
        //Check if the player is cloaked.
        if (this.cloaked) {

            //Give the player the invisibility effect.
            this.player.addPotionEffect(
                    new PotionEffect(PotionEffectType.INVISIBILITY, 10, 1, true, true), true
            );

            //Increment the cloakTime by 5 (this method runs once every 5 ticks)
            this.cloakTime += 5;

            //Check if the cloakTime is more than the maximum.
            if (this.cloakTime > MAX_CLOAK_TIME) {

                //Decloak the player.
                this.deCloak();
            }
        }
    }
}