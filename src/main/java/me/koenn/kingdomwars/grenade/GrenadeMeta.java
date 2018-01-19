package me.koenn.kingdomwars.grenade;

import me.koenn.kingdomwars.KingdomWars;
import org.bukkit.metadata.MetadataValueAdapter;

public class GrenadeMeta extends MetadataValueAdapter {

    private String type;

    protected GrenadeMeta(String type) {
        super(KingdomWars.getInstance());
        this.type = type;
    }

    @Override
    public Object value() {
        return this.type;
    }

    @Override
    public void invalidate() {
        this.type = null;
    }
}
