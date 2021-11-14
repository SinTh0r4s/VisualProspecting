package com.sinthoras.visualprospecting.mixins.ifu;

import com.encraft.dz.items.ItemOreFinderTool;
import com.sinthoras.visualprospecting.VisualProspecting_API;
import com.sinthoras.visualprospecting.VisualProspecting_API.LogicalServer;
import com.sinthoras.visualprospecting.database.OreVeinPosition;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.objects.ItemData;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = ItemOreFinderTool.class, remap = false)
public class ItemOreFinderToolMixin extends Item {

  @Shadow
  private static int found;
  
  @Shadow
  private static int MAX_DAMAGE;

  @Inject(
      method = "onUpdate",
      at = @At(value = "INVOKE", target = "Lcom/encraft/dz/items/ItemOreFinderTool;shouldKeepLooking()Z", shift = Shift.AFTER, remap = false, ordinal = 0),
      locals = LocalCapture.CAPTURE_FAILHARD,
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
    if (vanilla || world.isRemote || didNotDetectSignificantOreCluster() || !(entity instanceof EntityPlayer)) return;

    final short foundMaterialMetaId = (short) dataInWorld.mMaterial.mMaterial.mMetaItemSubID;

    final int blockX = x1;
    final int blockZ = z1;
    final List<OreVeinPosition> discoveredOreVeins = listVeinsInProximityContaining(foundMaterialMetaId, blockX, blockZ, world);

    VisualProspecting_API.LogicalServer.sendProspectionResultsToClient((EntityPlayerMP) entity, discoveredOreVeins, Collections.emptyList());
  }

  private boolean didNotDetectSignificantOreCluster() {
    return found < MAX_DAMAGE;
  }

  private List<OreVeinPosition> listVeinsInProximityContaining(short foundMaterialMetaId, int blocX, int blockZ, World world) {
    return LogicalServer
        .prospectOreVeinsWithinRadius(world.provider.dimensionId, blocX, blockZ, 48)
        .stream()
        .filter(it -> it.veinType.containsOre(foundMaterialMetaId))
        .collect(Collectors.toList());
  }
}
