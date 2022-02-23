package quote.fsrod.common.core.helper.rod.state;

import java.util.Optional;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import quote.fsrod.common.core.handler.ConfigHandler;
import quote.fsrod.common.core.utils.ChatUtils;
import quote.fsrod.common.item.utils.IItemHasSpaceInfoTag;

public class RodStateSettingEndPos extends RodState{

    @Override
    public void onRightClickWithPressShift(ItemStack stack, Player player) {
        IItemHasSpaceInfoTag.sendRemoveNBTTagToServer(stack, IItemHasSpaceInfoTag.TAG_POINT_NEAR);
        IItemHasSpaceInfoTag.sendRemoveNBTTagToServer(stack, IItemHasSpaceInfoTag.TAG_POINT_END);

        String path = stack.getItem().getRegistryName().getPath();
        ChatUtils.sendTranslatedChat(player, ChatFormatting.WHITE, "message.fsrod." + path + ".use.reset");
    }

    @Override
    public void onRightClickTargetBlock(BlockPos blockPos, ItemStack stack, Player player) {
        Optional<BlockPos> possibleBlockPosNear = IItemHasSpaceInfoTag.getBlockPosNear(stack);
        possibleBlockPosNear.ifPresent(blockPosNear -> {
            int sizeX = Math.abs((blockPos.getX() - blockPosNear.getX()));
            int sizeY = Math.abs((blockPos.getY() - blockPosNear.getY()));
            int sizeZ = Math.abs((blockPos.getZ() - blockPosNear.getZ()));
            if(
                sizeX > ConfigHandler.COMMON.rodCloneMaxLength.get() ||
                sizeY > ConfigHandler.COMMON.rodCloneMaxLength.get() ||
                sizeZ > ConfigHandler.COMMON.rodCloneMaxLength.get()
            ){
                String path = stack.getItem().getRegistryName().getPath();
                ChatUtils.sendTranslatedChat(player, ChatFormatting.RED, "message.fsrod." + path + ".warning.range_too_large");
                return;
            }
    
            CompoundTag tag = stack.getOrCreateTag();
            tag.putString(IItemHasSpaceInfoTag.TAG_DIMENSION, player.level.dimension().location().toString());
            tag.put(IItemHasSpaceInfoTag.TAG_POINT_END, NbtUtils.writeBlockPos(blockPos));
            IItemHasSpaceInfoTag.sendNBTTagToServer(stack);
        });
    }
    
}
