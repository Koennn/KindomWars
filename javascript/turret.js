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
            if (PlayerHelper.getTeam(player) == team) {
                continue;
            }
            var p = Bukkit.getPlayer(player);
            if (isInRange(p, deployable)) {
                lockedOn = p;
            }
        }
    }

    if (lockedOn != null) {
        var loc2 = deployable.getLocation().clone().add(0.5, 2.5, 0.5);
        var loc1 = lockedOn.getLocation().clone().add(0.0, 1.0 + (lockedOn.getLocation().distance(deployable.getLocation()) / 8), 0.0);
        var arrow = loc2.getWorld().spawnArrow(loc2, new Vector(loc1.getX()-loc2.getX(), loc1.getY()-loc2.getY(), loc1.getZ()-loc2.getZ()), 1.5, 0);
        arrow.setCustomName(Bukkit.getOfflinePlayer(owner).getName() + "'s Turret");
        arrow.setCustomNameVisible(false);
        cooldown = 8;
    }
}

function onDamageTake(deployable, amount, damager) {

}

function onDestroy(deployable) {

}

function isInRange(player, deployable) {
    if (player.getGameMode().equals(GameMode.SURVIVAL) && player.getLocation().distance(deployable.getLocation()) < 10) {
        return true;
    }
    return false;
}