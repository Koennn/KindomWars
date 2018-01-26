package me.koenn.kingdomwars.grenade;

import de.slikey.effectlib.Effect;
import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.effect.ExplodeEffect;
import de.slikey.effectlib.util.DynamicLocation;
import de.slikey.effectlib.util.ParticleEffect;
import me.koenn.kingdomwars.KingdomWars;
import me.koenn.kingdomwars.effect.GrenadeEffect;
import me.koenn.kingdomwars.game.Game;
import me.koenn.kingdomwars.util.PlayerHelper;
import me.koenn.kingdomwars.util.StunMeta;
import me.koenn.kingdomwars.util.Team;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class StunGrenade extends Grenade {

    public StunGrenade() {
        super("stun_grenade");
    }

    @Override
    protected GrenadeExecutor createExecutor() {
        return new GrenadeExecutor() {
            /**
             * Particle trail to follow the thrown grenade.
             */
            private Effect particle;

            /**
             * Thrower of the grenade.
             */
            private UUID thrower;

            @Override
            public void onThrow(Player thrower, Projectile grenade) {
                //Create and start the particle trail for the grenade.
                this.particle = new GrenadeEffect(ParticleEffect.SMOKE_NORMAL);
                this.particle.setDynamicOrigin(new DynamicLocation(grenade));
                this.particle.start();

                //Save the thrower to the variable.
                this.thrower = thrower.getUniqueId();
            }

            @Override
            public void onImpact(Location impact) {
                //Stop the particle trail.
                this.particle.cancel();

                //Calculate the impact point.
                impact.add(0, 0.5, 0);

                ExplodeEffect explodeEffect = new ExplodeEffect(new EffectManager(KingdomWars.getInstance()));
                explodeEffect.setDynamicOrigin(new DynamicLocation(impact));
                explodeEffect.start();

                //Get the current game instance.
                Game game = PlayerHelper.getGame(this.thrower);

                //Make sure we're in a game.
                if (game == null) {
                    return;
                }

                //Get the current team instance.
                Team team = PlayerHelper.getTeam(this.thrower);

                game.getTeam(team.getOpponent()).stream()
                        .map(Bukkit::getPlayer)
                        .filter(player -> player.getLocation().distance(impact) <= 6.5)
                        .forEach(player -> {
                            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 80, 0, true, true), true);

                            player.setMetadata("stun", new StunMeta());
                            Bukkit.getScheduler().scheduleSyncDelayedTask(KingdomWars.getInstance(), () ->
                                    player.removeMetadata("stun", KingdomWars.getInstance()), 80
                            );
                        });
            }
        };
    }
}
