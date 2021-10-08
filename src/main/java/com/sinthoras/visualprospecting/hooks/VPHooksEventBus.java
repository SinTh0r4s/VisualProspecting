package com.sinthoras.visualprospecting.hooks;

import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.VPUtils;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.world.WorldEvent;

public class VPHooksEventBus {

    @SubscribeEvent
    public void onEvent(WorldEvent.Unload event) {
        if(VPUtils.isLogicalClient()) {
            VP.clientVeinCache.saveVeinCache();
        }
    }

    @SubscribeEvent
    public void onEvent(WorldEvent.Save event) {
        VP.serverVeinCache.saveVeinCache();
    }
}
