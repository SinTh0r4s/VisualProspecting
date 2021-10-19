package com.sinthoras.visualprospecting.database;

import net.minecraft.entity.player.EntityPlayerMP;

import java.util.*;

/*
This class holds data to share between clients. It is wiped on server restart!
 */
public class TransferCache {

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
}
