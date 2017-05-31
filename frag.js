var grenade;
var baseGrenade;

var start = function(entity, grenadeObj) {
    baseGrenade = grenadeObj;
    grenade = entity
    new Timer(80, KingdomWars.getInstance()).start(explode);
}

var tick = function() {
    var location = grenade.getLocation();
    var world = grenade.getWorld();
    world.playEffect(location, Effect.SMOKE, 1, 25);
}

var explode = function() {
    var location = grenade.getLocation();
    var world = grenade.getWorld();
    world.createExplosion(location.getX(), location.getY(), location.getZ(), 4, false, false);
    grenade.remove();
    baseGrenade.remove();
}