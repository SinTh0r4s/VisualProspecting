package com.sinthoras.visualprospecting.database.veintypes;

import codechicken.nei.NEIClientConfig;
import codechicken.nei.SearchField;
import com.github.bartimaeusnek.bartworks.system.material.Werkstoff;
import com.github.bartimaeusnek.bartworks.system.oregen.BW_OreLayer;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.sinthoras.visualprospecting.Tags;
import com.sinthoras.visualprospecting.Utils;
import gregtech.api.GregTech_API;
import gregtech.api.enums.Materials;
import gregtech.common.GT_Worldgen_GT_Ore_Layer;
import net.minecraft.client.resources.I18n;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static com.sinthoras.visualprospecting.Utils.*;

public class VeinTypeCaching implements Runnable {

    private static BiMap<Short, VeinType> veinTypeLookupTableForIds = HashBiMap.create();
    private static Map<String, VeinType> veinTypeLookupTableForNames = new HashMap<>();
    private static Map<String, Short> veinTypeStorageInfo;
    public static List<VeinType> veinTypes;
    public static Set<Short> largeVeinOres;
    private static int longesOreName = 0;

    // BartWorks initializes veins in FML preInit
    // GalacticGreg initializes veins in FML postInit, but only copies all base game veins to make them available on all planets
    // GregTech initializes veins in a thread in FML postInit
    // Therefore, this method must be called after GregTech postInit
    public void run() {
        veinTypes = new ArrayList<>();
        largeVeinOres = new HashSet<>();
        veinTypes.add(VeinType.NO_VEIN);

        for(GT_Worldgen_GT_Ore_Layer vein : GT_Worldgen_GT_Ore_Layer.sList) {
            if(vein.mWorldGenName.equals(Tags.ORE_MIX_NONE_NAME)) {
                break;
            }
            Materials material = GregTech_API.sGeneratedMaterials[vein.mPrimaryMeta];
            if(material == null) {
                // Some materials are not registered in dev when their usage mod is not available.
                material = Materials.getAll().stream().filter(m -> m.mMetaItemSubID == vein.mPrimaryMeta).findAny().get();
            }

            veinTypes.add(new VeinType(
                    vein.mWorldGenName,
                    new GregTechOreMaterialProvider(material),
                    vein.mSize,
                    vein.mPrimaryMeta,
                    vein.mSecondaryMeta,
                    vein.mBetweenMeta,
                    vein.mSporadicMeta,
                    Math.max(0, vein.mMinY - 6),  // GregTech ore veins start at layer -1 and the blockY RNG adds another -5 offset
                    Math.min(255, vein.mMaxY - 6)));
        }

        if(isBartworksInstalled()) {
            for(BW_OreLayer vein : BW_OreLayer.sList) {
                veinTypes.add(new VeinType(
                        vein.mWorldGenName,
                        new BartworksOreMaterialProvider(Werkstoff.werkstoffHashMap.get((short) vein.mPrimaryMeta)),
                        vein.mSize,
                        (short) vein.mPrimaryMeta,
                        (short) vein.mSecondaryMeta,
                        (short) vein.mBetweenMeta,
                        (short) vein.mSporadicMeta,
                        Math.max(0, vein.mMinY),
                        Math.min(255, vein.mMaxY)));
            }
        }

        // Assign veinTypeIds for efficient storage
        loadVeinTypeStorageInfo();

        final Optional<Short> maxVeinTypeIdOptional = veinTypeStorageInfo.values().stream().max(Short::compare);
        short maxVeinTypeId = maxVeinTypeIdOptional.isPresent() ? maxVeinTypeIdOptional.get() : 0;

        for(VeinType veinType : veinTypes) {
            if(veinTypeStorageInfo.containsKey(veinType.name)) {
                veinType.veinId = veinTypeStorageInfo.get(veinType.name);
            }
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

        for(VeinType veinType : veinTypes) {
            if(veinType.name.length() > longesOreName) {
                longesOreName = veinType.name.length();
            }
        }
    }

    public static int getLongesOreNameLength() {
        return longesOreName;
    }

    public static short getVeinTypeId(VeinType veinType) {
        return veinTypeLookupTableForIds.inverse().get(veinType);
    }

    public static VeinType getVeinType(short veinTypeId) {
        return veinTypeLookupTableForIds.getOrDefault(veinTypeId, VeinType.NO_VEIN);
    }

    public static VeinType getVeinType(String veinTypeName) {
        return veinTypeLookupTableForNames.getOrDefault(veinTypeName, VeinType.NO_VEIN);
    }

    private static File getVeinTypeStorageInfoFile() {
        final File directory = Utils.getSubDirectory(Tags.VISUALPROSPECTING_DIR);
        directory.mkdirs();
        return new File(directory, "veintypesLUT");
    }

    private static void loadVeinTypeStorageInfo() {
        veinTypeStorageInfo = Utils.readFileToMap(getVeinTypeStorageInfoFile());
    }

    private static void saveVeinTypeStorageInfo() {
        Utils.writeMapToFile(getVeinTypeStorageInfoFile(), veinTypeStorageInfo);
    }

    public static void recalculateNEISearch() {
        if(isNEIInstalled()) {
            final boolean isSearchActive = SearchField.searchInventories();
            final String searchString = NEIClientConfig.getSearchExpression().toLowerCase();

            for (VeinType veinType : veinTypes) {
                if (veinType != VeinType.NO_VEIN) {
                    if (isSearchActive) {
                        List<String> searchableStrings = veinType.getOreMaterialNames();
                        searchableStrings.add(I18n.format(veinType.name));
                        searchableStrings = searchableStrings.stream().map(String::toLowerCase).filter(searchableString -> searchableString.contains(searchString)).collect(Collectors.toList());
                        veinType.setNEISearchHeighlight(searchableStrings.isEmpty() == false);
                    } else {
                        veinType.setNEISearchHeighlight(true);
                    }
                }
            }
        }
    }
}
