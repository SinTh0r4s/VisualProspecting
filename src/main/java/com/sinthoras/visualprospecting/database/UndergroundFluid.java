package com.sinthoras.visualprospecting.database;

import com.sinthoras.visualprospecting.VP;
import net.minecraftforge.fluids.Fluid;

import java.util.Arrays;

public class UndergroundFluid {

    public static final UndergroundFluid NOT_PROSPECTED = new UndergroundFluid(new Fluid("no_fluid"), new int[][]{{0, 0, 0}, {0, 0, 0}, {0, 0, 0}});

    public final Fluid fluid;
    public final int[][] chunks;

    public UndergroundFluid(Fluid fluid, int[][] chunks) {
        this.fluid = fluid;
        this.chunks = chunks;
    }

    public boolean equals(UndergroundFluid other) {
        return fluid == other.fluid && Arrays.deepEquals(chunks, other.chunks);
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
}
