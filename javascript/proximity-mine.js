var game = null;
var team = null;
var owner = null;
var ignited = false;
var inst = null;

function onConstruct(deployable, placer, location) {
    owner = placer;
    game = PlayerHelper.getGame(placer);
    team = PlayerHelper.getTeam(placer);
    if (game == null || team == null) {
        Messager.playerMessage(owner, References.NOT_IN_GAME);
    }
    inst = deployable;
}

function onConstructComplete(deployable) {
    if (owner != null) {
        Messager.playerMessage(owner, References.BUILDING_COMPLETE);
    }
}

function onTick(deployable) {
    if (game == null || team == null) {
        return;
    }

    if (!ignited) {
        var players = game.getPlayers();
        for (var index = 0; index < players.size(); index++) {
            var player = players.get(index);
            if (PlayerHelper.getTeam(player) == team) {
                continue;
            }
            if (isInRange(player, deployable)) {
                this.ignite();
            }
        }
    }
}

function ignite() {
    ignited = true;
    new Timer(20, KingdomWars.getInstance()).start(explode);
}

function explode() {
    inst.remove();
    var location = inst.getLocation();
    var world = location.getWorld();
    world.createExplosion(location.getX() + 0.5, location.getY() + 0.5, location.getZ() + 0.5, 2.5, false, false);
}

function onDamageTake(deployable, amount, damager) {

}

function onDestroy(deployable) {

}

function isInRange(player, deployable) {
    if (player.getGameMode().equals(GameMode.SURVIVAL) && player.getLocation().distance(deployable.getLocation()) < 4) {
        return true;
    }
    return false;
}