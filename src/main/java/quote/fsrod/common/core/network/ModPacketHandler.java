package quote.fsrod.common.core.network;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import quote.fsrod.common.core.network.item.CPacketItemUpdateNBT;
import quote.fsrod.common.core.network.item.CPacketItemUpdateSplitNBTList;
import quote.fsrod.common.core.network.item.CPacketRodCloneStartBuilding;
import quote.fsrod.common.core.network.item.CPacketRodReincarnationStartBuilding;
import quote.fsrod.common.lib.LibMisc;

public class ModPacketHandler {
    private ModPacketHandler(){}

    private static final String PROTOCOL = "3";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
        new ResourceLocation(LibMisc.MOD_ID, "channel"),
        () -> PROTOCOL,
        PROTOCOL::equals, PROTOCOL::equals
    );

    public static void init() {
        int id = 0;

        INSTANCE.registerMessage(id++, CPacketRodCloneStartBuilding.class, CPacketRodCloneStartBuilding::encode, CPacketRodCloneStartBuilding::decode, CPacketRodCloneStartBuilding::handle);
        INSTANCE.registerMessage(id++, CPacketRodReincarnationStartBuilding.class, CPacketRodReincarnationStartBuilding::encode, CPacketRodReincarnationStartBuilding::decode, CPacketRodReincarnationStartBuilding::handle);
        INSTANCE.registerMessage(id++, CPacketItemUpdateNBT.class, CPacketItemUpdateNBT::encode, CPacketItemUpdateNBT::decode, CPacketItemUpdateNBT::handle);
        INSTANCE.registerMessage(id++, CPacketItemUpdateSplitNBTList.class, CPacketItemUpdateSplitNBTList::encode, CPacketItemUpdateSplitNBTList::decode, CPacketItemUpdateSplitNBTList::handle);
    }
}