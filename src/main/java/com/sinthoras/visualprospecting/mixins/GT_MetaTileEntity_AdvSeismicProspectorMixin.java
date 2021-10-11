package com.sinthoras.visualprospecting.mixins;

import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.VPTags;
import com.sinthoras.visualprospecting.VPUtils;
import com.sinthoras.visualprospecting.database.VPOilFieldPosition;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_BasicMachine;
import gregtech.api.util.GT_Utility;
import gregtech.common.tileentities.machines.basic.GT_MetaTileEntity_AdvSeismicProspector;
import ic2.core.Ic2Items;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

import static gregtech.api.util.GT_Utility.ItemNBT.getNBT;
import static gregtech.api.util.GT_Utility.ItemNBT.setNBT;

@Mixin(GT_MetaTileEntity_AdvSeismicProspector.class)
public abstract class GT_MetaTileEntity_AdvSeismicProspectorMixin extends GT_MetaTileEntity_BasicMachine {

    @Shadow(remap = false)
    boolean ready = false;

    @Shadow(remap = false)
    int radius;

    public GT_MetaTileEntity_AdvSeismicProspectorMixin() {
        super(0, "", "", 0, 0, "", 0, 0, "", "", (ITexture[]) null);
    }

    /**
     * @author SinTh0r4s
     * @reason Redirect game mechanics onto VP database
     */
    @Overwrite(remap = false)
    @Override
    public boolean onRightclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
        if (aBaseMetaTileEntity.isServerSide()) {
            ItemStack aStack = aPlayer.getCurrentEquippedItem();

            if (!ready && (GT_Utility.consumeItems(aPlayer, aStack, Item.getItemFromBlock(Blocks.tnt), 16)
                    || GT_Utility.consumeItems(aPlayer, aStack, Ic2Items.industrialTnt.getItem(), 8)
                    || GT_Utility.consumeItems(aPlayer, aStack, Materials.Glyceryl, 4)
                    || GT_Utility.consumeItems(aPlayer, aStack, ItemList.Block_Powderbarrel.getItem(), 2))) {

                this.ready = true;
                this.mMaxProgresstime = (aPlayer.capabilities.isCreativeMode ? 20 : 800);

            } else if (ready && mMaxProgresstime == 0
                    && aStack != null && aStack.stackSize == 1
                    && aStack.getItem() == ItemList.Tool_DataStick.getItem()) {
                this.ready = false;

                final NBTTagCompound compound = getNBT(aStack);
                compound.setString(VPTags.BOOK_TITLE, "Raw Prospection Data");
                compound.setBoolean(VPTags.VISUALPROSPECTING_FLAG, true);
                compound.setByte(VPTags.PROSPECTION_TIER, mTier);
                compound.setInteger(VPTags.PROSPECTION_BLOCK_X, getBaseMetaTileEntity().getXCoord());
                compound.setInteger(VPTags.PROSPECTION_BLOCK_Y, getBaseMetaTileEntity().getYCoord());
                compound.setInteger(VPTags.PROSPECTION_BLOCK_Z, getBaseMetaTileEntity().getZCoord());
                compound.setInteger(VPTags.PROSPECTION_ORE_RADIUS, radius);

                final List<VPOilFieldPosition> oilFieldPositions = VP.serverCache.prospectOilBlockRadius(aPlayer.worldObj, getBaseMetaTileEntity().getXCoord(), getBaseMetaTileEntity().getZCoord(), VP.oilChunkProspectingRange);
                String[] oilStrings = new String[9];
                final int minOilFieldX = VPUtils.mapToCornerOilFieldChunkCoord(VPUtils.coordBlockToChunk(getBaseMetaTileEntity().getXCoord())) - VP.oilFieldSizeChunkX * VP.oilChunkProspectingRange;
                final int minOilFieldZ = VPUtils.mapToCornerOilFieldChunkCoord(VPUtils.coordBlockToChunk(getBaseMetaTileEntity().getZCoord())) - VP.oilFieldSizeChunkZ * VP.oilChunkProspectingRange;
                for(VPOilFieldPosition oilFieldPosition : oilFieldPositions) {
                    final int offsetOilfieldX = (VPUtils.mapToCornerOilFieldChunkCoord(oilFieldPosition.chunkX) - minOilFieldX) >> 3;
                    final int offsetOilfieldZ = (VPUtils.mapToCornerOilFieldChunkCoord(oilFieldPosition.chunkZ) - minOilFieldZ) >> 3;
                    final int oilFieldBookId = offsetOilfieldX + offsetOilfieldZ * 3;
                    oilStrings[oilFieldBookId] =  "" + oilFieldBookId + ": " + oilFieldPosition.oilField.getMinProduction() + "-" + oilFieldPosition.oilField.getMaxProduction() + " " + oilFieldPosition.oilField.oil.getLocalizedName(null);
                }
                compound.setString(VPTags.PROSPECTION_OILS, String.join("|", oilStrings));

                setNBT(aStack, compound);
            }
        }
        return true;
    }
}
