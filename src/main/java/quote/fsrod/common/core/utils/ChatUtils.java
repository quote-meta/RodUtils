package quote.fsrod.common.core.utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

public class ChatUtils{

    private ChatUtils(){}

    public static void sendTranslatedChat(PlayerEntity player, ITextComponent textComponent){
        player.sendMessage(textComponent);
    }

    public static void sendTranslatedChat(PlayerEntity player, String str, Object ... params){
        sendTranslatedChat(player, new TranslationTextComponent(str, params));
    }

    public static void sendTranslatedChat(PlayerEntity player, TextFormatting color, String str, Object ... params){
        sendTranslatedChat(player, new TranslationTextComponent(str, params).setStyle(new Style().setColor(color)));
    }

    public static void sendTranslatedChatToAllPlayer(TextFormatting color, String str, Object ... params){
        MinecraftServer mcServer = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
        if(mcServer == null) return;
        for(ServerPlayerEntity player : mcServer.getPlayerList().getPlayers()){
            sendTranslatedChat(player, color, str, params);
        }
    }

    public static void sendTranslatedEnemyQuote(PlayerEntity player, TextFormatting color, Entity entityHost, String str, Object ... params){
        TranslationTextComponent chatComponentTranslation = getUnlocalizedTextComponent(entityHost, str, params);
        chatComponentTranslation.setStyle(new Style().setColor(color));
        sendTranslatedChat(player, chatComponentTranslation);
    }

    public static void sendEnemyQuote(PlayerEntity player, Entity entityHost, String serial){
        TranslationTextComponent chatComponentTranslation = getUnlocalizedTextComponentBySerial(entityHost, serial);
        sendTranslatedChat(player, chatComponentTranslation);
    }

    public static void sendEnemyQuote(PlayerEntity player, TextFormatting color, Entity entityHost, String serial){
        TranslationTextComponent chatComponentTranslation = getUnlocalizedTextComponentBySerial(entityHost, serial);
        chatComponentTranslation.setStyle(new Style().setColor(color));
        sendTranslatedChat(player, chatComponentTranslation);
    }

    public static void sendTranslatedEnemyQuoteToAllPlayer(TextFormatting color, Entity entityHost, String str, Object ... params){
        if(entityHost.world.isRemote) return;
        MinecraftServer mcServer = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
        if(mcServer == null) return;
        for(PlayerEntity player : mcServer.getPlayerList().getPlayers()){
            sendTranslatedEnemyQuote(player, color, entityHost, str, params);
        }
    }

    public static void sendEnemyQuoteToAllPlayer(Entity entityHost, String serial){
        if(entityHost.world.isRemote) return;
        MinecraftServer mcServer = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
        if(mcServer == null) return;
        for(PlayerEntity player : mcServer.getPlayerList().getPlayers()){
            sendEnemyQuote(player, entityHost, serial);
        }
    }

    public static void sendEnemyQuoteToAllPlayer(TextFormatting color, Entity entityHost, String serial){
        if(entityHost.world.isRemote) return;
        MinecraftServer mcServer = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
        if(mcServer == null) return;
        for(PlayerEntity player : mcServer.getPlayerList().getPlayers()){
            sendEnemyQuote(player, color, entityHost, serial);
        }
    }

    private static TranslationTextComponent getUnlocalizedTextComponentBySerial(Entity entityHost, String serial){
        ITextComponent entityName = entityHost.getDisplayName();
        String unlocalizedText = "entity." + entityHost.getEntityString() + ".quote." + serial;
        ITextComponent text = new TranslationTextComponent(unlocalizedText);
        return new TranslationTextComponent("chat.type.text", entityName, text);
    }

    private static TranslationTextComponent getUnlocalizedTextComponent(Entity entityHost, String unlocalized, Object ... params){
        ITextComponent entityName = entityHost.getDisplayName();
        ITextComponent text = new TranslationTextComponent(unlocalized, params);
        return new TranslationTextComponent("chat.type.text", entityName, text);
    }
}