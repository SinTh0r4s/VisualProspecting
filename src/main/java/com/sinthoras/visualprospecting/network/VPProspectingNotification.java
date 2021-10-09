package com.sinthoras.visualprospecting.network;

import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.database.VPServerCache;
import com.sinthoras.visualprospecting.database.veintypes.VPVeinTypeCaching;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

public class VPProspectingNotification implements IMessage {

    private int dimensionId;
    private List<VPServerCache.VPProspectionResult> prospectingResults;

    public VPProspectingNotification() {

    }

    public VPProspectingNotification(int dimensionId, List<VPServerCache.VPProspectionResult> prospectingResults) {
        this.dimensionId = dimensionId;
        this.prospectingResults = prospectingResults;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        prospectingResults = new ArrayList<>();
        dimensionId = buf.readInt();
        final int size = buf.readInt();
        for(int i=0;i<size;i++) {
            final int chunkX = buf.readInt();
            final int chunkZ = buf.readInt();
            final String oreVeinName = ByteBufUtils.readUTF8String(buf);
            prospectingResults.add(new VPServerCache.VPProspectionResult(chunkX, chunkZ, VPVeinTypeCaching.getVeinType(oreVeinName)));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(dimensionId);
        buf.writeInt(prospectingResults.size());
        for(VPServerCache.VPProspectionResult prospectionResult : prospectingResults) {
            buf.writeInt(prospectionResult.chunkX);
            buf.writeInt(prospectionResult.chunkZ);
            ByteBufUtils.writeUTF8String(buf, prospectionResult.veinType.name);
        }
    }

    public static class Handler implements IMessageHandler<VPProspectingNotification, IMessage> {

        @Override
        public IMessage onMessage(VPProspectingNotification message, MessageContext ctx) {
            VP.clientVeinCache.putVeinTypes(message.dimensionId, message.prospectingResults);
            return null;
        }
    }
}
