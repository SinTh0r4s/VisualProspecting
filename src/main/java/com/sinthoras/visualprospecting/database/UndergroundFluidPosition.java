package com.sinthoras.visualprospecting.database;

import com.sinthoras.visualprospecting.Utils;
import com.sinthoras.visualprospecting.VP;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;

public class UndergroundFluidPosition implements Serializable {

    public static final int BYTES = (3 + 1 + VP.undergroundFluidSizeChunkX * VP.undergroundFluidSizeChunkZ) * Integer.BYTES;

    public final int dimensionId;
    public final int chunkX;
    public final int chunkZ;
    public transient Fluid fluid;
    public final int[][] chunks;

    public static UndergroundFluidPosition getNotProspected(int dimensionId, int chunkX, int chunkZ) {
        return new UndergroundFluidPosition(dimensionId, chunkX, chunkZ, null, null);
    }

    public UndergroundFluidPosition(int dimensionId, int chunkX, int chunkZ, Fluid fluid, int[][] chunks) {
        this.dimensionId = dimensionId;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.fluid = fluid;
        this.chunks = chunks;
    }

    public int getBlockX() {
        return Utils.coordChunkToBlock(chunkX);
    }

    public int getBlockZ() {
        return Utils.coordChunkToBlock(chunkZ);
    }

    public int getMinProduction() {
        int smallest = Integer.MAX_VALUE;
        for(int chunkX = 0; chunkX < VP.undergroundFluidSizeChunkX; chunkX++) {
            for (int chunkZ = 0; chunkZ < VP.undergroundFluidSizeChunkZ; chunkZ++) {
                if (chunks[chunkX][chunkZ] < smallest) {
                    smallest = chunks[chunkX][chunkZ];
                }
            }
        }
        return smallest;
    }

    public int getMaxProduction() {
        int largest = Integer.MIN_VALUE;
        for(int chunkX = 0; chunkX < VP.undergroundFluidSizeChunkX; chunkX++) {
            for (int chunkZ = 0; chunkZ < VP.undergroundFluidSizeChunkZ; chunkZ++) {
                if (chunks[chunkX][chunkZ] > largest) {
                    largest = chunks[chunkX][chunkZ];
                }
            }
        }
        return largest;
    }

    public boolean isProspected() {
        return fluid != null;
    }

    public boolean equals(UndergroundFluidPosition other) {
        return dimensionId == other.dimensionId
                && chunkX == other.chunkX
                && chunkZ == other.chunkZ
                && fluid == other.fluid
                && Arrays.deepEquals(chunks, other.chunks);
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws Exception {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeInt(fluid.getID());
    }

    private void readObject(ObjectInputStream objectInputStream) throws Exception {
        objectInputStream.defaultReadObject();
        fluid = FluidRegistry.getFluid(objectInputStream.readInt());
    }
}
