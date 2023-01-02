package quote.fsrod.common.core.handler;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import quote.fsrod.common.item.utils.IItemHasStructureData;
import quote.fsrod.common.property.item.StructureDataProperty;

public class ItemStackHandler {
    

    @SubscribeEvent
    public void attachItemCapability(AttachCapabilitiesEvent<ItemStack> event){
        ItemStack itemStack = event.getObject();
        if(itemStack.getItem() instanceof IItemHasStructureData){
            StructureDataProperty.addCapability(itemStack, event);
        }
    }
}
