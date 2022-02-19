package quote.fsrod.common.core.handler;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import quote.fsrod.common.core.helper.rod.RodCloneHelper;
import quote.fsrod.common.item.rod.RodCloneItem;
import quote.fsrod.common.item.rod.RodTransferItem;
import quote.fsrod.common.property.player.IPlayerProperty;
import quote.fsrod.common.property.player.PlayerProperty;

public class PlayerHandler {
    

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onChatMessage(ServerChatEvent event){

    }

    @SubscribeEvent
    public void onRightClickItem(RightClickItem event){
        Player player = event.getPlayer();
        ItemStack stackMainHand = event.getItemStack();

        if(player.level.isClientSide && (RodCloneItem.isItemOf(stackMainHand))){
            RodCloneHelper.onRightClickItem(stackMainHand, player);
        }
        if(player.level.isClientSide && (RodTransferItem.isItemOf(stackMainHand))){
            RodCloneHelper.onRightClickItem(stackMainHand, player);
        }
    }

    @SubscribeEvent
    public void onPlayerUpdate(LivingUpdateEvent event){
        if(!(event.getEntity() instanceof Player)) return;
        Player player = ((Player)event.getEntity());

        PlayerProperty.of(player).ifPresent(IPlayerProperty::update);
    }

    @SubscribeEvent
    public void attachPlayerCapability(AttachCapabilitiesEvent<Entity> event){
        Entity entity = event.getObject();
        if(entity instanceof Player){
            PlayerProperty.addCapability((Player)entity, event);
        }
    }
}
