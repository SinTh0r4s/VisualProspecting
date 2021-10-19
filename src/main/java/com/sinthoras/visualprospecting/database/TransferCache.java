package com.sinthoras.visualprospecting.database;

import java.io.Serializable;
import java.util.*;

public class TransferCache implements Serializable {

    private final Map<String, List<OreVeinPosition>> oreVeins = new HashMap<>();
    private final Map<String, List<UndergroundFluidPosition>> undergroundFluids = new HashMap<>();

    public void addClientProspectionData(String uuid, List<OreVeinPosition> oreVeins, List<UndergroundFluidPosition> undergroundFluids) {
        this.oreVeins.put(uuid, oreVeins);
        this.undergroundFluids.put(uuid, undergroundFluids);
    }

    public boolean isClientDataAvailable(String uuid) {
        return oreVeins.containsKey(uuid) && undergroundFluids.containsKey(uuid);
    }

    public List<OreVeinPosition> getSharedOreVeinsFrom(String uuid) {
        return oreVeins.getOrDefault(uuid, new ArrayList<>());
    }

    public List<UndergroundFluidPosition> getSharedUndergroundFluidsFrom(String uuid) {
        return undergroundFluids.getOrDefault(uuid, new ArrayList<>());
    }

    public boolean isEmpty() {
        return oreVeins.isEmpty() && undergroundFluids.isEmpty();
    }
}
