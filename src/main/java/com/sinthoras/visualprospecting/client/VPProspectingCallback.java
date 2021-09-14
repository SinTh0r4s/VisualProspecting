package com.sinthoras.visualprospecting.client;

import api.visualprospecting.VPProspectingCallbackHandler;
import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.VPConfig;
import com.sinthoras.visualprospecting.VPMod;
import com.sinthoras.visualprospecting.hooks.VPHooksClient;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.common.blocks.GT_Block_Ores_Abstract;
import gregtech.common.blocks.GT_TileEntity_Ores;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class VPProspectingCallback implements Runnable {

    @Override
    public void run() {
        GT_Block_Ores_Abstract.registerProspectingCallback(new VPProspectingCallbackHandler() {

            @Override
            public void prospectPotentialNewVein(World aWorld, int aX, int aY, int aZ, EntityPlayer aPlayer) {
                if(VPConfig.enableProspecting
                        && VPMod.proxy instanceof VPHooksClient
                        && Minecraft.getMinecraft().thePlayer == aPlayer) {
                            final TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
                            if (tTileEntity instanceof GT_TileEntity_Ores) {
                                final short oreMeta = ((GT_TileEntity_Ores) tTileEntity).mMetaData;
                                if (oreMeta < VP.gregTechSmallOreMinimumMeta)
                                    VPProspector.prospectPotentialNewVein(aWorld, aX, aY, aZ, oreMeta);
                            }
                }
            }
        });
    }
}
