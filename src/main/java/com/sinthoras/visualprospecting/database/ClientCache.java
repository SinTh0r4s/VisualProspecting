package com.sinthoras.visualprospecting.database;

import com.sinthoras.visualprospecting.*;
import com.sinthoras.visualprospecting.database.veintypes.VeinType;
import com.sinthoras.visualprospecting.network.ProspectingRequest;
import gregtech.common.blocks.GT_TileEntity_Ores;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class ClientCache extends WorldCache {

    protected File getStorageDirectory() {
        final EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        return new File(Utils.getSubDirectory(Tags.CLIENT_DIR), player.getDisplayName() + "_" + player.getPersistentID().toString());
    }

    private void notifyNewOreVein(OreVeinPosition oreVeinPosition) {
        final String location = "(" + oreVeinPosition.getBlockX() + "," + oreVeinPosition.getBlockZ() + ")";
        final IChatComponent veinNotification = new ChatComponentTranslation("visualprospecting.vein.prospected", oreVeinPosition.veinType.getNameReadable(), location);
        veinNotification.getChatStyle().setItalic(true);
        veinNotification.getChatStyle().setColor(EnumChatFormatting.GRAY);
        Minecraft.getMinecraft().thePlayer.addChatMessage(veinNotification);

        final String oreNames = oreVeinPosition.veinType.getOreMaterials().stream().map(material -> material.mLocalizedName).collect(Collectors.joining(", "));
        final IChatComponent oresNotification = new ChatComponentTranslation("visualprospecting.vein.contents", oreNames);
        oresNotification.getChatStyle().setItalic(true);
        oresNotification.getChatStyle().setColor(EnumChatFormatting.GRAY);
        Minecraft.getMinecraft().thePlayer.addChatMessage(oresNotification);
    }

    public void putOreVeins(int dimensionId, List<OreVeinPosition> oreVeinPositions) {
        if(oreVeinPositions.size() == 1) {
            final OreVeinPosition oreVeinPosition = oreVeinPositions.get(0);
            if(putOreVein(dimensionId, oreVeinPosition.chunkX, oreVeinPosition.chunkZ, oreVeinPosition.veinType) != DimensionCache.UpdateResult.AlreadyKnown) {
                notifyNewOreVein(oreVeinPosition);
            }
        }
        else if(oreVeinPositions.size() > 1) {
            int newOreVeins = 0;
            for(OreVeinPosition oreVeinPosition : oreVeinPositions) {
                if(putOreVein(dimensionId, oreVeinPosition.chunkX, oreVeinPosition.chunkZ, oreVeinPosition.veinType) != DimensionCache.UpdateResult.AlreadyKnown) {
                    newOreVeins++;
                }
            }
            if(newOreVeins > 0) {
                final IChatComponent oreVeinNotification = new ChatComponentTranslation("visualprospecting.veins.prospected", newOreVeins);
                oreVeinNotification.getChatStyle().setItalic(true);
                oreVeinNotification.getChatStyle().setColor(EnumChatFormatting.GRAY);
                Minecraft.getMinecraft().thePlayer.addChatMessage(oreVeinNotification);
            }
        }
    }

    public void putUndergroundFluids(int dimensionId, List<UndergroundFluidPosition> undergroundFluids) {
        int newUndergroundFluids = 0;
        int updatedUndergroundFluids = 0;
        for(UndergroundFluidPosition undergroundFluidPosition : undergroundFluids) {
            DimensionCache.UpdateResult updateResult = putUndergroundFluids(dimensionId, undergroundFluidPosition.chunkX, undergroundFluidPosition.chunkZ, undergroundFluidPosition.undergroundFluid);
            if(updateResult == DimensionCache.UpdateResult.New) {
                newUndergroundFluids++;
            }
            if(updateResult == DimensionCache.UpdateResult.Updated) {
                updatedUndergroundFluids++;
            }
        }
        if(newUndergroundFluids > 0 && updatedUndergroundFluids > 0) {
            final IChatComponent undergroundFluidsNotification = new ChatComponentTranslation("visualprospecting.undergroundfluid.prospected.newandupdated", newUndergroundFluids, updatedUndergroundFluids);
            undergroundFluidsNotification.getChatStyle().setItalic(true);
            undergroundFluidsNotification.getChatStyle().setColor(EnumChatFormatting.GRAY);
            Minecraft.getMinecraft().thePlayer.addChatMessage(undergroundFluidsNotification);
        }
        else {
            if(newUndergroundFluids > 0) {
                final IChatComponent undergroundFluidsNotification = new ChatComponentTranslation("visualprospecting.undergroundfluid.prospected.onlynew", newUndergroundFluids);
                undergroundFluidsNotification.getChatStyle().setItalic(true);
                undergroundFluidsNotification.getChatStyle().setColor(EnumChatFormatting.GRAY);
                Minecraft.getMinecraft().thePlayer.addChatMessage(undergroundFluidsNotification);
            }
            if(updatedUndergroundFluids > 0) {
                final IChatComponent undergroundFluidsNotification = new ChatComponentTranslation("visualprospecting.undergroundfluid.prospected.onlyupdated", updatedUndergroundFluids);
                undergroundFluidsNotification.getChatStyle().setItalic(true);
                undergroundFluidsNotification.getChatStyle().setColor(EnumChatFormatting.GRAY);
                Minecraft.getMinecraft().thePlayer.addChatMessage(undergroundFluidsNotification);
            }
        }
    }

    public void onOreInteracted(World world, int blockX, int blockY, int blockZ, EntityPlayer entityPlayer) {
        if(Config.enableProspecting
                && Minecraft.getMinecraft().thePlayer == entityPlayer) {
            final TileEntity tTileEntity = world.getTileEntity(blockX, blockY, blockZ);
            if (tTileEntity instanceof GT_TileEntity_Ores) {
                final short oreMetaData = ((GT_TileEntity_Ores) tTileEntity).mMetaData;
                if (Utils.isSmallOreId(oreMetaData) == false
                        && oreMetaData != 0) {
                    final int chunkX = Utils.coordBlockToChunk(blockX);
                    final int chunkZ = Utils.coordBlockToChunk(blockZ);
                    final VeinType veinType = getOreVein(entityPlayer.dimension, chunkX, chunkZ);
                    final short materialId = Utils.oreIdToMaterialId(oreMetaData);
                    if(veinType.containsOre(materialId) == false && ProspectingRequest.canSendRequest()) {
                        VP.network.sendToServer(new ProspectingRequest(entityPlayer.dimension, blockX, blockY, blockZ, materialId));
                    }
                }
            }
        }
    }

    public void resetClientCache() {
        // TODO
    }
}
