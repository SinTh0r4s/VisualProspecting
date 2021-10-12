package com.sinthoras.visualprospecting.network;

import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.database.OilField;
import com.sinthoras.visualprospecting.database.OilFieldPosition;
import com.sinthoras.visualprospecting.database.OreVeinPosition;
import com.sinthoras.visualprospecting.database.veintypes.VeinTypeCaching;
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

public class ProspectingNotification implements IMessage {

    private static List<OilFieldPosition> emptyOilFieldPositions = new ArrayList<>(0);

    private int dimensionId;
    private List<OreVeinPosition> oreVeinPositions;
    private List<OilFieldPosition> oilFieldPositions;

    public ProspectingNotification() {

    }

    public ProspectingNotification(int dimensionId, OreVeinPosition oreVeinPosition) {
        this.dimensionId = dimensionId;
        oreVeinPositions = Collections.singletonList(oreVeinPosition);
        oilFieldPositions = emptyOilFieldPositions;
    }

    public ProspectingNotification(int dimensionId, List<OreVeinPosition> oreVeinPositions, List<OilFieldPosition> oilFieldPositions) {
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
            oreVeinPositions.add(new OreVeinPosition(chunkX, chunkZ, VeinTypeCaching.getVeinType(oreVeinName)));
        }
        final int numberOfOilFields = buf.readInt();
        oilFieldPositions = new ArrayList<>(numberOfOilFields);
        for(int i=0;i<numberOfOilFields;i++) {
            final int chunkX = buf.readInt();
            final int chunkZ = buf.readInt();
            final Fluid oil = FluidRegistry.getFluid(buf.readInt());
            final int[][] chunks = new int[VP.oilFieldSizeChunkX][VP.oilFieldSizeChunkZ];
            for(int offsetChunkX = 0; offsetChunkX < VP.oilFieldSizeChunkX; offsetChunkX++)
                for(int offsetChunkZ = 0; offsetChunkZ< VP.oilFieldSizeChunkZ; offsetChunkZ++) {
                    chunks[offsetChunkX][offsetChunkZ] = buf.readInt();
                }
            oilFieldPositions.add(new OilFieldPosition(chunkX, chunkZ, new OilField(oil, chunks)));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(dimensionId);
        buf.writeInt(oreVeinPositions.size());
        for(OreVeinPosition oreVeinPosition : oreVeinPositions) {
            buf.writeInt(oreVeinPosition.chunkX);
            buf.writeInt(oreVeinPosition.chunkZ);
            ByteBufUtils.writeUTF8String(buf, oreVeinPosition.veinType.name);
        }
        buf.writeInt(oilFieldPositions.size());
        for(OilFieldPosition oilFieldPosition : oilFieldPositions) {
            buf.writeInt(oilFieldPosition.chunkX);
            buf.writeInt(oilFieldPosition.chunkZ);
            buf.writeInt(oilFieldPosition.oilField.oil.getID());
            for(int offsetChunkX = 0; offsetChunkX < VP.oilFieldSizeChunkX; offsetChunkX++) {
                for (int offsetChunkZ = 0; offsetChunkZ < VP.oilFieldSizeChunkZ; offsetChunkZ++) {
                    buf.writeInt(oilFieldPosition.oilField.chunks[offsetChunkX][offsetChunkZ]);
                }
            }
        }
    }

    public static class Handler implements IMessageHandler<ProspectingNotification, IMessage> {

        @Override
        public IMessage onMessage(ProspectingNotification message, MessageContext ctx) {
            VP.clientCache.putOreVeins(message.dimensionId, message.oreVeinPositions);
            VP.clientCache.putOilFields(message.dimensionId, message.oilFieldPositions);
            return null;
        }
    }
}
