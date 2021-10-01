package com.sinthoras.visualprospecting.database.veintypes;

import java.util.HashSet;
import java.util.Set;

public class VPVeinType {
    public static final int veinHeight = 9;

    public final String name;
    public short veinId;
    public final int size;
    public final short primaryOreMeta;
    public final short secondaryOreMeta;
    public final short inBetweenOreMeta;
    public final short sporadicOreMeta;
    private final HashSet<Short> oresAsHashSet;

    // Available after VisualProspecting post GT initialization
    public final static VPVeinType NO_VEIN = new VPVeinType("ore.mix.none", 0, (short)-1, (short)-1, (short)-1, (short)-1);

    public VPVeinType(String name, int size, short primaryOreMeta, short secondaryOreMeta, short inBetweenOreMeta, short sporadicOreMeta)
    {
        this.name = name;
        this.size = size;
        this.primaryOreMeta = primaryOreMeta;
        this.secondaryOreMeta = secondaryOreMeta;
        this.inBetweenOreMeta = inBetweenOreMeta;
        this.sporadicOreMeta = sporadicOreMeta;
        oresAsHashSet = new HashSet<>();
        oresAsHashSet.add(primaryOreMeta);
        oresAsHashSet.add(secondaryOreMeta);
        oresAsHashSet.add(inBetweenOreMeta);
        oresAsHashSet.add(sporadicOreMeta);
    }

    public boolean matches(Set<Short> foundOres) {
        return foundOres.containsAll(oresAsHashSet);
    }

    public boolean matchesWithSpecificPrimaryOrSecondary(Set<Short> foundOres, short specificMeta) {
        return (primaryOreMeta == specificMeta || secondaryOreMeta == specificMeta) && foundOres.containsAll(oresAsHashSet);
    }

    public boolean canOverlapIntoNeighborOreChunk() {
        return size > 24;
    }

    public HashSet<Short> getOresAtLayer(int layerBlockY) {
        final HashSet<Short> result = new HashSet<>();
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
}
