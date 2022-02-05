package quote.fsrod.common.property;

import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import quote.fsrod.common.property.player.IPlayerProperty;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModCapabilities {
    private ModCapabilities(){}

    @SubscribeEvent
    public static void registerItems(RegisterCapabilitiesEvent event){
        event.register(IPlayerProperty.class);
    }
}
