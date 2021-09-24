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

    private static BiMap<Short, VPVeinType> veinTypeLookupTable = HashBiMap.create();
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
            veinTypes.add(new VPVeinType(vein.mWorldGenName, vein.mPrimaryMeta, vein.mSecondaryMeta, vein.mBetweenMeta, vein.mSporadicMeta));
            if(vein.mSize > 16) {
                // These veins can be larger then 3x3 chunks. For these ores we need to check multiple possible locations
                largeVeinOres.add(vein.mPrimaryMeta);
                largeVeinOres.add(vein.mSecondaryMeta);
                largeVeinOres.add(vein.mBetweenMeta);
                largeVeinOres.add(vein.mSporadicMeta);
            }
        }

        if(isBartworksInstalled()) {
            for(Object vein : getBWOreVeins()) {
                veinTypes.add(new VPVeinType(getBWOreVeinName(vein),
                                                getBWOreVeinPrimaryMeta(vein),
                                                getBWOreVeinSecondaryMeta(vein),
                                                getBWOreVeinInBetweenMeta(vein),
                                                getBWOreVeinSporadicMeta(vein)));
                if(getBWOreVeinSize(vein) > 16) {
                    // These veins can be larger then 3x3 chunks. For these ores we need to check multiple possible locations
                    largeVeinOres.add(getBWOreVeinPrimaryMeta(vein));
                    largeVeinOres.add(getBWOreVeinSecondaryMeta(vein));
                    largeVeinOres.add(getBWOreVeinInBetweenMeta(vein));
                    largeVeinOres.add(getBWOreVeinSporadicMeta(vein));
                }
            }
        }

        // Assign veinTypeIds for efficient storage
        loadVeinTypeStorageInfo();

        final Optional<Short> maxVeinTypeIdOptional = veinTypeStorageInfo.values().stream().max(Short::compare);
        short maxVeinTypeId = maxVeinTypeIdOptional.isPresent() ? maxVeinTypeIdOptional.get() : 0;

        for(VPVeinType veintype : veinTypes) {
            if(veinTypeStorageInfo.containsKey(veintype.name))
                veintype.veinId = veinTypeStorageInfo.get(veintype.name);
            else {
                maxVeinTypeId++;
                veintype.veinId = maxVeinTypeId;
                veinTypeStorageInfo.put(veintype.name, veintype.veinId);
            }
            // Build LUT (id <-> object)
            veinTypeLookupTable.put(veintype.veinId, veintype);
        }
        saveVeinTypeStorageInfo();
    }

    public static short getVeinTypeId(VPVeinType veinType) {
        return veinTypeLookupTable.inverse().get(veinType);
    }

    public static VPVeinType getVeinType(short veinTypeId) {
        if(veinTypeLookupTable.containsKey(veinTypeId))
            return veinTypeLookupTable.get(veinTypeId);
        else
            return VPVeinType.NO_VEIN;
    }

    private static File getVeinTypeStorageInfoFile() {
        return new File(VP.configFile.getParent() + "/" + VPTags.MODID + "_veintypes");
    }

    private static void loadVeinTypeStorageInfo() {
        veinTypeStorageInfo = VPUtils.readFileToMap(getVeinTypeStorageInfoFile());
    }

    private static void saveVeinTypeStorageInfo() {
        VPUtils.writeMapToFile(getVeinTypeStorageInfoFile(), veinTypeStorageInfo);
    }
}
