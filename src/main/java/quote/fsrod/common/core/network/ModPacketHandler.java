package quote.fsrod.common.core.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import quote.fsrod.common.core.network.item.CPacketItemNotify;
import quote.fsrod.common.core.network.item.CPacketItemUpdateTag;
import quote.fsrod.common.lib.LibMisc;

public class ModPacketHandler {
    private ModPacketHandler(){}

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
        new ResourceLocation(LibMisc.MOD_ID, "channel"),
        () -> PROTOCOL_VERSION,
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    );

    public static void init() {
        int id = 0;

        CHANNEL.registerMessage(id++, CPacketItemUpdateTag.class, CPacketItemUpdateTag::encode, CPacketItemUpdateTag::new, CPacketItemUpdateTag.Handler::onMessage);
        CHANNEL.registerMessage(id++, CPacketItemNotify.class, CPacketItemNotify::encode, CPacketItemNotify::new, CPacketItemNotify.Handler::onMessage);
    }
}