package me.koenn.kingdomwars.traits;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class Robot extends Trait {

    private final UUID uuid;
    private boolean enabled;

    public Robot(UUID uuid) {
        this.uuid = uuid;
        this.enabled = true;
    }

    @Override
    protected void disable() {
        this.enabled = false;
    }

    @Override
    public void run() {
        if (this.enabled) {
            Player player = Bukkit.getPlayer(this.uuid);
            if (player == null) {
                return;
            }

            player.addPotionEffect(
                    new PotionEffect(PotionEffectType.SLOW, 10, 0, true, false), true
            );
            player.addPotionEffect(
                    new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10, 0, true, false), true
            );
        }
    }
}
