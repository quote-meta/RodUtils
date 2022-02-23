package quote.fsrod.common.core.helper.rod.state;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import quote.fsrod.common.core.network.ModPacketHandler;
import quote.fsrod.common.core.network.item.CPacketItemNotify;
import quote.fsrod.common.item.utils.IItemHasSpaceInfoTag;

public class RodStateRecollection implements IRodState{
    @Override
    public void onRightClickWithPressShift(ItemStack stack, Player player) {
        IItemHasSpaceInfoTag.sendRemoveNBTTagToServer(stack, IItemHasSpaceInfoTag.TAG_POINT_SCHEDULED);
        IItemHasSpaceInfoTag.sendRemoveNBTTagToServer(stack, IItemHasSpaceInfoTag.TAG_POINT_SCHEDULED_FACING);
    }

    @Override
    public void onRightClickTargetBlock(BlockPos blockPos, ItemStack stack, Player player) {
        Optional<BlockPos> possibleBlockPosScheduled = IItemHasSpaceInfoTag.getBlockPosScheduled(stack);
        Optional<Direction> possbleDirection = IItemHasSpaceInfoTag.getFacingScheduled(stack);

        possibleBlockPosScheduled.ifPresent(blockPosScheduled -> {
            possbleDirection.ifPresent(direction -> {
                if(!blockPosScheduled.equals(blockPos)){
                    CompoundTag tag = stack.getOrCreateTag();
                    tag.putString(IItemHasSpaceInfoTag.TAG_DIMENSION, player.level.dimension().location().toString());
                    tag.put(IItemHasSpaceInfoTag.TAG_POINT_SCHEDULED, NbtUtils.writeBlockPos(blockPos));
                    tag.putInt(IItemHasSpaceInfoTag.TAG_POINT_SCHEDULED_FACING, player.getDirection().ordinal());
                    IItemHasSpaceInfoTag.sendNBTTagToServer(stack);
                    return;
                }
                CPacketItemNotify.createPacket(player, stack).ifPresent(packet -> ModPacketHandler.CHANNEL.sendToServer(packet));
            });
        });
    }
}
