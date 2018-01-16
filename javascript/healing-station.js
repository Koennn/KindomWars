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
        Messager.playerMessage(placer, References.NOT_IN_GAME);
    }
}

function onConstructComplete(deployable) {
    if (owner != null) {
        Messager.playerMessage(placer, References.TURRET_COMPLETE);
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
            if (isInRange(player, deployable)) {
                lockedOn = player;
            }
        }
    }

    if (lockedOn != null) {
        var loc2 = deployable.getLocation().clone().add(0.5, 1, 0.5);
        var loc1 = lockedOn.getLocation().clone().add(0.0, 1.0, 0.0);
        ParticleRenderer.renderLine(ParticleEffect.HEART, loc1, loc2);
        lockedOn.setHealth(lockedOn.getHealth() + 0.5);
        cooldown = 5;
    }
}

function onDamageTake(deployable, amount, damager) {

}

function onDestroy(deployable) {

}

function isInRange(player, deployable) {
    if (player.getLocation().distance(deployable.getLocation()) < 3) {
        return true;
    }
    return false;
}