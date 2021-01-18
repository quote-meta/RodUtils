package quote.fsrod.common.core.handler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import quote.fsrod.client.core.helper.RodCloneHelper;
import quote.fsrod.client.core.helper.RodReincarnationHelper;
import quote.fsrod.common.item.ModItems;
import quote.fsrod.common.item.rod.ItemRodClone;
import quote.fsrod.common.item.rod.ItemRodReincarnation;
import quote.fsrod.common.item.rod.ItemRodTransfer;

public class PlayerTracker {
    public static final PlayerTracker INSTANCE = new PlayerTracker();

    private PlayerTracker(){}

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChatMessage(ServerChatEvent event){
        PlayerEntity player = event.getPlayer();
        if(!player.world.isRemote){
            if(player.getHeldItemMainhand().getItem() == ModItems.rodReincarnation){
                RodReincarnationHelper.setFileName(player.getHeldItemMainhand(), event.getMessage(), player);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onRightClickItem(RightClickItem event){
        PlayerEntity player = event.getPlayer();
        ItemStack stackMainHand = player.getHeldItemMainhand();

        if(player.world.isRemote && (ItemRodClone.isRodClone(stackMainHand) || ItemRodTransfer.isRodTransfer(stackMainHand))) {
            RodCloneHelper.onRightClickItem(stackMainHand, player);
        }
        if(player.world.isRemote && ItemRodReincarnation.isRodReincarnation(stackMainHand)) {
            RodReincarnationHelper.onRightClickItem(stackMainHand, player);
        }
    }
}