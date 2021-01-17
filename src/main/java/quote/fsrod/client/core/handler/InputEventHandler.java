package quote.fsrod.client.core.handler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.InputEvent.MouseScrollEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import quote.fsrod.client.core.helper.RodCloneHelper;
import quote.fsrod.client.core.helper.RodReincarnationHelper;
import quote.fsrod.common.RodUtils;
import quote.fsrod.common.item.rod.ItemRodClone;
import quote.fsrod.common.item.rod.ItemRodReincarnation;

public class InputEventHandler {
    
    @SubscribeEvent
    public void onMouseScroll(MouseScrollEvent event){
        PlayerEntity player = RodUtils.proxy.getEntityPlayerInstance();
        ItemStack stackMainHand = player.getHeldItemMainhand();
        
        if(ItemRodClone.isRodClone(stackMainHand) || ItemRodClone.isRodTransfer(stackMainHand)) {
            RodCloneHelper.onMouseEvent(event, stackMainHand, player);
        }
        if(ItemRodReincarnation.isRodReincarnation(stackMainHand)){
            RodReincarnationHelper.onMouseEvent(event, stackMainHand, player);
        }
    }
}