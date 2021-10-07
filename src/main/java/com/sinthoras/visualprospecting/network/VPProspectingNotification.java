package com.sinthoras.visualprospecting.network;

import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.database.veintypes.VPVeinTypeCaching;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class VPProspectingNotification implements IMessage {

    private int dimensionId;
    private int chunkX;
    private int chunkZ;
    private String oreVeinName;

    public VPProspectingNotification() {

    }

    public VPProspectingNotification(int dimensionId, int chunkX, int chunkZ, String oreVeinName) {
        this.dimensionId = dimensionId;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.oreVeinName = oreVeinName;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        dimensionId = buf.readInt();
        chunkX = buf.readInt();
        chunkZ = buf.readInt();
        oreVeinName = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(dimensionId);
        buf.writeInt(chunkX);
        buf.writeInt(chunkZ);
        ByteBufUtils.writeUTF8String(buf, oreVeinName);
    }

    public static class Handler implements IMessageHandler<VPProspectingNotification, IMessage> {

        @Override
        public IMessage onMessage(VPProspectingNotification message, MessageContext ctx) {
            VP.clientVeinCache.putVeinType(message.dimensionId, message.chunkX, message.chunkZ, VPVeinTypeCaching.getVeinType(message.oreVeinName));
            return null;
        }
    }
}
