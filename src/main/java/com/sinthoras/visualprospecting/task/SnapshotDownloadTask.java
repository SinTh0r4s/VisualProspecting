package com.sinthoras.visualprospecting.task;

import com.sinthoras.visualprospecting.Config;
import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.database.OreVeinPosition;
import com.sinthoras.visualprospecting.database.TransferCache;
import com.sinthoras.visualprospecting.database.UndergroundFluidPosition;
import com.sinthoras.visualprospecting.network.ProspectionSharing;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.util.List;

public class SnapshotDownloadTask implements ITask {

    private final EntityPlayerMP player;
    private final List<OreVeinPosition> oreVeins;
    private final List<UndergroundFluidPosition> undergroundFluids;
    private long lastUpload = 0;
    private boolean firstMessage = true;

    public SnapshotDownloadTask(String authorUuid, EntityPlayerMP player) {
        this.player = player;
        oreVeins = TransferCache.instance.getSharedOreVeinsFrom(authorUuid);
        undergroundFluids = TransferCache.instance.getSharedUndergroundFluidsFrom(authorUuid);
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

            VP.network.sendTo(packet, player);

            if(listsEmpty()) {
                final IChatComponent notification = new ChatComponentTranslation("item.visualprospecting.prospectorslog.reading.end", player.getDisplayName());
                notification.getChatStyle().setItalic(true);
                notification.getChatStyle().setColor(EnumChatFormatting.GRAY);
                player.addChatMessage(notification);
            }
        }
        return listsEmpty();
    }

    private boolean listsEmpty() {
        return oreVeins.isEmpty() && undergroundFluids.isEmpty();
    }
}
