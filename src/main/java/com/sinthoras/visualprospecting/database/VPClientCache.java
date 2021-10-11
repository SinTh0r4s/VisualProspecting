package com.sinthoras.visualprospecting.database;

import com.sinthoras.visualprospecting.*;
import com.sinthoras.visualprospecting.database.veintypes.VPVeinType;
import com.sinthoras.visualprospecting.network.VPProspectingRequest;
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

public class VPClientCache extends VPWorldCache {

    protected File getStorageDirectory() {
        final EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        return new File(VPUtils.getSubDirectory(VPTags.CLIENT_DIR), player.getDisplayName() + "_" + player.getPersistentID().toString());
    }

    private void notifyNewOreVein(VPOreVeinPosition prospectionResult) {
        final String location = "(" + prospectionResult.getBlockX() + "," + prospectionResult.getBlockZ() + ")";
        final IChatComponent veinNotification = new ChatComponentTranslation("visualprospecting.vein.prospected", prospectionResult.veinType.getNameReadable(), location);
        veinNotification.getChatStyle().setItalic(true);
        veinNotification.getChatStyle().setColor(EnumChatFormatting.GRAY);
        Minecraft.getMinecraft().thePlayer.addChatMessage(veinNotification);

        final String oreNames = prospectionResult.veinType.getOreMaterials().stream().map(material -> material.mLocalizedName).collect(Collectors.joining(", "));
        final IChatComponent oresNotification = new ChatComponentTranslation("visualprospecting.vein.contents", oreNames);
        oresNotification.getChatStyle().setItalic(true);
        oresNotification.getChatStyle().setColor(EnumChatFormatting.GRAY);
        Minecraft.getMinecraft().thePlayer.addChatMessage(oresNotification);
    }

    public void putOreVeins(int dimensionId, List<VPOreVeinPosition> oreVeinPositions) {
        if(oreVeinPositions.size() == 1) {
            final VPOreVeinPosition vpOreVeinPosition = oreVeinPositions.get(0);
            if(putOreVein(dimensionId, vpOreVeinPosition.chunkX, vpOreVeinPosition.chunkZ, vpOreVeinPosition.veinType) != VPDimensionCache.UpdateResult.AlreadyKnown) {
                notifyNewOreVein(vpOreVeinPosition);
            }
        }
        else if(oreVeinPositions.size() > 1) {
            int newOreVeins = 0;
            for(VPOreVeinPosition oreVeinPosition : oreVeinPositions) {
                if(putOreVein(dimensionId, oreVeinPosition.chunkX, oreVeinPosition.chunkZ, oreVeinPosition.veinType) != VPDimensionCache.UpdateResult.AlreadyKnown) {
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

    public void putOilFields(int dimensionId, List<VPOilFieldPosition> oilFields) {
        int newOilFields = 0;
        int updatedOilFields = 0;
        for(VPOilFieldPosition oilFieldPosition : oilFields) {
            VPDimensionCache.UpdateResult updateResult = putOilField(dimensionId, oilFieldPosition.chunkX, oilFieldPosition.chunkZ, oilFieldPosition.oilField);
            if(updateResult == VPDimensionCache.UpdateResult.New) {
                newOilFields++;
            }
            if(updateResult == VPDimensionCache.UpdateResult.Updated) {
                updatedOilFields++;
            }
        }
        if(newOilFields > 0 && updatedOilFields > 0) {
            final IChatComponent oreVeinNotification = new ChatComponentTranslation("visualprospecting.oilfields.prospected.newandupdated", newOilFields, updatedOilFields);
            oreVeinNotification.getChatStyle().setItalic(true);
            oreVeinNotification.getChatStyle().setColor(EnumChatFormatting.GRAY);
            Minecraft.getMinecraft().thePlayer.addChatMessage(oreVeinNotification);
        }
        else {
            if(newOilFields > 0) {
                final IChatComponent oreVeinNotification = new ChatComponentTranslation("visualprospecting.oilfields.prospected.onlynew", newOilFields);
                oreVeinNotification.getChatStyle().setItalic(true);
                oreVeinNotification.getChatStyle().setColor(EnumChatFormatting.GRAY);
                Minecraft.getMinecraft().thePlayer.addChatMessage(oreVeinNotification);
            }
            if(updatedOilFields > 0) {
                final IChatComponent oreVeinNotification = new ChatComponentTranslation("visualprospecting.oilfields.prospected.onlyupdated", updatedOilFields);
                oreVeinNotification.getChatStyle().setItalic(true);
                oreVeinNotification.getChatStyle().setColor(EnumChatFormatting.GRAY);
                Minecraft.getMinecraft().thePlayer.addChatMessage(oreVeinNotification);
            }
        }
    }

    public void onOreInteracted(World world, int blockX, int blockY, int blockZ, EntityPlayer entityPlayer) {
        if(VPConfig.enableProspecting
                && Minecraft.getMinecraft().thePlayer == entityPlayer) {
            final TileEntity tTileEntity = world.getTileEntity(blockX, blockY, blockZ);
            if (tTileEntity instanceof GT_TileEntity_Ores) {
                final short oreMetaData = ((GT_TileEntity_Ores) tTileEntity).mMetaData;
                if (VPUtils.isSmallOreId(oreMetaData) == false
                        && oreMetaData != 0) {
                    final int chunkX = VPUtils.coordBlockToChunk(blockX);
                    final int chunkZ = VPUtils.coordBlockToChunk(blockZ);
                    final VPVeinType veinType = getOreVein(entityPlayer.dimension, chunkX, chunkZ);
                    final short materialId = VPUtils.oreIdToMaterialId(oreMetaData);
                    if(veinType.containsOre(materialId) == false && VPProspectingRequest.canSendRequest()) {
                        VP.network.sendToServer(new VPProspectingRequest(entityPlayer.dimension, blockX, blockY, blockZ, materialId));
                    }
                }
            }
        }
    }
}
