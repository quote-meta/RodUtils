package quote.fsrod.common.property;

import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import quote.fsrod.common.property.item.ISplitListDataProperty;
import quote.fsrod.common.property.item.IStructureDataProperty;
import quote.fsrod.common.property.player.IPlayerProperty;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModCapabilities {
    private ModCapabilities(){}

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event){
        event.register(IPlayerProperty.class);
        event.register(IStructureDataProperty.class);
        event.register(ISplitListDataProperty.class);
    }
}
