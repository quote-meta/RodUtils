package quote.fsrod.common.core.handler;

import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import quote.fsrod.common.property.item.StructureDataProperty;

public class TickHandler {
    
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onTickLast(WorldTickEvent event){
        if(event.phase == Phase.END){
            StructureDataProperty.removeUnusedStorage();
        }
    }
}
