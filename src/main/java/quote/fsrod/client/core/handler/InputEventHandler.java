package quote.fsrod.client.core.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.InputEvent.MouseScrollEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import quote.fsrod.common.core.helper.rod.RodCloneHelper;
import quote.fsrod.common.core.helper.rod.RodRecollectionHelper;
import quote.fsrod.common.item.CharmUranusItem;
import quote.fsrod.common.item.rod.RodCloneItem;
import quote.fsrod.common.item.rod.RodRecollectionItem;
import quote.fsrod.common.item.rod.RodTeleportationItem;
import quote.fsrod.common.item.rod.RodTransferItem;

public class InputEventHandler {

    @SubscribeEvent
    @SuppressWarnings("resource")
    public void onMouseScroll(MouseScrollEvent event){
        Player player = Minecraft.getInstance().player;
        ItemStack stackMainHand = player.getItemInHand(InteractionHand.MAIN_HAND);

        if(player.level.isClientSide && (RodCloneItem.isItemOf(stackMainHand))){
            RodCloneHelper.onMouseScrollEvent(event, stackMainHand, player);
        }
        if(player.level.isClientSide && (RodTransferItem.isItemOf(stackMainHand))){
            RodCloneHelper.onMouseScrollEvent(event, stackMainHand, player);
        }
        if(player.level.isClientSide && (RodRecollectionItem.isItemOf(stackMainHand))){
            RodRecollectionHelper.onMouseScrollEvent(event, stackMainHand, player);
        }
        if(player.level.isClientSide && (RodTeleportationItem.isItemOf(stackMainHand))){
            RodTeleportationItem.onMouseScrollEvent(event, stackMainHand, player);
        }
        if(player.level.isClientSide && (CharmUranusItem.isItemOf(stackMainHand))){
            CharmUranusItem.onMouseScrollEvent(event, stackMainHand, player);
        }
    }
}
