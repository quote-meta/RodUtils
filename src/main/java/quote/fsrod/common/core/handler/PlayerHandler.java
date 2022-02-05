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
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import quote.fsrod.common.item.rod.RodCloneItem;
import quote.fsrod.common.lib.LibMisc;
import quote.fsrod.common.property.player.IPlayerProperty;
import quote.fsrod.common.property.player.PlayerProperty;

@Mod.EventBusSubscriber(modid = LibMisc.MOD_ID, bus = Bus.FORGE)
public class PlayerHandler {
    
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
