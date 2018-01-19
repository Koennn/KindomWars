package me.koenn.kingdomwars.traits;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Robot extends Trait {

    private final Player player;
    private boolean enabled;

    public Robot(Player player) {
        this.player = player;
        this.enabled = true;
    }

    @Override
    protected void disable() {
        this.enabled = false;
    }

    @Override
    public void run() {
        if (this.enabled) {
            this.player.addPotionEffect(
                    new PotionEffect(PotionEffectType.SLOW, 10, 1, true, false), true
            );
            this.player.addPotionEffect(
                    new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10, 0, true, false), true
            );
        }
    }
}
