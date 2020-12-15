package quote.fsRod.common.core.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import quote.fsRod.common.core.network.item.CPacketItemUpdateNBT;
import quote.fsRod.common.core.network.item.CPacketItemUpdateSplitNBTList;
import quote.fsRod.common.core.network.item.CPacketRodCloneStartBuilding;
import quote.fsRod.common.core.network.item.CPacketRodReincarnationStartBuilding;
import quote.fsRod.common.lib.LibMisc;

public class ModPacketHandler {
    private ModPacketHandler(){}

    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(LibMisc.MOD_ID);

    public static void init() {
        int id = 0;

        INSTANCE.registerMessage(CPacketRodCloneStartBuilding.Handler.class, CPacketRodCloneStartBuilding.class, id++, Side.SERVER);
        INSTANCE.registerMessage(CPacketRodReincarnationStartBuilding.Handler.class, CPacketRodReincarnationStartBuilding.class, id++, Side.SERVER);
        INSTANCE.registerMessage(CPacketItemUpdateNBT.Handler.class, CPacketItemUpdateNBT.class, id++, Side.SERVER);
        INSTANCE.registerMessage(CPacketItemUpdateSplitNBTList.Handler.class, CPacketItemUpdateSplitNBTList.class, id++, Side.SERVER);
    }
}