var lockedOn = null;
var cooldown = 0;
var game = null;
var team = null;
var owner = null;

function onConstruct(deployable, placer, location) {
    owner = placer;
    game = PlayerHelper.getGame(placer);
    team = PlayerHelper.getTeam(placer);
    if (game == null || team == null) {
        Messager.playerMessage(Bukkit.getPlayer(placer), References.NOT_IN_GAME);
    }
}

function onConstructComplete(deployable) {
    if (owner != null) {
        Messager.playerMessage(Bukkit.getPlayer(owner), References.BUILDING_COMPLETE);
    }
}

function onTick(deployable) {
    if (game == null || team == null) {
        return;
    }

    if (cooldown > 0) {
        cooldown--;
        return;
    }

    if (lockedOn == null || !isInRange(lockedOn, deployable)) {
        lockedOn = null;
        var players = game.getPlayers();
        for (var index = 0; index < players.size(); index++) {
            var player = players.get(index);
            if (PlayerHelper.getTeam(player) != team) {
                continue;
            }
            var p = Bukkit.getPlayer(player);
            if (isInRange(p, deployable) && p.getHealth() < p.getMaxHealth()) {
                lockedOn = p;
            }
        }
    }

    if (lockedOn != null) {
        var loc2 = deployable.getLocation().clone().add(0.5, 1, 0.5);
        var loc1 = lockedOn.getLocation().clone().add(0.0, 0.5, 0.0);
        ParticleRenderer.renderLine(ParticleEffect.HEART, 5, loc1, loc2);
        if (lockedOn.getHealth() + 1.0 < lockedOn.getMaxHealth()) {
            lockedOn.setHealth(lockedOn.getHealth() + 1.0);
        } else {
            lockedOn.setHealth(lockedOn.getMaxHealth());
            lockedOn = null;
        }
        cooldown = 5;
    }
}

function onDamageTake(deployable, amount, damager) {

}

function onDestroy(deployable) {

}

function isInRange(player, deployable) {
    if (player.getLocation().distance(deployable.getLocation()) < 4) {
        return true;
    }
    return false;
}