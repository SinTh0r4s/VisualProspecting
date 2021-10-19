package com.sinthoras.visualprospecting.network;

import com.sinthoras.visualprospecting.VP;
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

    private static final List<UndergroundFluidPosition> emptyUndergroundFluids = new ArrayList<>(0);

    private List<OreVeinPosition> oreVeins;
    private List<UndergroundFluidPosition> undergroundFluids;

    public ProspectingNotification() {

    }

    public ProspectingNotification(OreVeinPosition oreVeinPosition) {
        oreVeins = Collections.singletonList(oreVeinPosition);
        undergroundFluids = emptyUndergroundFluids;
    }

    public ProspectingNotification(List<OreVeinPosition> oreVeins, List<UndergroundFluidPosition> undergroundFluids) {
        this.oreVeins = oreVeins;
        this.undergroundFluids = undergroundFluids;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        final int numberOfOreVeins = buf.readInt();
        oreVeins = new ArrayList<>(numberOfOreVeins);
        for(int i = 0; i < numberOfOreVeins; i++) {
            final int dimensionId = buf.readInt();
            final int chunkX = buf.readInt();
            final int chunkZ = buf.readInt();
            final String oreVeinName = ByteBufUtils.readUTF8String(buf);
            oreVeins.add(new OreVeinPosition(dimensionId, chunkX, chunkZ, VeinTypeCaching.getVeinType(oreVeinName)));
        }

        final int numberOfUndergroundFluids = buf.readInt();
        undergroundFluids = new ArrayList<>(numberOfUndergroundFluids);
        for(int i = 0; i < numberOfUndergroundFluids; i++) {
            final int dimensionId = buf.readInt();
            final int chunkX = buf.readInt();
            final int chunkZ = buf.readInt();
            final Fluid fluid = FluidRegistry.getFluid(buf.readInt());
            final int[][] chunks = new int[VP.undergroundFluidSizeChunkX][VP.undergroundFluidSizeChunkZ];
            for(int offsetChunkX = 0; offsetChunkX < VP.undergroundFluidSizeChunkX; offsetChunkX++)
                for(int offsetChunkZ = 0; offsetChunkZ < VP.undergroundFluidSizeChunkZ; offsetChunkZ++) {
                    chunks[offsetChunkX][offsetChunkZ] = buf.readInt();
                }
            undergroundFluids.add(new UndergroundFluidPosition(dimensionId, chunkX, chunkZ, fluid, chunks));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(oreVeins.size());
        for(OreVeinPosition oreVein : oreVeins) {
            buf.writeInt(oreVein.dimensionId);
            buf.writeInt(oreVein.chunkX);
            buf.writeInt(oreVein.chunkZ);
            ByteBufUtils.writeUTF8String(buf, oreVein.veinType.name);
        }

        buf.writeInt(undergroundFluids.size());
        for(UndergroundFluidPosition undergroundFluid : undergroundFluids) {
            buf.writeInt(undergroundFluid.dimensionId);
            buf.writeInt(undergroundFluid.chunkX);
            buf.writeInt(undergroundFluid.chunkZ);
            buf.writeInt(undergroundFluid.fluid.getID());
            for(int offsetChunkX = 0; offsetChunkX < VP.undergroundFluidSizeChunkX; offsetChunkX++) {
                for (int offsetChunkZ = 0; offsetChunkZ < VP.undergroundFluidSizeChunkZ; offsetChunkZ++) {
                    buf.writeInt(undergroundFluid.chunks[offsetChunkX][offsetChunkZ]);
                }
            }
        }
    }

    public static class Handler implements IMessageHandler<ProspectingNotification, IMessage> {

        @Override
        public IMessage onMessage(ProspectingNotification message, MessageContext ctx) {
            VP.clientCache.putOreVeins(message.oreVeins);
            VP.clientCache.putUndergroundFluids(message.undergroundFluids);
            return null;
        }
    }
}
