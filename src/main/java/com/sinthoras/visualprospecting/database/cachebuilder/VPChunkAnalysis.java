package com.sinthoras.visualprospecting.database.cachebuilder;

import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.database.veintypes.VPVeinType;
import com.sinthoras.visualprospecting.database.veintypes.VPVeinTypeCaching;
import io.xol.enklume.nbt.*;

import java.util.HashSet;

// A slim, but faster version to identify >90% of veins
public class VPChunkAnalysis {

    private final HashSet<Short> ores = new HashSet<>();
    private final HashSet<VPVeinType> matchedVeins = new HashSet<>();
    private int minVeinBlockY = VP.minecraftWorldHeight;

    public void processMinecraftChunk(final NBTCompound chunkRoot) {
        for (final NBTNamed tileEntity : ((NBTList) chunkRoot.getTag("Level.TileEntities")).elements) {
            final VPGregTechOre gtOre = new VPGregTechOre((NBTCompound) tileEntity);
            if(gtOre.isValidGTOre) {
                ores.add(gtOre.metaData);
                if(minVeinBlockY > gtOre.blockY)
                    minVeinBlockY = gtOre.blockY;
            }
        }
    }

    public boolean matchesSingleVein() {
        for(VPVeinType veinType : VPVeinTypeCaching.veinTypes) {
            if(veinType.matches(ores))
                matchedVeins.add(veinType);
        }
        return matchedVeins.size() <= 1;
    }

    // Result only valid if matchesSingleVein() returned true
    public VPVeinType getMatchedVein() {
        if(matchedVeins.isEmpty())
            return VPVeinType.NO_VEIN;
        return matchedVeins.stream().findAny().get();
    }

    public int getVeinBlockY() {
        return minVeinBlockY;
    }
}