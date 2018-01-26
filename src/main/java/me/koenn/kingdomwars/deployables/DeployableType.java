package me.koenn.kingdomwars.deployables;

import me.koenn.kingdomwars.deployables.executors.TurretExecutor;
import org.jnbt.CompoundTag;

public enum DeployableType {

    TURRET(TurretExecutor.class, null), HEALING_STATION(null, null);

    private final Class<? extends DeployableExecutor> executor;
    private final CompoundTag construction;

    DeployableType(Class<? extends DeployableExecutor> executor, CompoundTag construction) {
        this.executor = executor;
        this.construction = construction;
    }

    public Class<? extends DeployableExecutor> getExecutor() {
        return executor;
    }

    public CompoundTag getConstruction() {
        return construction;
    }
}
