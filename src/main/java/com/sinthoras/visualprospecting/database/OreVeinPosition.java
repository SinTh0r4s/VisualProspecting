package com.sinthoras.visualprospecting.database;

import com.sinthoras.visualprospecting.Utils;
import com.sinthoras.visualprospecting.database.veintypes.VeinType;
import com.sinthoras.visualprospecting.database.veintypes.VeinTypeCaching;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class OreVeinPosition implements Serializable {

    private static final int MAX_BYTES = 3 * Integer.BYTES + Byte.BYTES;

    public final int dimensionId;
    public final int chunkX;
    public final int chunkZ;
    public transient VeinType veinType;

    private transient boolean depleted = false;
    private transient boolean asWaypointActive = false;

    public OreVeinPosition(int dimensionId, int chunkX, int chunkZ, VeinType veinType) {
        this.dimensionId = dimensionId;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.veinType = veinType;
    }

    public OreVeinPosition(int dimensionId, int chunkX, int chunkZ, VeinType veinType, boolean depleted) {
        this.dimensionId = dimensionId;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.veinType = veinType;
        this.depleted = depleted;
    }

    public int getBlockX() {
        return Utils.coordChunkToBlock(chunkX) + 8;
    }

    public int getBlockZ() {
        return Utils.coordChunkToBlock(chunkZ) + 8;
    }

    public boolean isDepleted() {
        return  depleted;
    }

    public void toggleDepleted() {
        depleted = !depleted;
    }

    public OreVeinPosition joinDepletedState(final OreVeinPosition other) {
        depleted = depleted || other.depleted;
        return this;
    }

    public static int getMaxBytes() {
        return MAX_BYTES + VeinTypeCaching.getLongesOreNameLength();
    }
    
    public void triggerAsWaypointActive(boolean isActive) {
        asWaypointActive = isActive && asWaypointActive == false;
    }

    public boolean isAsWaypointActive() {
        return asWaypointActive;
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws Exception {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeShort(veinType.veinId);
    }

    private void readObject(ObjectInputStream objectInputStream) throws Exception {
        objectInputStream.defaultReadObject();
        veinType = VeinTypeCaching.getVeinType(objectInputStream.readShort());
    }
}
