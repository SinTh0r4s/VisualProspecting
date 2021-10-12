package com.sinthoras.visualprospecting.hooks;

import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.Utils;
import com.sinthoras.visualprospecting.database.WorldIdHandler;
import com.sinthoras.visualprospecting.network.WorldIdNotification;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;

public class HooksEventBus {

    @SubscribeEvent
    public void onEvent(WorldEvent.Unload event) {
        if(Utils.isLogicalClient()) {
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
                VP.network.sendTo(new WorldIdNotification(WorldIdHandler.getWorldId()), (EntityPlayerMP) event.entity);
            }
            else if (event.entity instanceof EntityPlayer) {
                VP.clientCache.loadVeinCache(WorldIdHandler.getWorldId());
            }
        }
    }
}
