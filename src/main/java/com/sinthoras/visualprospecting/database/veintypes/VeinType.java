package com.sinthoras.visualprospecting.database.veintypes;

import com.sinthoras.visualprospecting.Tags;
import gregtech.api.GregTech_API;
import net.minecraft.util.EnumChatFormatting;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class VeinType {

    public static final int veinHeight = 9;

    public final String name;
    public short veinId;
    public final int blockSize;
    public final short primaryOreMeta;
    public final short secondaryOreMeta;
    public final short inBetweenOreMeta;
    public final short sporadicOreMeta;
    private final Set<Short> oresAsSet;
    private boolean isHighlighted = true;

    // Available after VisualProspecting post GT initialization
    public final static VeinType NO_VEIN = new VeinType(Tags.ORE_MIX_NONE_NAME, 0, (short)-1, (short)-1, (short)-1, (short)-1);

    public VeinType(String name, int blockSize, short primaryOreMeta, short secondaryOreMeta, short inBetweenOreMeta, short sporadicOreMeta)
    {
        this.name = name;
        this.blockSize = blockSize;
        this.primaryOreMeta = primaryOreMeta;
        this.secondaryOreMeta = secondaryOreMeta;
        this.inBetweenOreMeta = inBetweenOreMeta;
        this.sporadicOreMeta = sporadicOreMeta;
        oresAsSet = new HashSet<>();
        oresAsSet.add(primaryOreMeta);
        oresAsSet.add(secondaryOreMeta);
        oresAsSet.add(inBetweenOreMeta);
        oresAsSet.add(sporadicOreMeta);
    }

    public boolean matches(Set<Short> foundOres) {
        return foundOres.containsAll(oresAsSet);
    }

    public boolean matchesWithSpecificPrimaryOrSecondary(Set<Short> foundOres, short specificMeta) {
        return (primaryOreMeta == specificMeta || secondaryOreMeta == specificMeta) && foundOres.containsAll(oresAsSet);
    }

    public boolean canOverlapIntoNeighborOreChunk() {
        return blockSize > 24;
    }

    public boolean containsOre(short oreMetaData) {
        return primaryOreMeta == oreMetaData
                || secondaryOreMeta == oreMetaData
                || inBetweenOreMeta == oreMetaData
                || sporadicOreMeta == oreMetaData;
    }

    public List<String> getOreMaterialNames() {
        return oresAsSet.stream()
                .map(metaData -> GregTech_API.sGeneratedMaterials[metaData])
                .filter(Objects::nonNull)
                .map(material -> EnumChatFormatting.GRAY + material.mLocalizedName)
                .collect(Collectors.toList());
    }

    public String getNameReadable() {
        final String veinName = name.replace("ore.mix.custom.", "").replace("ore.mix.", "");
        return veinName.substring(0, 1).toUpperCase() + veinName.substring(1);
    }

    public Set<Short> getOresAtLayer(int layerBlockY) {
        final Set<Short> result = new HashSet<>();
        switch(layerBlockY) {
            case 0:
            case 1:
            case 2:
                result.add(secondaryOreMeta);
                result.add(sporadicOreMeta);
                return result;
            case 3:
                result.add(secondaryOreMeta);
                result.add(inBetweenOreMeta);
                result.add(sporadicOreMeta);
                return result;
            case 4:
                result.add(inBetweenOreMeta);
                result.add(sporadicOreMeta);
                return result;
            case 5:
            case 6:
                result.add(primaryOreMeta);
                result.add(inBetweenOreMeta);
                result.add(sporadicOreMeta);
                return result;
            case 7:
            case 8:
                result.add(primaryOreMeta);
                result.add(sporadicOreMeta);
                return result;
            default:
                return result;
        }
    }

    public boolean isHighlighted() {
        return isHighlighted;
    }

    public void setNEISearchHeighlight(boolean isHighlighted) {
        this.isHighlighted = isHighlighted;
    }
}
