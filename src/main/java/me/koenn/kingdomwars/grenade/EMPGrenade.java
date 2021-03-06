package me.koenn.kingdomwars.grenade;

import de.slikey.effectlib.Effect;
import de.slikey.effectlib.util.DynamicLocation;
import de.slikey.effectlib.util.ParticleEffect;
import me.koenn.kingdomwars.KingdomWars;
import me.koenn.kingdomwars.effect.EMPEffect;
import me.koenn.kingdomwars.effect.GrenadeEffect;
import me.koenn.kingdomwars.game.Game;
import me.koenn.kingdomwars.util.ElectricMeta;
import me.koenn.kingdomwars.util.PlayerHelper;
import me.koenn.kingdomwars.util.SoundSystem;
import me.koenn.kingdomwars.util.Team;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

/**
 * A grenade that damages and temporarily disables enemy deployables.
 */
public class EMPGrenade extends Grenade {

    public EMPGrenade() {
        super("emp_grenade");
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
            private Player thrower;

            @Override
            public void onThrow(Player thrower, Projectile grenade) {
                //Create and start the particle trail for the grenade.
                this.particle = new GrenadeEffect(ParticleEffect.FIREWORKS_SPARK);
                this.particle.setDynamicOrigin(new DynamicLocation(grenade));
                this.particle.start();

                //Save the thrower to the variable.
                this.thrower = thrower;
            }

            @Override
            public void onImpact(Location impact) {
                //Stop the particle trail.
                this.particle.cancel();

                //Calculate the impact point.
                impact.add(0, 0.5, 0);

                //Play the EMP particle effect.
                EMPEffect empEffect = new EMPEffect();
                empEffect.setDynamicOrigin(new DynamicLocation(impact));
                empEffect.start();

                //Play a high-pitch explosion sound.
                SoundSystem.locationSound(impact, Sound.ENTITY_GENERIC_EXPLODE, 1.0F, 1.5F);

                //Get the current game instance.
                Game game = PlayerHelper.getGame(this.thrower.getUniqueId());

                //Make sure we're in a game.
                if (game == null) {
                    return;
                }

                //Get the current team instance.
                Team team = PlayerHelper.getTeam(this.thrower.getUniqueId());

                //Disable all deployables within 6.5 blocks of the impact.
                game.getDeployables().stream()
                        .filter(Objects::nonNull)
                        .filter(deployable -> deployable.getLocation().distance(impact) <= 6.5)
                        .filter(deployable -> deployable.getTeam() != null)
                        .filter(deployable -> !deployable.getTeam().equals(team))
                        .forEach(deployable -> deployable.disable(this.thrower));

                //'Electrify' all players within 6.5 blocks of the impact.
                game.getTeam(team.getOpponent()).stream()
                        .map(Bukkit::getPlayer)
                        .filter(player -> player.getLocation().distance(impact) <= 6.5)
                        .forEach(player -> {
                            player.addPotionEffect(
                                    new PotionEffect(PotionEffectType.CONFUSION, 250, 0, true, true), true
                            );

                            player.setMetadata("electric", new ElectricMeta());
                            Bukkit.getScheduler().scheduleSyncDelayedTask(KingdomWars.getInstance(), () ->
                                    player.removeMetadata("electric", KingdomWars.getInstance()), 300
                            );
                        });
            }
        };
    }
}
