package com.sinthoras.visualprospecting.client.database;

import com.sinthoras.visualprospecting.VP;
import gregtech.common.GT_Worldgen_GT_Ore_Layer;

import java.util.ArrayList;
import java.util.List;

import static com.sinthoras.visualprospecting.VPUtils.isBartworksInstalled;
import static com.sinthoras.visualprospecting.client.VPReflection.*;

public class VPVeinCaching implements Runnable {

    public static List<VPVeinType> veinTypes;

    // BartWorks initializes veins in FML preInit
    // GalacticGreg initializes veins in FML postInit, but only copies all base game veins to make them available on all planets
    // GregTech initializes veins in a thread in FML postInit
    // Therefore, this method must be called after GregTech postInit
    public void run() {
        veinTypes = new ArrayList<>();
        for(GT_Worldgen_GT_Ore_Layer vein : GT_Worldgen_GT_Ore_Layer.sList) {
            veinTypes.add(new VPVeinType(vein.mWorldGenName, vein.mPrimaryMeta, vein.mSecondaryMeta, vein.mBetweenMeta, vein.mSporadicMeta));
        }

        if(isBartworksInstalled()) {
            for(Object vein : getBWOreVeins()) {
                veinTypes.add(new VPVeinType(getBWOreVeinName(vein),
                                                getBWOreVeinPrimaryMeta(vein),
                                                getBWOreVeinSecondaryMeta(vein),
                                                getBWOreVeinInBetweenMeta(vein),
                                                getBWOreVeinSporadicMeta((vein))));
            }
        }

        for(VPVeinType veinType : veinTypes) {
            VP.info(veinType.name + " " + veinType.primaryOreMeta + " " + veinType.secondaryOreMeta + " " + veinType.inBetweenOreMeta + " " + veinType.sporadicOreMeta);
        }
    }
}
