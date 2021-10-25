package com.sinthoras.visualprospecting.hooks;

import com.sinthoras.visualprospecting.database.ClientCache;
import com.sinthoras.visualprospecting.task.TaskManager;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;

public class HooksFML {

    @SubscribeEvent
    public void onEvent(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        ClientCache.instance.reset();
    }

    @SubscribeEvent
    public void onEvent(TickEvent event) {
        TaskManager.instance.onTick();
    }
}
