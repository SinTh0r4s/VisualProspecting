package com.sinthoras.visualprospecting.client.database;

import com.sinthoras.visualprospecting.VP;
import gregtech.common.GT_Worldgen_GT_Ore_Layer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.sinthoras.visualprospecting.VPUtils.isBartworksInstalled;
import static com.sinthoras.visualprospecting.client.VPReflection.*;

public class VPVeinCaching implements Runnable {

    public static List<VPVeinType> veinTypes;
    public static HashSet<Short> largeVeinOres;

    // BartWorks initializes veins in FML preInit
    // GalacticGreg initializes veins in FML postInit, but only copies all base game veins to make them available on all planets
    // GregTech initializes veins in a thread in FML postInit
    // Therefore, this method must be called after GregTech postInit
    public void run() {
        veinTypes = new ArrayList<>();
        largeVeinOres = new HashSet<>();
        for(GT_Worldgen_GT_Ore_Layer vein : GT_Worldgen_GT_Ore_Layer.sList) {
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

        for(VPVeinType veinType : veinTypes) {
            VP.info(veinType.name + " " + veinType.primaryOreMeta + " " + veinType.secondaryOreMeta + " " + veinType.inBetweenOreMeta + " " + veinType.sporadicOreMeta);
        }
    }
}
