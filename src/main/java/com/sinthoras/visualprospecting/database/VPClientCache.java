package com.sinthoras.visualprospecting.database;

import com.sinthoras.visualprospecting.*;
import com.sinthoras.visualprospecting.database.veintypes.VPVeinType;
import com.sinthoras.visualprospecting.network.VPProspectingRequest;
import gregtech.common.blocks.GT_TileEntity_Ores;
import journeymap.client.JourneymapClient;
import journeymap.common.Journeymap;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

import java.io.File;
import java.util.stream.Collectors;

public class VPClientCache extends VPWorldCache{

    protected File getStorageDirectory() {
        return VPUtils.getSubDirectory(VPTags.CLIENT_DIR);
    }

    protected void onNewVein(VPVeinType veinType) {
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentTranslation("visualprospecting.vein.prospected", veinType.getNameReadable()));

        final String oreNames = veinType.getOreMaterials().stream().map(material -> material.mLocalizedName).collect(Collectors.joining(", "));
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentTranslation("visualprospecting.vein.contents", oreNames));
    }

    public void onOreInteracted(World world, int blockX, int blockY, int blockZ, EntityPlayer entityPlayer) {
        if(VPConfig.enableProspecting
                && ((JourneymapClient) Journeymap.proxy).isMapping()
                && Minecraft.getMinecraft().thePlayer == entityPlayer) {
            final TileEntity tTileEntity = world.getTileEntity(blockX, blockY, blockZ);
            if (tTileEntity instanceof GT_TileEntity_Ores) {
                final short oreMetaData = ((GT_TileEntity_Ores) tTileEntity).mMetaData;
                if (oreMetaData < VP.gregTechSmallOreMinimumMeta
                        && oreMetaData != 0) {
                    final int chunkX = VPUtils.coordBlockToChunk(blockX);
                    final int chunkZ = VPUtils.coordBlockToChunk(blockZ);
                    final VPVeinType veinType = getVeinType(entityPlayer.dimension, chunkX, chunkZ);
                    if(veinType.containsOre((short)(oreMetaData % 1000)) == false
                            && VPProspectingRequest.canSendRequest()) {
                        VP.network.sendToServer(new VPProspectingRequest(entityPlayer.dimension, blockX, blockY, blockZ, (short)(oreMetaData % 1000)));
                    }
                }
            }
        }
    }
}
