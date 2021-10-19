package com.sinthoras.visualprospecting.task;

import com.sinthoras.visualprospecting.Config;
import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.database.OreVeinPosition;
import com.sinthoras.visualprospecting.database.UndergroundFluidPosition;
import com.sinthoras.visualprospecting.network.ProspectionUpload;

import java.util.List;

public class SnapshotTask implements ITask {

    private final List<OreVeinPosition> oreVeins;
    private final List<UndergroundFluidPosition> undergroundFluids;
    private long lastUpload = 0;
    private boolean firstMessage = true;

    public SnapshotTask() {
        oreVeins = VP.clientCache.getAllOreVeins();
        undergroundFluids = VP.clientCache.getAllUndergroundFluids();
    }

    @Override
    public boolean process() {
        final long timestamp = System.currentTimeMillis();
        if(timestamp - lastUpload > 1000 / Config.uploadPacketsPerSecond
                && (oreVeins.isEmpty() == false || undergroundFluids.isEmpty() == false)) {
            lastUpload = timestamp;
            final ProspectionUpload packet = new ProspectionUpload();

            final int addedOreVeins = packet.putOreVeins(oreVeins);
            oreVeins.subList(0, addedOreVeins).clear();

            final int addedUndergroundFluids = packet.putOreUndergroundFluids(undergroundFluids);
            undergroundFluids.subList(0, addedUndergroundFluids).clear();

            packet.setFirstMessage(firstMessage);
            firstMessage = false;

            packet.setLastMessage(oreVeins.isEmpty() && undergroundFluids.isEmpty());

            VP.network.sendToServer(packet);
        }
        return oreVeins.isEmpty() && undergroundFluids.isEmpty();
    }
}
