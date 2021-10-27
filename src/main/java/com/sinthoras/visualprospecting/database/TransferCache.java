package com.sinthoras.visualprospecting.database;

import com.sinthoras.visualprospecting.Config;

import java.util.*;

public class TransferCache {

    public static final TransferCache instance = new TransferCache();

    private final Map<String, List<OreVeinPosition>> sharedOreVeins = new HashMap<>();
    private final Map<String, List<UndergroundFluidPosition>> sharedUndergroundFluids = new HashMap<>();
    private final Queue<String> timestamp = new LinkedList<>();

    public void addClientProspectionData(String uuid, List<OreVeinPosition> oreVeins, List<UndergroundFluidPosition> undergroundFluids) {
        sharedOreVeins.remove(uuid);
        sharedUndergroundFluids.remove(uuid);
        timestamp.remove(uuid);

        final int oreVeinPositionSizeInRam = OreVeinPosition.getMaxBytes() + 3 * Byte.BYTES;  // JVM represents everything aligned at 4 bytes
        final int newEntryBytes = oreVeins.size() * oreVeinPositionSizeInRam + undergroundFluids.size() * UndergroundFluidPosition.BYTES;

        while(getUsedMemory() > (Config.maxTransferCacheSizeMB << 20) - newEntryBytes
                && timestamp.isEmpty() == false) {
            String oldestUUID = timestamp.remove();
            sharedOreVeins.remove(oldestUUID);
            sharedUndergroundFluids.remove(oldestUUID);
        }

        sharedOreVeins.put(uuid, oreVeins);
        sharedUndergroundFluids.put(uuid, undergroundFluids);
        timestamp.add(uuid);
    }

    public boolean isClientDataAvailable(String uuid) {
        return sharedOreVeins.containsKey(uuid) && sharedUndergroundFluids.containsKey(uuid);
    }

    public List<OreVeinPosition> getSharedOreVeinsFrom(String uuid) {
        return sharedOreVeins.getOrDefault(uuid, new ArrayList<>());
    }

    public List<UndergroundFluidPosition> getSharedUndergroundFluidsFrom(String uuid) {
        return sharedUndergroundFluids.getOrDefault(uuid, new ArrayList<>());
    }

    private int getUsedMemory() {
        final int oreVeinPositionSizeInRam = OreVeinPosition.getMaxBytes() + 3 * Byte.BYTES;  // JVM represents everything aligned at 4 bytes
        return sharedOreVeins.values().stream().mapToInt(oreVeins -> oreVeins.size() * oreVeinPositionSizeInRam).sum()
                + sharedUndergroundFluids.values().stream().mapToInt(undergroundFluids -> undergroundFluids.size() * UndergroundFluidPosition.BYTES).sum();
    }
}
