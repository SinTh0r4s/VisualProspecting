package com.sinthoras.visualprospecting.network;

import com.sinthoras.visualprospecting.VP;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class VPWorldId implements IMessage {

    private String worldId;

    public VPWorldId() {

    }

    public VPWorldId(String worldId) {
        this.worldId = worldId;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        worldId = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, worldId);
    }

    public static class Handler implements IMessageHandler<VPWorldId, IMessage> {

        @Override
        public IMessage onMessage(VPWorldId message, MessageContext ctx) {
            VP.clientVeinCache.loadVeinCache(message.worldId);
            return null;
        }
    }
}
