package com.sinthoras.visualprospecting.database;

import com.sinthoras.visualprospecting.*;
import com.sinthoras.visualprospecting.database.veintypes.VPVeinType;
import com.sinthoras.visualprospecting.network.VPProspectingRequest;
import gregtech.common.blocks.GT_TileEntity_Ores;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

import java.io.File;
import java.util.stream.Collectors;

public class VPClientCache extends VPWorldCache{

    protected File getStorageDirectory() {
        return VPUtils.getSubDirectory(VPTags.CLIENT_DIR);
    }

    protected void onNewVein(VPVeinType veinType) {
        final IChatComponent veinNotification = new ChatComponentTranslation("visualprospecting.vein.prospected", veinType.getNameReadable());
        veinNotification.getChatStyle().setItalic(true);
        veinNotification.getChatStyle().setColor(EnumChatFormatting.GRAY);
        Minecraft.getMinecraft().thePlayer.addChatMessage(veinNotification);

        final String oreNames = veinType.getOreMaterials().stream().map(material -> material.mLocalizedName).collect(Collectors.joining(", "));
        final IChatComponent oresNotification = new ChatComponentTranslation("visualprospecting.vein.contents", oreNames);
        oresNotification.getChatStyle().setItalic(true);
        oresNotification.getChatStyle().setColor(EnumChatFormatting.GRAY);
        Minecraft.getMinecraft().thePlayer.addChatMessage(oresNotification);
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
                    final VPVeinType veinType = getVeinType(entityPlayer.dimension, chunkX, chunkZ);
                    final short materialId = VPUtils.oreIdToMaterialId(oreMetaData);
                    if(veinType.containsOre(materialId) == false && VPProspectingRequest.canSendRequest()) {
                        VP.network.sendToServer(new VPProspectingRequest(entityPlayer.dimension, blockX, blockY, blockZ, materialId));
                    }
                }
            }
        }
    }
}
