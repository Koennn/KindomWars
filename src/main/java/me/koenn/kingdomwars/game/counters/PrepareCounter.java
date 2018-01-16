package me.koenn.kingdomwars.game.counters;

import me.koenn.core.misc.ActionBar;
import me.koenn.core.misc.Counter;
import me.koenn.core.misc.ProgressBar;
import me.koenn.kingdomwars.KingdomWars;
import me.koenn.kingdomwars.game.Game;
import org.bukkit.Sound;

//TODO Test system!
public class PrepareCounter extends Counter {

    private static final int PREPARE_TIME = 500;

    private final Game game;
    private int cooldown = 0;

    public PrepareCounter(boolean debug, Game game) {
        super(debug ? 1 : PREPARE_TIME, KingdomWars.getInstance());
        this.game = game;
    }

    @Override
    public void onCount(int time) {
        int scaledTime = Math.round((1.0F / ((float) time / 500.0F)) * 10.0F);
        float pitch = calculateScaledProgress(scaledTime, 1.0F) + 0.5F;
        ActionBar actionBar = new ActionBar(new ProgressBar(60).get((int) ((calculateScaledProgress(time, PREPARE_TIME) / 100) * 60)), KingdomWars.getInstance()).setStay(1);

        this.game.getPlayers().forEach(player -> {
            actionBar.send(player);

            if (this.cooldown == 0) {
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, pitch);
            }

            if (this.cooldown > 0) {
                this.cooldown--;
            } else if (this.cooldown == 0) {
                this.cooldown = 10 - Math.round(scaledTime / 10);
            }
        });
    }

    private float calculateScaledProgress(float current, float maxSize) {
        return current < 100.0F ? (current > 0.0F ? current * maxSize / 100.0F : 0.0F) : maxSize;
    }
}
