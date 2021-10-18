package com.sinthoras.visualprospecting.network;

import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.database.UndergroundFluid;
import com.sinthoras.visualprospecting.database.UndergroundFluidPosition;
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

    private static List<UndergroundFluidPosition> emptyUndergroundFluidPositions = new ArrayList<>(0);

    private List<OreVeinPosition> oreVeinPositions;
    private List<UndergroundFluidPosition> undergroundFluidPositions;

    public ProspectingNotification() {

    }

    public ProspectingNotification(OreVeinPosition oreVeinPosition) {
        oreVeinPositions = Collections.singletonList(oreVeinPosition);
        undergroundFluidPositions = emptyUndergroundFluidPositions;
    }

    public ProspectingNotification(List<OreVeinPosition> oreVeinPositions, List<UndergroundFluidPosition> undergroundFluidPositions) {
        this.oreVeinPositions = oreVeinPositions;
        this.undergroundFluidPositions = undergroundFluidPositions;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        final int numberOfOreVeins = buf.readInt();
        oreVeinPositions = new ArrayList<>(numberOfOreVeins);
        for(int i=0;i<numberOfOreVeins;i++) {
            final int dimensionId = buf.readInt();
            final int chunkX = buf.readInt();
            final int chunkZ = buf.readInt();
            final String oreVeinName = ByteBufUtils.readUTF8String(buf);
            oreVeinPositions.add(new OreVeinPosition(dimensionId, chunkX, chunkZ, VeinTypeCaching.getVeinType(oreVeinName)));
        }
        final int numberOfUndergroundFluids = buf.readInt();
        undergroundFluidPositions = new ArrayList<>(numberOfUndergroundFluids);
        for(int i=0;i<numberOfUndergroundFluids;i++) {
            final int dimensionId = buf.readInt();
            final int chunkX = buf.readInt();
            final int chunkZ = buf.readInt();
            final Fluid fluid = FluidRegistry.getFluid(buf.readInt());
            final int[][] chunks = new int[VP.undergroundFluidSizeChunkX][VP.undergroundFluidSizeChunkZ];
            for(int offsetChunkX = 0; offsetChunkX < VP.undergroundFluidSizeChunkX; offsetChunkX++)
                for(int offsetChunkZ = 0; offsetChunkZ< VP.undergroundFluidSizeChunkZ; offsetChunkZ++) {
                    chunks[offsetChunkX][offsetChunkZ] = buf.readInt();
                }
            undergroundFluidPositions.add(new UndergroundFluidPosition(dimensionId, chunkX, chunkZ, new UndergroundFluid(fluid, chunks)));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(oreVeinPositions.size());
        for(OreVeinPosition oreVeinPosition : oreVeinPositions) {
            buf.writeInt(oreVeinPosition.dimensionId);
            buf.writeInt(oreVeinPosition.chunkX);
            buf.writeInt(oreVeinPosition.chunkZ);
            ByteBufUtils.writeUTF8String(buf, oreVeinPosition.veinType.name);
        }
        buf.writeInt(undergroundFluidPositions.size());
        for(UndergroundFluidPosition undergroundFluidPosition : undergroundFluidPositions) {
            buf.writeInt(undergroundFluidPosition.dimensionId);
            buf.writeInt(undergroundFluidPosition.chunkX);
            buf.writeInt(undergroundFluidPosition.chunkZ);
            buf.writeInt(undergroundFluidPosition.undergroundFluid.fluid.getID());
            for(int offsetChunkX = 0; offsetChunkX < VP.undergroundFluidSizeChunkX; offsetChunkX++) {
                for (int offsetChunkZ = 0; offsetChunkZ < VP.undergroundFluidSizeChunkZ; offsetChunkZ++) {
                    buf.writeInt(undergroundFluidPosition.undergroundFluid.chunks[offsetChunkX][offsetChunkZ]);
                }
            }
        }
    }

    public static class Handler implements IMessageHandler<ProspectingNotification, IMessage> {

        @Override
        public IMessage onMessage(ProspectingNotification message, MessageContext ctx) {
            VP.clientCache.putOreVeins(message.oreVeinPositions);
            VP.clientCache.putUndergroundFluids(message.undergroundFluidPositions);
            return null;
        }
    }
}
