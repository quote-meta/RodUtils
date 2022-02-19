package quote.fsrod.common.core.utils;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.server.ServerLifecycleHooks;

public class ChatUtils{

    public static void sendTranslatedChat(Player player, Component component){
        player.sendMessage(component, Util.NIL_UUID);
    }

    public static void sendTranslatedChat(Player player, String str, Object ... params){
        sendTranslatedChat(player, new TranslatableComponent(str, params));
    }

    public static void sendTranslatedChat(Player player, ChatFormatting color, String str, Object ... params){
        sendTranslatedChat(player, new TranslatableComponent(str, params).withStyle(color));
    }

    public static void sendTranslatedChatToAllPlayer(ChatFormatting color, String str, Object ... params){
        MinecraftServer mcServer = ServerLifecycleHooks.getCurrentServer();
        if(mcServer == null) return;
        for(ServerPlayer player : mcServer.getPlayerList().getPlayers()){
            sendTranslatedChat(player, color, str, params);
        }
    }

    public static void sendTranslatedEnemyQuote(Player player, ChatFormatting color, Entity entityHost, String str, Object ... params){
        TranslatableComponent translatableComponent = getUnlocalizedTextComponent(entityHost, str, params);
        translatableComponent.withStyle(color);
        sendTranslatedChat(player, translatableComponent);
    }

    public static void sendEnemyQuote(Player player, Entity entityHost, String serial){
        TranslatableComponent translatableComponent = getUnlocalizedTextComponentBySerial(entityHost, serial);
        sendTranslatedChat(player, translatableComponent);
    }

    public static void sendEnemyQuote(Player player, ChatFormatting color, Entity entityHost, String serial){
        TranslatableComponent translatableComponent = getUnlocalizedTextComponentBySerial(entityHost, serial);
        translatableComponent.withStyle(color);
        sendTranslatedChat(player, translatableComponent);
    }

    public static void sendTranslatedEnemyQuoteToAllPlayer(ChatFormatting color, Entity entityHost, String str, Object ... params){
        if(entityHost.level.isClientSide) return;
        MinecraftServer mcServer = ServerLifecycleHooks.getCurrentServer();
        if(mcServer == null) return;

        for(ServerPlayer player : mcServer.getPlayerList().getPlayers()){
            sendTranslatedEnemyQuote(player, color, entityHost, str, params);
        }
    }

    public static void sendEnemyQuoteToAllPlayer(Entity entityHost, String serial){
        if(entityHost.level.isClientSide) return;
        MinecraftServer mcServer = ServerLifecycleHooks.getCurrentServer();
        if(mcServer == null) return;

        for(ServerPlayer player : mcServer.getPlayerList().getPlayers()){
            sendEnemyQuote(player, entityHost, serial);
        }
    }

    public static void sendEnemyQuoteToAllPlayer(ChatFormatting color, Entity entityHost, String serial){
        if(entityHost.level.isClientSide) return;
        MinecraftServer mcServer = ServerLifecycleHooks.getCurrentServer();
        if(mcServer == null) return;

        for(ServerPlayer player : mcServer.getPlayerList().getPlayers()){
            sendEnemyQuote(player, color, entityHost, serial);
        }
    }

    private static TranslatableComponent getUnlocalizedTextComponentBySerial(Entity entityHost, String serial){
        Component entityName = entityHost.getDisplayName();
        String unlocalizedText =  entityHost.getType().toString() + ".quote." + serial;
        Component text = new TranslatableComponent(unlocalizedText);
        return new TranslatableComponent("chat.type.text", entityName, text);
    }

    private static TranslatableComponent getUnlocalizedTextComponent(Entity entityHost, String unlocalized, Object ... params){
        Component entityName = entityHost.getDisplayName();
        Component text = new TranslatableComponent(unlocalized, params);
        return new TranslatableComponent("chat.type.text", entityName, text);
    }
}