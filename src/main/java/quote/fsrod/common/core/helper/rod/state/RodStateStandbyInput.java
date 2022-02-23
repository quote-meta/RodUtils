package quote.fsrod.common.core.helper.rod.state;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import quote.fsrod.common.core.utils.ChatUtils;
import quote.fsrod.common.item.utils.IItemHasSpaceInfoTag;

public class RodStateStandbyInput extends RodState{

    @Override
    public void onRightClickTargetBlock(BlockPos blockPos, ItemStack stack, Player player) {
        String path = stack.getItem().getRegistryName().getPath();
        ChatUtils.sendTranslatedChat(player, ChatFormatting.WHITE, "message.fsrod." + path + ".warning.standby_input");

    }

    @Override
    public void onRightClickWithPressShift(ItemStack stack, Player player) {
        IItemHasSpaceInfoTag.sendRemoveNBTTagToServer(stack, IItemHasSpaceInfoTag.TAG_POINT_NEAR);
        IItemHasSpaceInfoTag.sendRemoveNBTTagToServer(stack, IItemHasSpaceInfoTag.TAG_POINT_END);

        String path = stack.getItem().getRegistryName().getPath();
        ChatUtils.sendTranslatedChat(player, ChatFormatting.WHITE, "message.fsrod." + path + ".use.reset");
    }
    
}
