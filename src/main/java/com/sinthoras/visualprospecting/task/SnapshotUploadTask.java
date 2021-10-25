package com.sinthoras.visualprospecting.task;

import com.sinthoras.visualprospecting.Config;
import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.database.ClientCache;
import com.sinthoras.visualprospecting.database.OreVeinPosition;
import com.sinthoras.visualprospecting.database.UndergroundFluidPosition;
import com.sinthoras.visualprospecting.network.ProspectionSharing;

import java.util.List;

public class SnapshotUploadTask implements ITask {

    private final List<OreVeinPosition> oreVeins;
    private final List<UndergroundFluidPosition> undergroundFluids;
    private long lastUpload = 0;
    private boolean firstMessage = true;

    public SnapshotUploadTask() {
        oreVeins = ClientCache.instance.getAllOreVeins();
        undergroundFluids = ClientCache.instance.getAllUndergroundFluids();
    }

    @Override
    public boolean process() {
        final long timestamp = System.currentTimeMillis();
        if(timestamp - lastUpload > 1000 / Config.uploadPacketsPerSecond && listsEmpty() == false) {
            lastUpload = timestamp;
            final ProspectionSharing packet = new ProspectionSharing();

            final int addedOreVeins = packet.putOreVeins(oreVeins);
            oreVeins.subList(0, addedOreVeins).clear();

            final int addedUndergroundFluids = packet.putOreUndergroundFluids(undergroundFluids);
            undergroundFluids.subList(0, addedUndergroundFluids).clear();

            packet.setFirstMessage(firstMessage);
            firstMessage = false;

            packet.setLastMessage(listsEmpty());

            VP.network.sendToServer(packet);
        }
        return listsEmpty();
    }

    private boolean listsEmpty() {
        return oreVeins.isEmpty() && undergroundFluids.isEmpty();
    }
}
