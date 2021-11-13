package com.sinthoras.visualprospecting.mixins.ifu;

import com.encraft.dz.items.*;
import com.sinthoras.visualprospecting.*;
import com.sinthoras.visualprospecting.database.*;
import gregtech.api.enums.*;
import gregtech.api.objects.*;
import java.util.*;
import java.util.stream.*;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.world.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.At.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(value = ItemOreFinderTool.class, remap = false)
public class ItemOreFinderToolMixin extends Item {

  @Shadow
  private static int found;
  
  @Shadow
  private static int MAX_DAMAGE;

  @Inject(
      method = "onUpdate",
      at = @At(value = "INVOKE", target = "Lcom/encraft/dz/items/ItemOreFinderTool;shouldKeepLooking()Z", shift = Shift.AFTER, remap = false),
      locals = LocalCapture.CAPTURE_FAILSOFT,
      remap = true
  )
  public void onGtOreFound(
      ItemStack itemstack, 
      World world, Entity entity, 
      int par4, boolean par5,
      CallbackInfo ci, 
      ItemStack searchItem, ItemData data, boolean vanilla, int id, 
      double cur_x, double cur_y, double cur_z, 
      int min_x, int min_y, int min_z, 
      int max_x, int max_y, int max_z, 
      boolean keepLooking, 
      int z1, int x1, int y1, 
      Block tBlock, int meta, ItemStack inWorld, ItemData dataInWorld, List<OrePrefixes> oreTypes
  ) {
    if (vanilla || world.isRemote || found < MAX_DAMAGE || !(entity instanceof EntityPlayer)) return;
    
    final short foundMaterialMetaItemSubId = (short) dataInWorld.mMaterial.mMaterial.mMetaItemSubID;
    final List<OreVeinPosition> discoveredOreVeins = listAssociateVeins( foundMaterialMetaItemSubId, x1, z1, world);
    VisualProspecting_API.LogicalServer.sendProspectionResultsToClient((EntityPlayerMP) entity, discoveredOreVeins, Collections.emptyList());
  }

  private List<OreVeinPosition> listAssociateVeins(short foundMaterialMetaItemSubId, int x, int z, World world) {
    return VisualProspecting_API
        .LogicalServer
        .prospectOreVeinsWithinRadius(world.provider.dimensionId, x, z, 24)
        .stream()
        .filter(it -> it.veinType.containsOre(foundMaterialMetaItemSubId))
        .collect(Collectors.toList());
  }

}
