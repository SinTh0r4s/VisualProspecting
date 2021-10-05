package com.sinthoras.visualprospecting.mixins;

import com.sinthoras.visualprospecting.VP;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import journeymap.client.network.WorldInfoHandler;
import journeymap.common.network.WorldIDPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldInfoHandler.WorldIdListener.class)
public class VPJourneyMapWorldIdMixin {
    @Redirect(
            method = {"onMessage"},
            at = @At(value = "INVOKE", target = "journeymap.client.network.WorldInfoHandler.WorldIdListener.onMessage(Ljourneymap.common.network.WorldIDPacket;Lcpw.mods.fml.common.network.simpleimpl.MessageContext;)Lcpw.mods.fml.common.network.simpleimpl.IMessage;"),
            require = 4
    )
    private void grabJourneyMapWorldId(WorldInfoHandler.WorldIdListener worldIdListener, WorldIDPacket message, MessageContext ctx) {
        VP.info("IMPORTANT!!!");
        VP.info(message.getWorldID());
        VP.info("IMPORTANT!!!");
        worldIdListener.onMessage(message, ctx);
    }

}
