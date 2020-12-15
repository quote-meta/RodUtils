package quote.fsRod.common.core.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import quote.fsRod.client.core.helper.RodCloneHelper;
import quote.fsRod.client.core.helper.RodReincarnationHelper;
import quote.fsRod.common.item.ModItems;
import quote.fsRod.common.item.rod.ItemRodClone;
import quote.fsRod.common.item.rod.ItemRodReincarnation;

public class PlayerTracker {
    public static final PlayerTracker INSTANCE = new PlayerTracker();

    private PlayerTracker(){}

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChatMessage(ServerChatEvent event){
        EntityPlayer player = event.getPlayer();
        if(!player.world.isRemote){
            if(player.getHeldItemMainhand().getItem() == ModItems.rodReincarnation){
                RodReincarnationHelper.setFileName(player.getHeldItemMainhand(), event.getMessage(), player);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onRightClickItem(RightClickItem event){
        EntityPlayer player = event.getEntityPlayer();
        ItemStack stackMainHand = player.getHeldItemMainhand();

        if(player.world.isRemote && (ItemRodClone.isRodClone(stackMainHand) || ItemRodClone.isRodTransfer(stackMainHand))) {
            RodCloneHelper.onRightClickItem(stackMainHand, player);
        }
        if(player.world.isRemote && ItemRodReincarnation.isRodReincarnation(stackMainHand)) {
            RodReincarnationHelper.onRightClickItem(stackMainHand, player);
        }
    }
}