package com.sinthoras.visualprospecting.tileentities;

import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.VPTags;
import com.sinthoras.visualprospecting.database.VPServerCache;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_BasicMachine;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GT_Utility;
import ic2.core.Ic2Items;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import java.util.List;

import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_BOTTOM_ROCK_BREAKER_GLOW;
import static gregtech.api.util.GT_Utility.ItemNBT.getNBT;
import static gregtech.api.util.GT_Utility.ItemNBT.setNBT;

public class VPAdvancedSeismicProspector extends GT_MetaTileEntity_BasicMachine {

    boolean ready = false;
    int radius;
    int step;

    public VPAdvancedSeismicProspector(int aID, String aName, String aNameRegional, int aTier, int aRadius, int aStep) {
        super(aID, aName, aNameRegional, aTier, 1, // amperage
                "",
                1, // input slot count
                1, // output slot count
                "Default.png", // GUI name
                "", // NEI name
                TextureFactory.of(
                        TextureFactory.of(OVERLAY_SIDE_ROCK_BREAKER_ACTIVE),
                        TextureFactory.builder().addIcon(OVERLAY_SIDE_ROCK_BREAKER_ACTIVE_GLOW).glow().build()),
                TextureFactory.of(
                        TextureFactory.of(OVERLAY_SIDE_ROCK_BREAKER),
                        TextureFactory.builder().addIcon(OVERLAY_SIDE_ROCK_BREAKER_GLOW).glow().build()),
                TextureFactory.of(
                        TextureFactory.of(OVERLAY_TOP_ROCK_BREAKER_ACTIVE),
                        TextureFactory.builder().addIcon(OVERLAY_TOP_ROCK_BREAKER_ACTIVE_GLOW).glow().build()),
                TextureFactory.of(
                        TextureFactory.of(OVERLAY_TOP_ROCK_BREAKER),
                        TextureFactory.builder().addIcon(OVERLAY_TOP_ROCK_BREAKER_GLOW).glow().build()),
                TextureFactory.of(
                        TextureFactory.of(OVERLAY_FRONT_ROCK_BREAKER_ACTIVE),
                        TextureFactory.builder().addIcon(OVERLAY_FRONT_ROCK_BREAKER_ACTIVE_GLOW).glow().build()),
                TextureFactory.of(
                        TextureFactory.of(OVERLAY_FRONT_ROCK_BREAKER),
                        TextureFactory.builder().addIcon(OVERLAY_FRONT_ROCK_BREAKER_GLOW).glow().build()),
                TextureFactory.of(
                        TextureFactory.of(OVERLAY_BOTTOM_ROCK_BREAKER_ACTIVE),
                        TextureFactory.builder().addIcon(OVERLAY_BOTTOM_ROCK_BREAKER_ACTIVE_GLOW).glow().build()),
                TextureFactory.of(
                        TextureFactory.of(OVERLAY_BOTTOM_ROCK_BREAKER),
                        TextureFactory.builder().addIcon(OVERLAY_BOTTOM_ROCK_BREAKER_GLOW).glow().build()));
        radius = aRadius;
        step = aStep;
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                "Place, activate with explosives",
                "2 Powderbarrels, "
                        + "4 Glyceryl Trinitrate, "
                        + "16 TNT, or "
                        + "8 ITNT",
                "Use Data Stick, Scan Data Stick, Print Data Stick, Bind Pages into Book",
                "Ore vein prospecting area = "
                        + radius*2
                        + "x"
                        + radius*2,
                "Oil prospecting area 3x3 oilfields, each is 8x8 chunks"};
    }

    protected VPAdvancedSeismicProspector(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures,
                                          String aGUIName, String aNEIName, int aRadius, int aStep) {
        super(aName, aTier, 1, aDescription, aTextures, 1, 1, aGUIName, aNEIName);
        radius = aRadius;
        step = aStep;
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new VPAdvancedSeismicProspector(mName, mTier, mDescriptionArray, mTextures,
                mGUIName, mNEIName, radius, step);
    }

    @Override
    public boolean onRightclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
        if (aBaseMetaTileEntity.isServerSide()) {
            ItemStack aStack = aPlayer.getCurrentEquippedItem();

            if (!ready && (GT_Utility.consumeItems(aPlayer, aStack, Item.getItemFromBlock(Blocks.tnt), 16)
                    || GT_Utility.consumeItems(aPlayer, aStack, Ic2Items.industrialTnt.getItem(), 8)
                    || GT_Utility.consumeItems(aPlayer, aStack, Materials.Glyceryl, 4)
                    || GT_Utility.consumeItems(aPlayer, aStack, ItemList.Block_Powderbarrel.getItem(), 2) )) {

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
                compound.setInteger(VPTags.PROSPECTION_RADIUS, radius);
                setNBT(aStack, compound);
            }
        }
        return true;
    }
}
