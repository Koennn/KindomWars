package me.koenn.kingdomwars.mapcreator;

import me.koenn.kingdomwars.util.Team;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerInteractEvent;

public class MapToolUseEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final PlayerInteractEvent event;
    private final MapTool.Mode mode;
    private final Team team;
    private final Location selected;
    private final Player player;

    public MapToolUseEvent(PlayerInteractEvent event) {
        this.event = event;
        this.mode = MapTool.Mode.valueOf(event.getItem().getItemMeta().getLore().get(0).split(" ")[1]);
        this.team = event.getAction().name().startsWith("LEFT") ? Team.BLUE : Team.RED;
        this.selected = event.getClickedBlock() != null ? event.getClickedBlock().getLocation() : null;
        this.player = event.getPlayer();
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public PlayerInteractEvent getEvent() {
        return event;
    }

    public MapTool.Mode getMode() {
        return mode;
    }

    public Team getTeam() {
        return team;
    }

    public Location getSelected() {
        return selected;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
