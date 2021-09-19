package com.sinthoras.visualprospecting.database.veintypes;

import java.util.HashSet;

public class VPVeinType {

    public final String name;
    public final short primaryOreMeta;
    public final short secondaryOreMeta;
    public final short inBetweenOreMeta;
    public final short sporadicOreMeta;
    private final HashSet<Short> oresAsHashSet;

    // Available after VisualProspecting post GT initialization
    public static VPVeinType NO_VEIN;

    public VPVeinType(String name, short primaryOreMeta, short secondaryOreMeta, short inBetweenOreMeta, short sporadicOreMeta)
    {
        this.name = name;
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

    public boolean matches(HashSet<Short> foundOres) {
        return oresAsHashSet.equals(foundOres);
    }

    public boolean partiallyMatches(HashSet<Short> foundOres) {
        return foundOres.containsAll(oresAsHashSet);
    }
}
