package me.koenn.kingdomwars.game.counters;

import me.koenn.core.misc.ActionBar;
import me.koenn.core.misc.Counter;
import me.koenn.core.misc.ProgressBar;
import me.koenn.kingdomwars.KingdomWars;
import me.koenn.kingdomwars.game.Game;
import me.koenn.kingdomwars.util.SoundSystem;
import org.bukkit.Sound;

public class PrepareCounter extends Counter {

    private static final int PREPARE_TIME = 400;

    private final Game game;
    private final ProgressBar progressBar;
    private int cooldown = 0;

    public PrepareCounter(boolean debug, Game game) {
        super(debug ? 1 : PREPARE_TIME, KingdomWars.getInstance());
        this.game = game;
        this.progressBar = new ProgressBar(60);
    }

    @Override
    public void onCount(int time) {
        int scaledTime = Math.round((1.0F / ((float) time / 500.0F) + 0.2F) * 10.0F);
        float pitch = calculateScaledProgress(scaledTime, 1.0F);
        ActionBar actionBar = new ActionBar(this.progressBar.get(Math.round(((float) time / PREPARE_TIME) * 100.0F)), KingdomWars.getInstance()).setStay(1);

        this.game.getPlayers().forEach(player -> {
            actionBar.send(player);

            if (this.cooldown == 0) {
                SoundSystem.playerSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, pitch);
            }

            if (this.cooldown > 0) {
                this.cooldown--;
            } else if (this.cooldown == 0) {
                this.cooldown = 20 - Math.round(scaledTime / 20);
            }
        });
    }

    private float calculateScaledProgress(float current, float maxSize) {
        return current < 100.0F ? (current > 0.0F ? current * maxSize / 100.0F : 0.0F) : maxSize;
    }
}
