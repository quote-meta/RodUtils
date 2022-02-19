package quote.fsrod.common.core.helper.rod;

import java.util.Optional;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.InputEvent.MouseScrollEvent;
import quote.fsrod.common.core.helper.rod.state.RodState;
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
            CompoundTag tag = stack.getOrCreateTag();
            UUID uuid = IItemHasUUID.getOrCreateUUID(stack);

            int oldReach = tag.getInt(IItemHasSpaceInfoTag.TAG_REACH_DISTANCE);
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

        RodState state = getCurrendRodState(stack);
        if(player.isShiftKeyDown()){
            state.onRightClickWithPressShift(stack, player);
        }
        else{
            state.onRightClickTargetBlock(getBlockPosSeeing(stack, player, Minecraft.getInstance().getDeltaFrameTime()), stack, player);
        }
    }

    public static RodState getCurrendRodState(ItemStack stack){
        CompoundTag tag = stack.getOrCreateTag();
        
        if(tag.getCompound(IItemHasSpaceInfoTag.TAG_POINT_NEAR).isEmpty()) return new RodStateSettingStartPos();
        if(tag.getCompound(IItemHasSpaceInfoTag.TAG_POINT_END).isEmpty()) return new RodStateSettingEndPos();
        if(tag.getCompound(IItemHasSpaceInfoTag.TAG_POINT_SCHEDULED).isEmpty()) return new RodStateSettingScheduledPos();
        return new RodStateBuilding();
    }

    @SuppressWarnings("resource")
    public static BlockPos getBlockPosSeeing(ItemStack stack, Player player, float partialTicks){
        CompoundTag tag = stack.getOrCreateTag();

        int distance = tag.getInt(IItemHasSpaceInfoTag.TAG_REACH_DISTANCE);
        BlockPos blockPos = null;
        HitResult objectMouseOver = Minecraft.getInstance().hitResult;

        boolean isLookingAir = true;
        if (objectMouseOver instanceof BlockHitResult && ((BlockHitResult)objectMouseOver).getBlockPos() != null){
            blockPos = ((BlockHitResult)objectMouseOver).getBlockPos();
            isLookingAir = player.level.getBlockState(blockPos).isAir();
        }
        if (isLookingAir){
            Vec3 eyePos = player.getEyePosition(partialTicks);
            Vec3 viewVec = player.getViewVector(partialTicks).scale(distance);
            Vec3 viewPos = eyePos.add(viewVec);
            objectMouseOver = player.level.clip(new ClipContext(eyePos, viewPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null));
            if(objectMouseOver instanceof BlockHitResult && ((BlockHitResult)objectMouseOver).getBlockPos() != null){
                blockPos = ((BlockHitResult)objectMouseOver).getBlockPos();
            }
            else{
                blockPos = new BlockPos(viewPos);
            }
        }

        return blockPos;
    }
}
