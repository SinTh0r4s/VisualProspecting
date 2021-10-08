package com.sinthoras.visualprospecting.hooks;

import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.VPUtils;
import com.sinthoras.visualprospecting.database.VPWorldIdHandler;
import com.sinthoras.visualprospecting.network.VPWorldId;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.entity.player.EntityPlayerMP;

public class VPHooksFML {

    @SubscribeEvent
    public void onEvent(PlayerEvent.PlayerLoggedInEvent event) {
        if(VPUtils.isLogicalClient()) {
            VP.clientVeinCache.loadVeinCache(VPWorldIdHandler.getWorldId());
        }
        else {
            VP.network.sendTo(new VPWorldId(VPWorldIdHandler.getWorldId()), (EntityPlayerMP) event.player);
        }
    }
}
