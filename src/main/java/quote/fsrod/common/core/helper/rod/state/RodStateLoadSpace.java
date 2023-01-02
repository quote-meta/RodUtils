package quote.fsrod.common.core.helper.rod.state;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import quote.fsrod.common.core.utils.ChatUtils;
import quote.fsrod.common.item.utils.IItemHasFileData;
import quote.fsrod.common.item.utils.IItemHasSpaceInfoTag;
import quote.fsrod.common.item.utils.IItemHasStructureData;
import quote.fsrod.common.property.item.StructureDataProperty;
import quote.fsrod.common.structure.BasicStructure;

public class RodStateLoadSpace implements IRodState{

    @Override
    public void onRightClickWithPressShift(ItemStack stack, Player player) {
        IItemHasSpaceInfoTag.sendRemoveNBTTagToServer(stack, IItemHasSpaceInfoTag.TAG_POINT_SCHEDULED);
        IItemHasSpaceInfoTag.sendRemoveNBTTagToServer(stack, IItemHasSpaceInfoTag.TAG_POINT_SCHEDULED_FACING);
    }

    @Override
    public void onRightClickTargetBlock(BlockPos blockPos, ItemStack stack, Player player) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.remove(IItemHasSpaceInfoTag.TAG_POINT_NEAR);
        tag.remove(IItemHasSpaceInfoTag.TAG_POINT_END);
        tag.remove(IItemHasSpaceInfoTag.TAG_POINT_SCHEDULED);
        tag.remove(IItemHasSpaceInfoTag.TAG_POINT_SCHEDULED_FACING);
        IItemHasSpaceInfoTag.sendRemoveNBTTagToServer(stack, IItemHasSpaceInfoTag.TAG_POINT_NEAR);
        IItemHasSpaceInfoTag.sendRemoveNBTTagToServer(stack, IItemHasSpaceInfoTag.TAG_POINT_END);
        IItemHasSpaceInfoTag.sendRemoveNBTTagToServer(stack, IItemHasSpaceInfoTag.TAG_POINT_SCHEDULED);
        IItemHasSpaceInfoTag.sendRemoveNBTTagToServer(stack, IItemHasSpaceInfoTag.TAG_POINT_SCHEDULED_FACING);

        handleLoading(player, stack);
    }


    private void handleLoading(Player player, ItemStack stack){
        CompoundTag tag = IItemHasFileData.loadTag(stack);
        if(tag.isEmpty()){
            String path = stack.getItem().getRegistryName().getPath();
            ChatUtils.sendTranslatedChat(player, ChatFormatting.RED, "message.fsrod." + path + ".use.load.failed");
            return;
        }
        
        IItemHasStructureData.sendToServer(stack, tag);

        BasicStructure structureData = new BasicStructure(tag);
        StructureDataProperty.of(stack).ifPresent(p -> p.bindStuctureData(structureData));
    }
}
