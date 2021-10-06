package com.sinthoras.visualprospecting.database.veintypes;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.VPTags;
import com.sinthoras.visualprospecting.VPUtils;
import gregtech.common.GT_Worldgen_GT_Ore_Layer;

import java.io.File;
import java.util.*;

import static com.sinthoras.visualprospecting.VPUtils.isBartworksInstalled;
import static com.sinthoras.visualprospecting.database.veintypes.VPReflection.*;

public class VPVeinTypeCaching implements Runnable {

    private static BiMap<Short, VPVeinType> veinTypeLookupTableForIds = HashBiMap.create();
    private static Map<String, VPVeinType> veinTypeLookupTableForNames = new HashMap<>();
    private static Map<String, Short> veinTypeStorageInfo;
    public static List<VPVeinType> veinTypes;
    public static HashSet<Short> largeVeinOres;

    // BartWorks initializes veins in FML preInit
    // GalacticGreg initializes veins in FML postInit, but only copies all base game veins to make them available on all planets
    // GregTech initializes veins in a thread in FML postInit
    // Therefore, this method must be called after GregTech postInit
    public void run() {
        veinTypes = new ArrayList<>();
        largeVeinOres = new HashSet<>();
        veinTypes.add(VPVeinType.NO_VEIN);

        for(GT_Worldgen_GT_Ore_Layer vein : GT_Worldgen_GT_Ore_Layer.sList) {
            if(vein.mWorldGenName.equals("ore.mix.none"))
                break;
            veinTypes.add(new VPVeinType(vein.mWorldGenName, vein.mSize, vein.mPrimaryMeta, vein.mSecondaryMeta, vein.mBetweenMeta, vein.mSporadicMeta));
        }

        if(isBartworksInstalled()) {
            for(Object vein : getBWOreVeins())
                veinTypes.add(new VPVeinType(getBWOreVeinName(vein),
                                                getBWOreVeinSize(vein),
                                                getBWOreVeinPrimaryMeta(vein),
                                                getBWOreVeinSecondaryMeta(vein),
                                                getBWOreVeinInBetweenMeta(vein),
                                                getBWOreVeinSporadicMeta(vein)));
        }

        // Assign veinTypeIds for efficient storage
        loadVeinTypeStorageInfo();

        final Optional<Short> maxVeinTypeIdOptional = veinTypeStorageInfo.values().stream().max(Short::compare);
        short maxVeinTypeId = maxVeinTypeIdOptional.isPresent() ? maxVeinTypeIdOptional.get() : 0;

        for(VPVeinType veinType : veinTypes) {
            if(veinTypeStorageInfo.containsKey(veinType.name))
                veinType.veinId = veinTypeStorageInfo.get(veinType.name);
            else {
                maxVeinTypeId++;
                veinType.veinId = maxVeinTypeId;
                veinTypeStorageInfo.put(veinType.name, veinType.veinId);
            }
            // Build LUT (id <-> object)
            veinTypeLookupTableForIds.put(veinType.veinId, veinType);

            // Build LUT (name -> object)
            veinTypeLookupTableForNames.put(veinType.name, veinType);

            // Build large vein LUT
            if(veinType.canOverlapIntoNeighborOreChunk()) {
                largeVeinOres.add(veinType.primaryOreMeta);
                largeVeinOres.add(veinType.secondaryOreMeta);
                largeVeinOres.add(veinType.inBetweenOreMeta);
                largeVeinOres.add(veinType.sporadicOreMeta);
            }
        }
        saveVeinTypeStorageInfo();
    }

    public static short getVeinTypeId(VPVeinType veinType) {
        return veinTypeLookupTableForIds.inverse().get(veinType);
    }

    public static VPVeinType getVeinType(short veinTypeId) {
        return veinTypeLookupTableForIds.getOrDefault(veinTypeId, VPVeinType.NO_VEIN);
    }

    public static VPVeinType getVeinType(String veinTypeName) {
        return veinTypeLookupTableForNames.getOrDefault(veinTypeName, VPVeinType.NO_VEIN);
    }

    private static File getVeinTypeStorageInfoFile() {
        final File directory = VPUtils.getSubDirectory(VPTags.VISUALPROSPECTING_DIR);
        directory.mkdirs();
        return new File(directory, "veintypesLUT");
    }

    private static void loadVeinTypeStorageInfo() {
        veinTypeStorageInfo = VPUtils.readFileToMap(getVeinTypeStorageInfoFile());
    }

    private static void saveVeinTypeStorageInfo() {
        VPUtils.writeMapToFile(getVeinTypeStorageInfoFile(), veinTypeStorageInfo);
    }
}
