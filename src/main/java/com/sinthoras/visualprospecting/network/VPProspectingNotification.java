package com.sinthoras.visualprospecting.network;

import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.database.VPOilField;
import com.sinthoras.visualprospecting.database.VPOilFieldPosition;
import com.sinthoras.visualprospecting.database.VPOreVeinPosition;
import com.sinthoras.visualprospecting.database.veintypes.VPVeinTypeCaching;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VPProspectingNotification implements IMessage {

    private static List<VPOilFieldPosition> emptyOilFieldPositions = new ArrayList<>(0);

    private int dimensionId;
    private List<VPOreVeinPosition> oreVeinPositions;
    private List<VPOilFieldPosition> oilFieldPositions;

    public VPProspectingNotification() {

    }

    public VPProspectingNotification(int dimensionId, VPOreVeinPosition oreVeinPosition) {
        this.dimensionId = dimensionId;
        oreVeinPositions = Collections.singletonList(oreVeinPosition);
        oilFieldPositions = emptyOilFieldPositions;
    }

    public VPProspectingNotification(int dimensionId, List<VPOreVeinPosition> oreVeinPositions, List<VPOilFieldPosition> oilFieldPositions) {
        this.dimensionId = dimensionId;
        this.oreVeinPositions = oreVeinPositions;
        this.oilFieldPositions = oilFieldPositions;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        dimensionId = buf.readInt();
        final int numberOfOreVeins = buf.readInt();
        oreVeinPositions = new ArrayList<>(numberOfOreVeins);
        for(int i=0;i<numberOfOreVeins;i++) {
            final int chunkX = buf.readInt();
            final int chunkZ = buf.readInt();
            final String oreVeinName = ByteBufUtils.readUTF8String(buf);
            oreVeinPositions.add(new VPOreVeinPosition(chunkX, chunkZ, VPVeinTypeCaching.getVeinType(oreVeinName)));
        }
        final int numberOfOilFields = buf.readInt();
        oilFieldPositions = new ArrayList<>(numberOfOilFields);
        for(int i=0;i<numberOfOilFields;i++) {
            final int chunkX = buf.readInt();
            final int chunkZ = buf.readInt();
            final Fluid oil = FluidRegistry.getFluid(buf.readInt());
            final int[][] chunks = new int[VP.oilFieldSizeChunkX][VP.oilFieldSizeChunkZ];
            for(int offsetChunkX = 0; offsetChunkX< VP.oilFieldSizeChunkX; offsetChunkX++)
                for(int offsetChunkZ = 0; offsetChunkZ< VP.oilFieldSizeChunkZ; offsetChunkZ++) {
                    chunks[offsetChunkX][offsetChunkZ] = buf.readInt();
                }
            oilFieldPositions.add(new VPOilFieldPosition(chunkX, chunkZ, new VPOilField(oil, chunks)));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(dimensionId);
        buf.writeInt(oreVeinPositions.size());
        for(VPOreVeinPosition oreVeinPosition : oreVeinPositions) {
            buf.writeInt(oreVeinPosition.chunkX);
            buf.writeInt(oreVeinPosition.chunkZ);
            ByteBufUtils.writeUTF8String(buf, oreVeinPosition.veinType.name);
        }
        buf.writeInt(oilFieldPositions.size());
        for(VPOilFieldPosition oilFieldPosition : oilFieldPositions) {
            buf.writeInt(oilFieldPosition.chunkX);
            buf.writeInt(oilFieldPosition.chunkZ);
            buf.writeInt(oilFieldPosition.oilField.oil.getID());
            for(int offsetChunkX = 0; offsetChunkX< VP.oilFieldSizeChunkX; offsetChunkX++)
                for(int offsetChunkZ = 0; offsetChunkZ< VP.oilFieldSizeChunkZ; offsetChunkZ++) {
                    buf.writeInt(oilFieldPosition.oilField.chunks[offsetChunkX][offsetChunkZ]);
                }
        }
    }

    public static class Handler implements IMessageHandler<VPProspectingNotification, IMessage> {

        @Override
        public IMessage onMessage(VPProspectingNotification message, MessageContext ctx) {
            VP.clientCache.putOreVeins(message.dimensionId, message.oreVeinPositions);
            VP.clientCache.putOilFields(message.dimensionId, message.oilFieldPositions);
            return null;
        }
    }
}
