package com.sinthoras.visualprospecting.mixins.ifu;

import com.encraft.dz.items.*;
import com.sinthoras.visualprospecting.*;
import com.sinthoras.visualprospecting.database.*;
import gregtech.api.enums.*;
import gregtech.api.objects.*;
import gregtech.common.*;
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
  private static int found = 0;
  
  @Shadow
  private static int MAX_DAMAGE = 10;

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
    if(vanilla) return;
    oreFoundAt(world, entity, z1, x1, dataInWorld);
  }

  private void oreFoundAt(World world, Entity entity, int z1, int x1, ItemData foundItemData) {
    if (!world.isRemote && entity instanceof EntityPlayer && found >= MAX_DAMAGE) {
      final int foundMaterialMetaItemSubId = foundItemData.mMaterial.mMaterial.mMetaItemSubID;
      final List<String> matchingVeinNames = 
          GT_Worldgen_GT_Ore_Layer.sList
              .stream()
              .filter(layer -> 
                  layer.mPrimaryMeta == foundMaterialMetaItemSubId 
                      || layer.mSecondaryMeta == foundMaterialMetaItemSubId 
                      || layer.mBetweenMeta == foundMaterialMetaItemSubId 
                      || layer.mSporadicMeta == foundMaterialMetaItemSubId
              ).map(layer -> layer.mWorldGenName)
              .collect(Collectors.toList()); // TODO: make a cache for all materials [mMetaItemSubID] -> Set<VeinNames>
      
      final List<OreVeinPosition> allOreVeinsInChunk = VisualProspecting_API.LogicalServer.prospectOreVeinsWithinRadius(world.provider.dimensionId, x1, z1, 1); // TODO: increase radius to 16-48?
      final List<OreVeinPosition> discoveredOreVeins = allOreVeinsInChunk.stream().filter(it -> matchingVeinNames.contains(it.veinType.name)).collect(Collectors.toList());
      
      VisualProspecting_API.LogicalServer.sendProspectionResultsToClient((EntityPlayerMP) entity, discoveredOreVeins, Collections.emptyList());
     
      // TODO: remove debug code
      final String allVeinNames = allOreVeinsInChunk.stream().map(it -> it.veinType.name).collect(Collectors.joining(","));
      final String discoveredVeinNames = discoveredOreVeins.stream().map(it -> it.veinType.name).collect(Collectors.joining(","));
      System.out.println(">>>>>> Searching for: " + foundMaterialMetaItemSubId + ", discovered: " + discoveredVeinNames + ", all veins: " + allVeinNames);
    }
  }

}
