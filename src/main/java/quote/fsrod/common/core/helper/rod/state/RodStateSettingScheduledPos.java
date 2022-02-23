package quote.fsrod.common.core.helper.rod.state;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import quote.fsrod.common.core.utils.ChatUtils;
import quote.fsrod.common.item.utils.IItemHasSpaceInfoTag;

public class RodStateSettingScheduledPos implements IRodState{

    @Override
    public void onRightClickWithPressShift(ItemStack stack, Player player) {
        IItemHasSpaceInfoTag.sendRemoveNBTTagToServer(stack, IItemHasSpaceInfoTag.TAG_POINT_NEAR);
        IItemHasSpaceInfoTag.sendRemoveNBTTagToServer(stack, IItemHasSpaceInfoTag.TAG_POINT_END);

        String path = stack.getItem().getRegistryName().getPath();
        ChatUtils.sendTranslatedChat(player, ChatFormatting.WHITE, "message.fsrod." + path + ".use.reset");
    }

    @Override
    public void onRightClickTargetBlock(BlockPos blockPos, ItemStack stack, Player player) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString(IItemHasSpaceInfoTag.TAG_DIMENSION, player.level.dimension().location().toString());
        tag.put(IItemHasSpaceInfoTag.TAG_POINT_SCHEDULED, NbtUtils.writeBlockPos(blockPos));
        tag.putInt(IItemHasSpaceInfoTag.TAG_POINT_SCHEDULED_FACING, player.getDirection().ordinal());
        IItemHasSpaceInfoTag.sendNBTTagToServer(stack);
    }
    
}
