package com.sinthoras.visualprospecting.network;

import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.VPConfig;
import com.sinthoras.visualprospecting.VPUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import gregtech.common.blocks.GT_Block_Ores_Abstract;
import gregtech.common.blocks.GT_TileEntity_Ores;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.UUID;

public class VPProspectingRequest implements IMessage {

    public static long timestampLastRequest = 0;

    private int dimensionId;
    private int blockX;
    private int blockY;
    private int blockZ;
    private short foundOreMetaData;

    public VPProspectingRequest() {

    }

    public VPProspectingRequest(int dimensionId, int blockX, int blockY, int blockZ, short foundOreMetaData) {
        this.dimensionId = dimensionId;
        this.blockX = blockX;
        this.blockY = blockY;
        this.blockZ = blockZ;
        this.foundOreMetaData = foundOreMetaData;
    }

    public static boolean canSendRequest() {
        final long timestamp = System.currentTimeMillis();
        if(timestamp - timestampLastRequest > VPConfig.minDelayBetweenVeinRequests) {
            timestampLastRequest = timestamp;
            return true;
        }
        return false;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        dimensionId = buf.readInt();
        blockX = buf.readInt();
        blockY = buf.readInt();
        blockZ = buf.readInt();
        foundOreMetaData = buf.readShort();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(dimensionId);
        buf.writeInt(blockX);
        buf.writeInt(blockY);
        buf.writeInt(blockZ);
        buf.writeShort(foundOreMetaData);
    }

    public static class Handler implements IMessageHandler<VPProspectingRequest, IMessage> {

        private static final HashMap<UUID, Long> lastRequestPerPlayer = new HashMap<>();

        @Override
        public IMessage onMessage(VPProspectingRequest message, MessageContext ctx) {
            final UUID uuid = ctx.getServerHandler().playerEntity.getUniqueID();
            final long lastRequest = lastRequestPerPlayer.containsKey(uuid) ? lastRequestPerPlayer.get(uuid) : 0;
            final long timestamp = System.currentTimeMillis();
            final float distanceSquared = ctx.getServerHandler().playerEntity.getPlayerCoordinates().getDistanceSquared(message.blockX, message.blockY, message.blockZ);
            final World world = ctx.getServerHandler().playerEntity.getEntityWorld();
            final int chunkX = VPUtils.coordBlockToChunk(message.blockX);
            final int chunkZ = VPUtils.coordBlockToChunk(message.blockZ);
            final boolean isChunkLoaded = world.getChunkProvider().chunkExists(chunkX, chunkZ);
            if(ctx.getServerHandler().playerEntity.dimension == message.dimensionId
                    && distanceSquared <= 1024  // max 32 blocks distance
                    && timestamp - lastRequest >= VPConfig.minDelayBetweenVeinRequests
                    && isChunkLoaded) {
                final Block block = world.getBlock(message.blockX, message.blockY, message.blockZ);
                if(block instanceof GT_Block_Ores_Abstract) {
                    final TileEntity tileEntity = world.getTileEntity(message.blockX, message.blockY, message.blockZ);
                    if (tileEntity instanceof GT_TileEntity_Ores) {
                        final short metaData = ((GT_TileEntity_Ores) tileEntity).mMetaData;
                        if(metaData <= 16000 && (metaData % 1000) == message.foundOreMetaData) {
                            lastRequestPerPlayer.put(uuid, timestamp);
                            final String oreVeinName = VP.serverVeinCache.getVeinType(message.dimensionId, chunkX, chunkZ).name;
                            // TODO: check neighboring ore chunks if ore is not in directly corresponding ore chunk
                            return new VPProspectingAnswer(message.dimensionId, chunkX, chunkZ, oreVeinName);
                        }
                    }
                }
            }
            return null;
        }
    }
}
