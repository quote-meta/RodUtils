package quote.fsrod.common.core.helper.rod.state;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IRodState {
    public void onRightClickWithPressShift(ItemStack stack, Player player);
    public void onRightClickTargetBlock(BlockPos blockPos, ItemStack stack, Player player);
}
