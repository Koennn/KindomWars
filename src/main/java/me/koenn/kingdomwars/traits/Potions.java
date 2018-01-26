package me.koenn.kingdomwars.traits;

import me.koenn.core.misc.FancyString;
import me.koenn.kingdomwars.util.PlayerHelper;
import me.koenn.kingdomwars.util.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class Potions extends Trait {

    private static final List<ItemStack> POTIONS = loadPotions();

    private final UUID uuid;

    private int count;
    private int potions;

    public Potions(UUID uuid) {
        this.uuid = uuid;

        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return;
        }

        for (int i = 0; i < 3; i++) {
            player.getInventory().addItem(POTIONS.get(ThreadLocalRandom.current().nextInt(POTIONS.size())));
        }
    }

    private static List<ItemStack> loadPotions() {
        List<ItemStack> POTIONS = new ArrayList<>();
        POTIONS.add(makePotion(new PotionEffect(PotionEffectType.SPEED, 1000, 1, false, true), Color.AQUA, ChatColor.GREEN, 1));
        POTIONS.add(makePotion(new PotionEffect(PotionEffectType.REGENERATION, 500, 0, false, true), Color.RED, ChatColor.GREEN, 1));
        POTIONS.add(makePotion(new PotionEffect(PotionEffectType.POISON, 300, 0, false, true), Color.OLIVE, ChatColor.RED, 1));
        POTIONS.add(makePotion(new PotionEffect(PotionEffectType.JUMP, 500, 3, false, true), Color.LIME, ChatColor.GREEN, 1));
        POTIONS.add(makePotion(new PotionEffect(PotionEffectType.BLINDNESS, 200, 0, false, true), Color.BLACK, ChatColor.RED, 1));
        POTIONS.add(makePotion(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 500, 1, false, true), Color.MAROON, ChatColor.GREEN, 1));
        POTIONS.add(makePotion(new PotionEffect(PotionEffectType.ABSORPTION, 500, 1, false, true), Color.YELLOW, ChatColor.GREEN, 1));
        POTIONS.add(makePotion(new PotionEffect(PotionEffectType.CONFUSION, 200, 1, false, true), Color.PURPLE, ChatColor.RED, 1));
        POTIONS.add(makePotion(new PotionEffect(PotionEffectType.HARM, 5, 1, false, true), Color.MAROON, ChatColor.RED, 1));
        return POTIONS;
    }

    private static ItemStack makePotion(PotionEffect effect, Color color, ChatColor chatColor, int type) {
        ItemStack potion = new ItemStack(type == 0 ? Material.POTION : type == 1 ? Material.SPLASH_POTION : Material.LINGERING_POTION);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        meta.addCustomEffect(effect, false);
        meta.setColor(color);
        String potionName = effect.getType().getName().replace("INCREASE_DAMAGE", "STRENGTH");
        meta.setDisplayName(chatColor + "Potion of " + new FancyString(potionName));
        potion.setItemMeta(meta);
        return potion;
    }

    @Override
    protected void disable() {
        this.count = 0;
    }

    private void attemptGivePotion() {
        Player player = Bukkit.getPlayer(this.uuid);
        if (player == null) {
            return;
        }

        Random random = ThreadLocalRandom.current();
        if (random.nextInt(8) == 1) {
            player.getInventory().addItem(POTIONS.get(random.nextInt(POTIONS.size())));
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPotionSplash(PotionSplashEvent event) {
        if (!(event.getPotion().getShooter() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getPotion().getShooter();
        if (!player.getUniqueId().equals(this.uuid)) {
            return;
        }

        Team team = PlayerHelper.getTeam(player.getUniqueId());
        boolean good = event.getPotion().getItem().getItemMeta().getDisplayName().startsWith(String.valueOf(ChatColor.GREEN));

        event.getAffectedEntities().stream()
                .filter(entity -> PlayerHelper.isInGame(entity.getUniqueId()))
                .filter(entity -> {
                    if (good) {
                        return PlayerHelper.getTeam(entity.getUniqueId()).equals(team);
                    } else {
                        return !PlayerHelper.getTeam(entity.getUniqueId()).equals(team);
                    }
                })
                .forEach(entity -> event.getPotion().getEffects().forEach(entity::addPotionEffect));
        event.setCancelled(true);
    }

    @Override
    public void run() {
        this.count += 5;

        if (this.count >= 50) {
            this.count = 0;

            if (this.potions < 3) {
                this.attemptGivePotion();
            }
        }

        Player player = Bukkit.getPlayer(this.uuid);
        if (player == null) {
            return;
        }

        this.potions = 0;

        for (ItemStack item : player.getInventory()) {
            if (item == null) {
                continue;
            }
            if (item.getType().equals(Material.GLASS_BOTTLE)) {
                player.getInventory().remove(item);
            }

            if (item.getType().name().contains("POTION")) {
                this.potions++;
            }
        }
    }
}
