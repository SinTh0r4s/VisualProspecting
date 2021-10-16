package com.sinthoras.visualprospecting.hooks;

import com.sinthoras.visualprospecting.VP;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;

public class HooksFML {

    @SubscribeEvent
    public void onEvent(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        VP.clientCache.reset();
    }
}
