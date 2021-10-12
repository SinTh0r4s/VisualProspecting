package com.sinthoras.visualprospecting.database.veintypes;

import codechicken.nei.NEIClientConfig;
import codechicken.nei.SearchField;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.sinthoras.visualprospecting.Tags;
import com.sinthoras.visualprospecting.Utils;
import gregtech.common.GT_Worldgen_GT_Ore_Layer;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static com.sinthoras.visualprospecting.Utils.isBartworksInstalled;
import static com.sinthoras.visualprospecting.database.veintypes.Reflection.*;

public class VeinTypeCaching implements Runnable {

    private static BiMap<Short, VeinType> veinTypeLookupTableForIds = HashBiMap.create();
    private static Map<String, VeinType> veinTypeLookupTableForNames = new HashMap<>();
    private static Map<String, Short> veinTypeStorageInfo;
    public static List<VeinType> veinTypes;
    public static HashSet<Short> largeVeinOres;

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
            veinTypes.add(new VeinType(vein.mWorldGenName, vein.mSize, vein.mPrimaryMeta, vein.mSecondaryMeta, vein.mBetweenMeta, vein.mSporadicMeta));
        }

        if(isBartworksInstalled()) {
            for(Object vein : getBWOreVeins()) {
                veinTypes.add(new VeinType(
                        getBWOreVeinName(vein),
                        getBWOreVeinSize(vein),
                        getBWOreVeinPrimaryMeta(vein),
                        getBWOreVeinSecondaryMeta(vein),
                        getBWOreVeinInBetweenMeta(vein),
                        getBWOreVeinSporadicMeta(vein)));
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
        final boolean isSearchActive = SearchField.searchInventories();
        final String searchString = NEIClientConfig.getSearchExpression().toLowerCase();

        for(VeinType veinType : veinTypes) {
            if(veinType != VeinType.NO_VEIN) {
                if (isSearchActive) {
                    List<String> searchableStrings = veinType.getOreMaterials().stream().filter(Objects::nonNull).map(material -> material.mLocalizedName + " ore").collect(Collectors.toList());
                    searchableStrings.add(veinType.getNameReadable() + " Vein");
                    searchableStrings = searchableStrings.stream().map(String::toLowerCase).filter(searchableString -> searchableString.contains(searchString)).collect(Collectors.toList());
                    veinType.setNEISearchHeighlight(searchableStrings.isEmpty() == false);
                } else {
                    veinType.setNEISearchHeighlight(true);
                }
            }
        }
    }
}
