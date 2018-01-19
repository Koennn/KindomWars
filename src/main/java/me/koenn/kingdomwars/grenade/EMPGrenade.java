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
                Game game = PlayerHelper.getGame(this.thrower);

                //Make sure we're in a game.
                if (game == null) {
                    return;
                }

                //Get the current team instance.
                Team team = PlayerHelper.getTeam(this.thrower);

                //Disable all deployables within 6.5 blocks of the impact.
                game.getDeployables().stream()
                        .filter(deployable -> deployable.getLocation().distance(impact) <= 6.5)
                        .filter(deployable -> !deployable.getTeam().equals(team))
                        .forEach(deployable -> deployable.disable(this.thrower));

                //'Electrify' all players within 6.5 blocks of the impact.
                game.getTeam(team.getOpponent()).stream()
                        .filter(player -> player.getLocation().distance(impact) <= 6.5)
                        .forEach(player -> {
                            player.setMetadata("electric", new ElectricMeta());
                            Bukkit.getScheduler().scheduleSyncDelayedTask(KingdomWars.getInstance(), () ->
                                    player.removeMetadata("electric", KingdomWars.getInstance()), 300
                            );
                        });
            }
        };
    }
}
