package quote.fsRod.common.core.utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class ChatUtils{

    public static void sendTranslatedChat(EntityPlayer player, ITextComponent textComponent){
        player.sendMessage(textComponent);
    }

    public static void sendTranslatedChat(EntityPlayer player, String str, Object ... params){
        sendTranslatedChat(player, new TextComponentTranslation(str, params));
    }

    public static void sendTranslatedChat(EntityPlayer player, TextFormatting color, String str, Object ... params){
        sendTranslatedChat(player, new TextComponentTranslation(str, params).setStyle(new Style().setColor(color)));
    }

    public static void sendTranslatedChatToAllPlayer(TextFormatting color, String str, Object ... params){
        MinecraftServer mcServer = FMLCommonHandler.instance().getMinecraftServerInstance();
        if(mcServer == null) return;
        for(EntityPlayerMP player : mcServer.getPlayerList().getPlayers()){
            sendTranslatedChat(player, color, str, params);
        }
    }

    public static void sendTranslatedEnemyQuote(EntityPlayer player, TextFormatting color, Entity entityHost, String str, Object ... params){
        TextComponentTranslation chatComponentTranslation = getUnlocalizedTextComponent(entityHost, str, params);
        chatComponentTranslation.setStyle(new Style().setColor(color));
        sendTranslatedChat(player, chatComponentTranslation);
    }

    public static void sendEnemyQuote(EntityPlayer player, Entity entityHost, String serial){
        TextComponentTranslation chatComponentTranslation = getUnlocalizedTextComponentBySerial(entityHost, serial);
        sendTranslatedChat(player, chatComponentTranslation);
    }

    public static void sendEnemyQuote(EntityPlayer player, TextFormatting color, Entity entityHost, String serial){
        TextComponentTranslation chatComponentTranslation = getUnlocalizedTextComponentBySerial(entityHost, serial);
        chatComponentTranslation.setStyle(new Style().setColor(color));
        sendTranslatedChat(player, chatComponentTranslation);
    }

    public static void sendTranslatedEnemyQuoteToAllPlayer(TextFormatting color, Entity entityHost, String str, Object ... params){
        if(entityHost.world.isRemote) return;
        MinecraftServer mcServer = FMLCommonHandler.instance().getMinecraftServerInstance();
        if(mcServer == null) return;

        for(EntityPlayerMP player : mcServer.getPlayerList().getPlayers()){
            sendTranslatedEnemyQuote(player, color, entityHost, str, params);
        }
    }

    public static void sendEnemyQuoteToAllPlayer(Entity entityHost, String serial){
        if(entityHost.world.isRemote) return;
        MinecraftServer mcServer = FMLCommonHandler.instance().getMinecraftServerInstance();
        if(mcServer == null) return;

        for(EntityPlayerMP player : mcServer.getPlayerList().getPlayers()){
            sendEnemyQuote(player, entityHost, serial);
        }
    }

    public static void sendEnemyQuoteToAllPlayer(TextFormatting color, Entity entityHost, String serial){
        if(entityHost.world.isRemote) return;
        MinecraftServer mcServer = FMLCommonHandler.instance().getMinecraftServerInstance();
        if(mcServer == null) return;

        for(EntityPlayerMP player : mcServer.getPlayerList().getPlayers()){
            sendEnemyQuote(player, color, entityHost, serial);
        }
    }

    private static TextComponentTranslation getUnlocalizedTextComponentBySerial(Entity entityHost, String serial){
        ITextComponent entityName = entityHost.getDisplayName();
        String unlocalizedText = "entity." + EntityList.getEntityString(entityHost) + ".quote." + serial;
        ITextComponent text = new TextComponentTranslation(unlocalizedText);
        return new TextComponentTranslation("chat.type.text", entityName, text);
    }

    private static TextComponentTranslation getUnlocalizedTextComponent(Entity entityHost, String unlocalized, Object ... params){
        ITextComponent entityName = entityHost.getDisplayName();
        ITextComponent text = new TextComponentTranslation(unlocalized, params);
        return new TextComponentTranslation("chat.type.text", entityName, text);
    }
}