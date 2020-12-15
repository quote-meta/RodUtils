package quote.fsRod.common.item.utils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;

public interface IItemHasSplitNBTList extends IItemHasUUID{
    public static final String NBT_SPLIT = "nbtSplit";
    public static final String NBT_SPLIT_UUID = "splitUUID";
    public static final String NBT_SPLIT_MAX = "splitMax";
    public static final String NBT_SPLIT_TAG_TYPE = "splitTagType";
    public static final String NBT_SPLIT_PARTS = "splitParts";
    public static final String NBT_SPLIT_PART = "splitPart";
    public static final String NBT_SPLIT_PART_LIST = "splitList";
    public static final String NBT_SPLIT_PART_INDEX = "Idx";

    public default void onResetSplitList(EntityPlayer player, ItemStack stack){}
    public void onCompleteMergingSplitList(EntityPlayer player, ItemStack stack, NBTTagList nbtListMarged);
}