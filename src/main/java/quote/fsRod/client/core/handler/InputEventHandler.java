package quote.fsRod.client.core.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import quote.fsRod.client.core.helper.RodCloneHelper;
import quote.fsRod.client.core.helper.RodReincarnationHelper;
import quote.fsRod.common.RodUtils;
import quote.fsRod.common.item.rod.ItemRodClone;
import quote.fsRod.common.item.rod.ItemRodReincarnation;

@SideOnly(Side.CLIENT)
public class InputEventHandler {
    
    @SubscribeEvent
    public void onMouseScroll(MouseEvent event){
        EntityPlayer player = RodUtils.proxy.getEntityPlayerInstance();
        ItemStack stackMainHand = player.getHeldItemMainhand();
        
        if(ItemRodClone.isRodClone(stackMainHand) || ItemRodClone.isRodTransfer(stackMainHand)) {
            RodCloneHelper.onMouseEvent(event, stackMainHand, player);
        }
        if(ItemRodReincarnation.isRodReincarnation(stackMainHand)){
            RodReincarnationHelper.onMouseEvent(event, stackMainHand, player);
        }
    }
}