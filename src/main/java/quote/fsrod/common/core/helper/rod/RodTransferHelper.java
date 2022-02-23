package quote.fsrod.common.core.helper.rod;

import java.util.Optional;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.InputEvent.MouseScrollEvent;
import quote.fsrod.common.core.helper.rod.state.IRodState;
import quote.fsrod.common.core.helper.rod.state.RodStateBuilding;
import quote.fsrod.common.core.helper.rod.state.RodStateSettingEndPos;
import quote.fsrod.common.core.helper.rod.state.RodStateSettingScheduledPos;
import quote.fsrod.common.core.helper.rod.state.RodStateSettingStartPos;
import quote.fsrod.common.core.network.ModPacketHandler;
import quote.fsrod.common.core.network.item.CPacketItemUpdateTag;
import quote.fsrod.common.item.utils.IItemHasSpaceInfoTag;
import quote.fsrod.common.item.utils.IItemHasUUID;
public class RodTransferHelper {
    
    public static void onMouseScrollEvent(MouseScrollEvent event, ItemStack stack, Player player){
        if (event.getScrollDelta() != 0 && player.isShiftKeyDown()){
            if(!IItemHasUUID.hasUUID(stack)) return;
            CompoundTag tag = new CompoundTag();
            CompoundTag tagStack = stack.getOrCreateTag();
            UUID uuid = IItemHasUUID.getOrCreateUUID(stack);

            int oldReach = tagStack.getInt(IItemHasSpaceInfoTag.TAG_REACH_DISTANCE);
            int newReach = (int)Mth.clamp(oldReach + event.getScrollDelta(), 2, 10);
            tag.putInt(IItemHasSpaceInfoTag.TAG_REACH_DISTANCE, newReach);

            ModPacketHandler.CHANNEL.sendToServer(new CPacketItemUpdateTag(tag, uuid, CPacketItemUpdateTag.Operation.ADD));
            event.setCanceled(true);
        }
    }

    public static void onRightClickItem(ItemStack stack, Player player){
        Optional<String> possibleDimension = IItemHasSpaceInfoTag.getDimension(stack);
        if(possibleDimension.isPresent() && player.level.dimension().location().getPath().equals(IItemHasSpaceInfoTag.getDimension(stack).get())){
            IItemHasSpaceInfoTag.sendRemoveNBTTagToServer(stack, IItemHasSpaceInfoTag.TAG_DIMENSION);
            IItemHasSpaceInfoTag.sendRemoveNBTTagToServer(stack, IItemHasSpaceInfoTag.TAG_POINT_NEAR);
            IItemHasSpaceInfoTag.sendRemoveNBTTagToServer(stack, IItemHasSpaceInfoTag.TAG_POINT_END);
            IItemHasSpaceInfoTag.sendRemoveNBTTagToServer(stack, IItemHasSpaceInfoTag.TAG_POINT_SCHEDULED);
            IItemHasSpaceInfoTag.sendRemoveNBTTagToServer(stack, IItemHasSpaceInfoTag.TAG_POINT_SCHEDULED_FACING);
            stack.setTag(new CompoundTag());
        }

        IRodState state = getCurrendRodState(stack);
        if(player.isShiftKeyDown()){
            state.onRightClickWithPressShift(stack, player);
        }
        else{
            state.onRightClickTargetBlock(SpaceReader.getBlockPosSeeing(stack, player, Minecraft.getInstance().getDeltaFrameTime()), stack, player);
        }
    }

    public static IRodState getCurrendRodState(ItemStack stack){
        CompoundTag tag = stack.getOrCreateTag();
        
        if(tag.getCompound(IItemHasSpaceInfoTag.TAG_POINT_NEAR).isEmpty()) return new RodStateSettingStartPos();
        if(tag.getCompound(IItemHasSpaceInfoTag.TAG_POINT_END).isEmpty()) return new RodStateSettingEndPos();
        if(tag.getCompound(IItemHasSpaceInfoTag.TAG_POINT_SCHEDULED).isEmpty()) return new RodStateSettingScheduledPos();
        return new RodStateBuilding();
    }
}
