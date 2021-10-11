package com.sinthoras.visualprospecting.mixins;

import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.VPTags;
import com.sinthoras.visualprospecting.VPUtils;
import com.sinthoras.visualprospecting.database.VPServerOreCache;
import com.sinthoras.visualprospecting.network.VPProspectingNotification;
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

@Mixin(ItemEditableBook.class)
public class ItemEditableBookMixin {

    @Inject(method = "onItemRightClick", at = @At("HEAD"), remap = false, require = 1, locals = LocalCapture.CAPTURE_FAILEXCEPTION, cancellable = false)
    private void onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer, CallbackInfoReturnable<ItemStack> callbackInfoReturnable) {
        if(world.isRemote == false) {
            final NBTTagCompound compound = itemStack.getTagCompound();
            if(compound.hasKey(VPTags.VISUALPROSPECTING_FLAG)) {
                final int dimensionId = compound.getInteger(VPTags.PROSPECTION_DIMENSION_ID);
                final int blockX = compound.getInteger(VPTags.PROSPECTION_BLOCK_X);
                final int blockZ = compound.getInteger(VPTags.PROSPECTION_BLOCK_Z);
                final int blockRadius = compound.getInteger(VPTags.PROSPECTION_RADIUS);
                final List<VPServerOreCache.VPProspectionResult> foundOreVeins = VP.serverVeinCache.prospectBlockRadius(dimensionId, blockX, blockZ, blockRadius);
                if(VPUtils.isLogicalClient()) {
                    VP.clientVeinCache.putVeinTypes(dimensionId, foundOreVeins);
                }
                else {
                    VP.network.sendTo(new VPProspectingNotification(dimensionId, foundOreVeins), (EntityPlayerMP) entityPlayer);
                }
            }
        }
    }
}
