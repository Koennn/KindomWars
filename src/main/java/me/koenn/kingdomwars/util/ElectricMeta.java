package me.koenn.kingdomwars.util;

import me.koenn.kingdomwars.KingdomWars;
import org.bukkit.metadata.MetadataValueAdapter;

public class ElectricMeta extends MetadataValueAdapter {

    private boolean isElectric = true;

    public ElectricMeta() {
        super(KingdomWars.getInstance());
    }

    @Override
    public Object value() {
        return this.isElectric;
    }

    @Override
    public void invalidate() {
        this.isElectric = false;
    }
}
