package me.koenn.kingdomwars.util;

import me.koenn.kingdomwars.KingdomWars;
import org.bukkit.metadata.MetadataValueAdapter;

public class StunMeta extends MetadataValueAdapter {

    public StunMeta() {
        super(KingdomWars.getInstance());
    }

    @Override
    public Object value() {
        return true;
    }

    @Override
    public void invalidate() {

    }
}
