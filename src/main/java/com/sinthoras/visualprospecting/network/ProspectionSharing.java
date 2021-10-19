package com.sinthoras.visualprospecting.network;

import com.sinthoras.visualprospecting.Config;
import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.database.OreVeinPosition;
import com.sinthoras.visualprospecting.database.UndergroundFluidPosition;
import com.sinthoras.visualprospecting.database.veintypes.VeinTypeCaching;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProspectionSharing implements IMessage {

    private static final int BYTES_OVERHEAD = 2 * Byte.BYTES + 2 * Integer.BYTES;

    final List<OreVeinPosition> oreVeins = new ArrayList<>();
    final List<UndergroundFluidPosition> undergroundFluids = new ArrayList<>();
    private int bytesUsed = BYTES_OVERHEAD;
    boolean isFirstMessage = false;
    boolean isLastMessage = false;

    public ProspectionSharing() {

    }

    public int putOreVeins(List<OreVeinPosition> oreVeins) {
        final int availableBytes = Config.uploadSizePerPacket - bytesUsed;
        final int maxAddedOreVeins = availableBytes / OreVeinPosition.getMaxBytes();
        final int addedOreVeins = Math.min(oreVeins.size(), maxAddedOreVeins);
        this.oreVeins.addAll(oreVeins.subList(0, addedOreVeins));
        bytesUsed += addedOreVeins * OreVeinPosition.getMaxBytes();
        return addedOreVeins;
    }

    public int putOreUndergroundFluids(List<UndergroundFluidPosition> undergroundFluids) {
        final int availableBytes = Config.uploadSizePerPacket - bytesUsed;
        final int maxAddedUndergroundFluids = availableBytes / UndergroundFluidPosition.BYTES;
        final int addedUndergroundFluids = Math.min(undergroundFluids.size(), maxAddedUndergroundFluids);
        this.undergroundFluids.addAll(undergroundFluids.subList(0, addedUndergroundFluids));
        bytesUsed += addedUndergroundFluids * UndergroundFluidPosition.BYTES;
        return addedUndergroundFluids;
    }

    public void setFirstMessage(boolean isFirstMessage) {
        this.isFirstMessage = isFirstMessage;
    }

    public void setLastMessage(boolean isLastMessage) {
        this.isLastMessage = isLastMessage;
    }

    public int getBytes() {
        return BYTES_OVERHEAD
                + VeinTypeCaching.getLongesOreNameLength() * oreVeins.size()
                + UndergroundFluidPosition.BYTES * undergroundFluids.size();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        isFirstMessage = buf.readByte() > 0;
        isLastMessage = buf.readByte() > 0;

        final int numberOfOreVeins = buf.readInt();
        for(int i=0;i<numberOfOreVeins;i++) {
            final int dimensionId = buf.readInt();
            final int chunkX = buf.readInt();
            final int chunkZ = buf.readInt();
            final boolean isDepleted = buf.readByte() > 0;
            final String oreVeinName = ByteBufUtils.readUTF8String(buf);
            oreVeins.add(new OreVeinPosition(dimensionId, chunkX, chunkZ, VeinTypeCaching.getVeinType(oreVeinName), isDepleted));
        }

        final int numberOfUndergroundFluids = buf.readInt();
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
            undergroundFluids.add(new UndergroundFluidPosition(dimensionId, chunkX, chunkZ, fluid, chunks));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(isFirstMessage ? 1 : 0);
        buf.writeByte(isLastMessage ? 1 : 0);

        buf.writeInt(oreVeins.size());
        for(OreVeinPosition oreVein : oreVeins) {
            buf.writeInt(oreVein.dimensionId);
            buf.writeInt(oreVein.chunkX);
            buf.writeInt(oreVein.chunkZ);
            buf.writeByte(oreVein.isDepleted() ? 1 : 0);
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

    public static class ServerHandler implements IMessageHandler<ProspectionSharing, IMessage> {

        private static Map<EntityPlayerMP, List<OreVeinPosition>> oreVeins = new HashMap<>();
        private static Map<EntityPlayerMP, List<UndergroundFluidPosition>> undergroundFluids = new HashMap<>();

        @Override
        public IMessage onMessage(ProspectionSharing message, MessageContext ctx) {
            final EntityPlayerMP player = ctx.getServerHandler().playerEntity;

            // Optional todo: Integrate over time for proper checking
            if(message.getBytes() > Config.uploadSizePerPacket) {
                player.playerNetServerHandler.kickPlayerFromServer("Do not spam the server! Change your VisualProcessing configuration back to the servers!");
            }
            if(message.isFirstMessage) {
                oreVeins.put(player, new ArrayList<>());
                undergroundFluids.put(player, new ArrayList<>());
            }
            if(oreVeins.containsKey(player) == false || undergroundFluids.containsKey(player) == false) {
                return null;
            }
            oreVeins.get(player).addAll(message.oreVeins);
            undergroundFluids.get(player).addAll(message.undergroundFluids);
            if(message.isLastMessage) {
                VP.transferCache.addClientProspectionData(player.getPersistentID().toString(), oreVeins.get(player), undergroundFluids.get(player));
                oreVeins.remove(player);
                undergroundFluids.remove(player);
            }
            return null;
        }
    }

    public static class ClientHandler implements IMessageHandler<ProspectionSharing, IMessage> {

        private static List<OreVeinPosition> oreVeins;
        private static List<UndergroundFluidPosition> undergroundFluids;

        @Override
        public IMessage onMessage(ProspectionSharing message, MessageContext ctx) {
            if(message.isFirstMessage) {
                oreVeins= new ArrayList<>();
                undergroundFluids= new ArrayList<>();
            }
            if(oreVeins == null || undergroundFluids == null) {
                return null;
            }
            oreVeins.addAll(message.oreVeins);
            undergroundFluids.addAll(message.undergroundFluids);
            if(message.isLastMessage) {
                VP.clientCache.putOreVeins(oreVeins);
                VP.clientCache.putUndergroundFluids(undergroundFluids);
            }
            return null;
        }
    }
}
