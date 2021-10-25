package com.sinthoras.visualprospecting.database;

import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

public class ResetClientCacheCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "visualprospectingresetprogress";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return I18n.format("visualprospecting.resetprogress.command");
    }

    @Override
    public void processCommand(ICommandSender sender, String[] parameters) {
        ClientCache.instance.resetPlayerProgression();
        final IChatComponent confirmation = new ChatComponentTranslation("visualprospecting.resetprogress.confirmation");
        confirmation.getChatStyle().setItalic(true);
        sender.addChatMessage(confirmation);
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
