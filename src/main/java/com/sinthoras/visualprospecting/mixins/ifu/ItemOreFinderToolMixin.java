package com.sinthoras.visualprospecting.mixins.ifu;

import com.encraft.dz.items.*;
import com.sinthoras.visualprospecting.database.*;
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
      locals = LocalCapture.PRINT //LocalCapture.CAPTURE_FAILSOFT
  )
  public void onOreFoundCallHook(ItemStack dataInWorld,
      World world,
      Entity entity,
      int unused1,
      boolean unused2,
      CallbackInfo ci//,
//      int z1,
//      int x1,
//      int y1
  ) {
    if (entity instanceof EntityPlayer && found >= MAX_DAMAGE) {
      int x = 0, y = 0, z = 0; // TODO: capture
      ClientCache.instance.onOreInteracted(world, x, y, z, (EntityPlayer) entity);
    }
  }

}
