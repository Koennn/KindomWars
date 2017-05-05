var grenade;
var baseGrenade;

var start = function(entity, grenadeObj) {
    baseGrenade = grenadeObj;
    grenade = entity
    setTimeout(explode, 3000);
}

var tick = function() {
    var location = grenade.getLocation();
    var world = grenade.getWorld();
    world.playEffect(location, Effect.SMOKE, 1, 10);
}

var explode = function() {
    var location = grenade.getLocation();
    var world = grenade.getWorld();
    Bukkit.getScheduler().scheduleSyncDelayedTask(KingdomWars.getInstance(), new Runnable() {
        run: function() {
            world.createExplosion(location.getX(), location.getY(), location.getZ(), 4, true, false);
        }
    }, 1);
}