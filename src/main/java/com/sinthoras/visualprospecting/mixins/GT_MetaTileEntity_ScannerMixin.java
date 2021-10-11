package com.sinthoras.visualprospecting.mixins;

import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.VPTags;
import com.sinthoras.visualprospecting.database.VPServerOreCache;
import gregtech.api.interfaces.ITexture;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_BasicMachine;
import gregtech.api.util.GT_Utility;
import gregtech.common.tileentities.machines.basic.GT_MetaTileEntity_Scanner;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static gregtech.api.util.GT_Utility.ItemNBT.setNBT;

@Mixin(GT_MetaTileEntity_Scanner.class)
public abstract class GT_MetaTileEntity_ScannerMixin extends GT_MetaTileEntity_BasicMachine {

    public GT_MetaTileEntity_ScannerMixin() {
        super(0, "", "", 0, 0, "", 0, 0, "", "", (ITexture[]) null);
    }

    @Inject(method = "checkRecipe",
            at = @At(value = "INVOKE", target = "Lgregtech/api/util/GT_Utility$ItemNBT;convertProspectionData(Lnet/minecraft/item/ItemStack;)V"),
            remap = false,
            require = 1,
            cancellable = true)
    private void onAnalyzeProspectionData(CallbackInfoReturnable<Integer> callbackInfoReturnable) {
        final ItemStack itemStack = getInputAt(0);
        final NBTTagCompound compound = itemStack.getTagCompound();
        if(compound.hasKey(VPTags.VISUALPROSPECTING_FLAG)) {
            final int dimensionId = compound.getInteger(VPTags.PROSPECTION_DIMENSION_ID);
            final int blockX = compound.getInteger(VPTags.PROSPECTION_BLOCK_X);
            final int blockY = compound.getInteger(VPTags.PROSPECTION_BLOCK_Y);
            final int blockZ = compound.getInteger(VPTags.PROSPECTION_BLOCK_Z);
            final int blockRadius = compound.getInteger(VPTags.PROSPECTION_RADIUS);
            final String position = "X: " + blockX + " Y: " + blockY + " Z: " + blockZ;

            final NBTTagList bookPages = new NBTTagList();

            final String frontPage = "Prospector report\n"
                    + position + "\n\n"
                    + "Oils: " + 0 + "\n\n"
                    + "Ores within " + blockRadius + " blocks\n\n"
                    + "Location is center of orevein\n\n"
                    + "Results are synchronized to your map";
            bookPages.appendTag(new NBTTagString(frontPage));

            final List<VPServerOreCache.VPProspectionResult> foundOreVeins = VP.serverVeinCache.prospectBlockRadius(dimensionId, blockX, blockZ, blockRadius);
            if(foundOreVeins.isEmpty() == false) {
                final int pageSize = 7;
                final int numberOfPages = (foundOreVeins.size() + pageSize) / pageSize;  // Equals to ceil((foundOreVeins.size())

                for(int pageNumber=0;pageNumber < numberOfPages;pageNumber++) {
                    final StringBuilder pageString = new StringBuilder();
                    for (int i = 0; i < pageSize; i++) {
                        final int veinId = pageNumber * pageSize + i;
                        if(veinId < foundOreVeins.size()) {
                            final VPServerOreCache.VPProspectionResult oreVein = foundOreVeins.get(veinId);
                            pageString.append(oreVein.getBlockX()).append(",").append(oreVein.getBlockZ()).append(" - ").append(oreVein.veinType.getNameReadable() + " Vein").append("\n");
                        }
                    }
                    String pageCounter = numberOfPages > 1 ? String.format(" %d/%d", pageNumber + 1, numberOfPages) : "";
                    NBTTagString pageTag = new NBTTagString(String.format("Ore Veins %s\n\n", pageCounter) + pageString);
                    bookPages.appendTag(pageTag);
                }
            }

            final String oilCoverPage = "Oil notes\n\n"
                    + "Prospects from NW to SE 576 chunks"
                    + "(9 8x8 oilfields)\n around and gives min-max amount" + "\n\n"
                    + "[1][2][3]" + "\n"
                    + "[4][5][6]" + "\n"
                    + "[7][8][9]" + "\n"
                    + "\n"
                    + "[5] - Prospector in this 8x8 area";
            bookPages.appendTag(new NBTTagString(oilCoverPage));

            final String oilsPage = "Corners of [5] are \n" +
                    "" + "\n" +
                    "P - Prospector in 8x8 field";
            bookPages.appendTag(new NBTTagString(oilsPage));

            compound.setString("author", position);
            compound.setTag("pages", bookPages);
            setNBT(itemStack, compound);


            // Mimic original behaviour
            itemStack.stackSize -= 1;
            mOutputItems[0] = GT_Utility.copyAmount(1L, itemStack);
            calculateOverclockedNess(30, 1000);
            //In case recipe is too OP for that machine
            if (mMaxProgresstime == Integer.MAX_VALUE - 1 && mEUt == Integer.MAX_VALUE - 1) {
                callbackInfoReturnable.setReturnValue(FOUND_RECIPE_BUT_DID_NOT_MEET_REQUIREMENTS);
            }
            else {
                callbackInfoReturnable.setReturnValue(2);
            }
        }
    }
}
