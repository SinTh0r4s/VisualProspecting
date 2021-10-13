package com.sinthoras.visualprospecting.mixins;

import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.Tags;
import com.sinthoras.visualprospecting.Utils;
import com.sinthoras.visualprospecting.database.OilFieldPosition;
import com.sinthoras.visualprospecting.database.OreVeinPosition;
import com.sinthoras.visualprospecting.network.ProspectingNotification;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemEditableBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(value = ItemEditableBook.class, remap = true)
public class ItemEditableBookMixin {

    @Inject(method = "onItemRightClick", at = @At("HEAD"), remap = true, require = 1, locals = LocalCapture.CAPTURE_FAILEXCEPTION, cancellable = false)
    private void onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer, CallbackInfoReturnable<ItemStack> callbackInfoReturnable) {
        if(world.isRemote == false) {
            final NBTTagCompound compound = itemStack.getTagCompound();
            if(compound.hasKey(Tags.VISUALPROSPECTING_FLAG)) {
                final int dimensionId = compound.getInteger(Tags.PROSPECTION_DIMENSION_ID);
                final int blockX = compound.getInteger(Tags.PROSPECTION_BLOCK_X);
                final int blockZ = compound.getInteger(Tags.PROSPECTION_BLOCK_Z);
                final int blockRadius = compound.getInteger(Tags.PROSPECTION_ORE_RADIUS);
                final List<OreVeinPosition> foundOreVeins = VP.serverCache.prospectOreBlockRadius(dimensionId, blockX, blockZ, blockRadius);
                final List<OilFieldPosition> foundOilFields = VP.serverCache.prospectOilBlockRadius(world, blockX, blockZ, VP.oilChunkProspectingBlockRadius);
                if(Utils.isLogicalClient()) {
                    VP.clientCache.putOreVeins(dimensionId, foundOreVeins);
                    VP.clientCache.putOilFields(dimensionId, foundOilFields);
                }
                else {
                    VP.network.sendTo(new ProspectingNotification(dimensionId, foundOreVeins, foundOilFields), (EntityPlayerMP) entityPlayer);
                }
            }
        }
    }
}
