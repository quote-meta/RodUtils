package quote.fsrod.common.core.helper.rod.state;

import java.util.Optional;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import quote.fsrod.common.core.utils.ChatUtils;
import quote.fsrod.common.item.utils.IItemHasFileData;
import quote.fsrod.common.item.utils.IItemHasSpaceInfoTag;
import quote.fsrod.common.structure.BasicStructure;

public class RodStateSaveSpace extends RodState{

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
        Optional<BlockPos> possibleBlockPosEnd = IItemHasSpaceInfoTag.getBlockPosEnd(stack);

        possibleBlockPosNear.ifPresent(blockPosNear -> {
            possibleBlockPosEnd.ifPresent(blockPosEnd -> {
                if(blockPosNear.equals(blockPos)){
                    handleSaving(player, blockPosNear, blockPosEnd, stack);
                    IItemHasSpaceInfoTag.sendRemoveNBTTagToServer(stack, IItemHasSpaceInfoTag.TAG_POINT_NEAR);
                    IItemHasSpaceInfoTag.sendRemoveNBTTagToServer(stack, IItemHasSpaceInfoTag.TAG_POINT_END);
                }
                else{
                    String path = stack.getItem().getRegistryName().getPath();
                    ChatUtils.sendTranslatedChat(player, ChatFormatting.WHITE, "message.fsrod." + path + ".warning.click_point_near");
                }
            });
        });
    }


    private void handleSaving(Player player, BlockPos posNear, BlockPos posEnd, ItemStack stack){
        BasicStructure structure = new BasicStructure(player.level, posNear, posEnd);

        CompoundTag nbt = structure.serializeNBT();
        
        if(IItemHasFileData.saveTag(nbt, stack)){
            String path = stack.getItem().getRegistryName().getPath();
            ChatUtils.sendTranslatedChat(player, ChatFormatting.GREEN, "message.fsrod." + path + ".use.save.success");
        }
        else{
            String path = stack.getItem().getRegistryName().getPath();
            ChatUtils.sendTranslatedChat(player, ChatFormatting.RED, "message.fsrod." + path + ".use.save.failed");
        }
    }
    
}
