package com.sinthoras.visualprospecting.database;

import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.VPUtils;
import scala.actors.threadpool.Arrays;

import java.util.Collections;

public class VPOilFieldPosition {
    public final int chunkX;
    public final int chunkZ;
    public final VPOilField oilField;

    public VPOilFieldPosition(int chunkX, int chunkZ, VPOilField oilField) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.oilField = oilField;
    }

    public int getBlockX() {
        return VPUtils.coordChunkToBlock(chunkX);
    }

    public int getBlockZ() {
        return VPUtils.coordChunkToBlock(chunkZ);
    }

    public int getMinProduction() {
        int smallest = Integer.MAX_VALUE;
        for(int chunkX=0;chunkX< VP.oilFieldSizeChunkX;chunkX++)
            for(int chunkZ=0;chunkZ< VP.oilFieldSizeChunkZ;chunkZ++) {
                if(oilField.chunks[chunkX][chunkZ] < smallest) {
                    smallest = oilField.chunks[chunkX][chunkZ];
                }
            }
        return smallest;
    }

    public int getMaxProduction() {
        int largest = Integer.MIN_VALUE;
        for(int chunkX=0;chunkX< VP.oilFieldSizeChunkX;chunkX++)
            for(int chunkZ=0;chunkZ< VP.oilFieldSizeChunkZ;chunkZ++) {
                if(oilField.chunks[chunkX][chunkZ] > largest) {
                    largest = oilField.chunks[chunkX][chunkZ];
                }
            }
        return largest;
    }
}
