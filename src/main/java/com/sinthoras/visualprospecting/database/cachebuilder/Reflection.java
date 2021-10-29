package com.sinthoras.visualprospecting.database.cachebuilder;

import com.sinthoras.visualprospecting.Utils;
import net.minecraft.server.MinecraftServer;

import java.lang.reflect.Field;

public class Reflection {

    private static Field userMessage;

    static {
        try {
            userMessage = MinecraftServer.class.getDeclaredField(Utils.isDevelopmentEnvironment() ? "userMessage" : "field_71298_S");
            userMessage.setAccessible(true);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void renderUserMessage(String message) {
        try {
            userMessage.set(MinecraftServer.getServer(), message);
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
