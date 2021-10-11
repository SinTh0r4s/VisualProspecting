package com.sinthoras.visualprospecting.hooks;

import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.VPUtils;
import com.sinthoras.visualprospecting.database.VPWorldIdHandler;
import com.sinthoras.visualprospecting.network.VPWorldIdNotification;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;

public class VPHooksEventBus {

    @SubscribeEvent
    public void onEvent(WorldEvent.Unload event) {
        if(VPUtils.isLogicalClient()) {
            VP.clientCache.saveVeinCache();
        }
    }

    @SubscribeEvent
    public void onEvent(WorldEvent.Save event) {
        VP.serverCache.saveVeinCache();
    }

    @SubscribeEvent
    public void onEvent(EntityJoinWorldEvent event) {
        if(event.world.isRemote == false) {
            if (event.entity instanceof EntityPlayerMP) {
                VP.network.sendTo(new VPWorldIdNotification(VPWorldIdHandler.getWorldId()), (EntityPlayerMP) event.entity);
            } else if (event.entity instanceof EntityPlayer) {
                VP.clientCache.loadVeinCache(VPWorldIdHandler.getWorldId());
            }
        }
    }
}
