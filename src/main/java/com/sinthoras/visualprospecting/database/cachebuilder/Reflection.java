package com.sinthoras.visualprospecting.database.cachebuilder;

import net.minecraft.server.MinecraftServer;

import java.lang.reflect.Field;

public class Reflection {

    private static Field userMessage;

    static {
        try {
            userMessage = MinecraftServer.class.getDeclaredField("userMessage");
            userMessage.setAccessible(true);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void setUserMessage(String message) {
        try {
            userMessage.set(MinecraftServer.getServer(), message);
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
