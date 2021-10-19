package com.sinthoras.visualprospecting.database.cachebuilder;

import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.database.veintypes.VeinType;
import com.sinthoras.visualprospecting.database.veintypes.VeinTypeCaching;
import io.xol.enklume.nbt.*;

import java.util.HashSet;
import java.util.Set;

// A slim, but faster version to identify >90% of veins
public class ChunkAnalysis {

    private final Set<Short> ores = new HashSet<>();
    private final Set<VeinType> matchedVeins = new HashSet<>();
    private int minVeinBlockY = VP.minecraftWorldHeight;

    public void processMinecraftChunk(final NBTCompound chunkRoot) {
        for (final NBTNamed tileEntity : ((NBTList) chunkRoot.getTag("Level.TileEntities")).elements) {
            final GregTechOre gtOre = new GregTechOre((NBTCompound) tileEntity);
            if(gtOre.isValidGTOre) {
                ores.add(gtOre.metaData);
                if(minVeinBlockY > gtOre.blockY) {
                    minVeinBlockY = gtOre.blockY;
                }
            }
        }
    }

    public boolean matchesSingleVein() {
        for(VeinType veinType : VeinTypeCaching.veinTypes) {
            if(veinType.matches(ores)) {
                matchedVeins.add(veinType);
            }
        }
        return matchedVeins.size() <= 1;
    }

    // Result only valid if matchesSingleVein() returned true
    public VeinType getMatchedVein() {
        if(matchedVeins.isEmpty()) {
            return VeinType.NO_VEIN;
        }
        return matchedVeins.stream().findAny().get();
    }

    public int getVeinBlockY() {
        return minVeinBlockY;
    }
}