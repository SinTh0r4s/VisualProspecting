package com.sinthoras.visualprospecting.mixins;

import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.Tags;
import com.sinthoras.visualprospecting.Utils;
import com.sinthoras.visualprospecting.database.OilFieldPosition;
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
import net.minecraftforge.fluids.Fluid;
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

    private String getEnglishLocalization(Fluid oil) {
        switch(oil.getUnlocalizedName()) {
            case "gas_natural_gas":
                return "Natural Gas";
            case "liquid_light_oil":
                return "Light Oil";
            case "liquid_medium_oil":
                return "Raw Oil";
            case "liquid_heavy_oil":
                return "Heavy Oil";
            default:
                return oil.getLocalizedName(null);
        }
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

            }
            else if (ready && mMaxProgresstime == 0
                    && aStack != null && aStack.stackSize == 1
                    && aStack.getItem() == ItemList.Tool_DataStick.getItem()) {
                this.ready = false;

                final NBTTagCompound compound = getNBT(aStack);
                compound.setString(Tags.BOOK_TITLE, "Raw Prospection Data");
                compound.setBoolean(Tags.VISUALPROSPECTING_FLAG, true);
                compound.setByte(Tags.PROSPECTION_TIER, mTier);
                compound.setInteger(Tags.PROSPECTION_BLOCK_X, getBaseMetaTileEntity().getXCoord());
                compound.setInteger(Tags.PROSPECTION_BLOCK_Y, getBaseMetaTileEntity().getYCoord());
                compound.setInteger(Tags.PROSPECTION_BLOCK_Z, getBaseMetaTileEntity().getZCoord());
                compound.setInteger(Tags.PROSPECTION_ORE_RADIUS, radius);

                final List<OilFieldPosition> oilFieldPositions = VP.serverCache.prospectOilBlockRadius(aPlayer.worldObj, getBaseMetaTileEntity().getXCoord(), getBaseMetaTileEntity().getZCoord(), VP.oilChunkProspectingBlockRadius);
                String[] oilStrings = new String[9];
                final int minOilFieldX = Utils.mapToCornerOilFieldChunkCoord(Utils.coordBlockToChunk(getBaseMetaTileEntity().getXCoord() - VP.oilChunkProspectingBlockRadius));
                final int minOilFieldZ = Utils.mapToCornerOilFieldChunkCoord(Utils.coordBlockToChunk(getBaseMetaTileEntity().getZCoord() - VP.oilChunkProspectingBlockRadius));
                for(OilFieldPosition oilFieldPosition : oilFieldPositions) {
                    final int offsetOilfieldX = (Utils.mapToCornerOilFieldChunkCoord(oilFieldPosition.chunkX) - minOilFieldX) >> 3;
                    final int offsetOilfieldZ = (Utils.mapToCornerOilFieldChunkCoord(oilFieldPosition.chunkZ) - minOilFieldZ) >> 3;
                    final int oilFieldBookId = offsetOilfieldX + offsetOilfieldZ * 3;
                    oilStrings[oilFieldBookId] =  "" + oilFieldBookId + ": " + oilFieldPosition.oilField.getMinProduction() + "-" + oilFieldPosition.oilField.getMaxProduction() + " " + getEnglishLocalization(oilFieldPosition.oilField.oil);
                }
                compound.setString(Tags.PROSPECTION_OILS, String.join("|", oilStrings));

                setNBT(aStack, compound);
            }
        }
        return true;
    }
}
