package com.sinthoras.visualprospecting.mixins.ifu;

import com.encraft.dz.items.*;
import com.sinthoras.visualprospecting.database.*;
import gregtech.api.objects.*;
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
      at = @At(value = "FIELD", target = "Lcom/encraft/dz/items/ItemOreFinderTool;found:I", shift = Shift.AFTER),
      locals = LocalCapture.CAPTURE_FAILSOFT
  )
  public void onOreFoundCallHook(
      ItemStack itemstack,
      World world, Entity entity,
      int par4, boolean par5,
      CallbackInfo ci,
      ItemStack searchItem, ItemData data, boolean vanilla, int id,
      double cur_x, double cur_y, double cur_z, int min_x, int min_y, int min_z, int max_x, int max_y, int max_z,
      boolean keepLooking,
      int z1, int x1, int y1,
      Block tBlock, int meta, ItemStack inWorld
  ) {
    if (!world.isRemote && entity instanceof EntityPlayer && found >= MAX_DAMAGE) {
      ClientCache.instance.onOreInteracted(world, x1, y1, z1, (EntityPlayer) entity);
    }
  }

}
