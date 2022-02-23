package quote.fsrod.common.item.utils;

import java.util.UUID;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import quote.fsrod.common.core.network.ModPacketHandler;
import quote.fsrod.common.core.network.item.CPacketItemUpdateSplitTagList;
import quote.fsrod.common.core.network.item.CPacketItemUpdateTag;
import quote.fsrod.common.structure.BasicStructure;

public interface IItemHasStructureData extends IItemHasFileData, IItemHasSplitTagList{
    public static final String TAG_STRUCTURE_DATA = "structure_data";
    
    public static void sendSplitNBTTagToServer(ItemStack stack){
        if(!IItemHasUUID.hasUUID(stack)) return;
        UUID uuid = IItemHasUUID.getUUID(stack);

        CompoundTag tagMerge = new CompoundTag();
        CompoundTag tagStructureData = stack.getOrCreateTag().getCompound(TAG_STRUCTURE_DATA);
        ListTag tagStateNums = tagStructureData.getList(BasicStructure.TAG_DATA_STATE_NUMS, 3);
        tagStructureData.remove(BasicStructure.TAG_DATA_STATE_NUMS);
        tagMerge.put(TAG_STRUCTURE_DATA, tagStructureData);

        // data exclude stateNum
        ModPacketHandler.CHANNEL.sendToServer(new CPacketItemUpdateTag(tagMerge, uuid, CPacketItemUpdateTag.Operation.ADD));
        // split stateNum
        int count = tagStateNums.size();
        int chunk = 2048;
        int split = Mth.ceil((float)count/chunk);
        UUID uuidSplitData = UUID.randomUUID();
        for(int i = 0; i < split; i++){
            ListTag nbtSplitList = new ListTag();
            for(int n = 0; n < chunk; n++){
                int idx = i*chunk + n;
                if(idx >= count) break;

                nbtSplitList.add(IntTag.valueOf(tagStateNums.getInt(idx)));
            }
            ModPacketHandler.CHANNEL.sendToServer(new CPacketItemUpdateSplitTagList(nbtSplitList, uuid, i, split, uuidSplitData));
        }
    }
}
