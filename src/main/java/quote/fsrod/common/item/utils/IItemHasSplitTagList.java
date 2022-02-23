package quote.fsrod.common.item.utils;

import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IItemHasSplitTagList extends IItemHasUUID{
    public static final String TAG_SPLIT = "nbt_split";
    public static final String TAG_SPLIT_UUID = "split_uuid";
    public static final String TAG_SPLIT_MAX = "split_max";
    public static final String TAG_SPLIT_TAG_TYPE = "split_tag_type";
    public static final String TAG_SPLIT_PARTS = "split_parts";
    public static final String TAG_SPLIT_PART = "split_part";
    public static final String TAG_SPLIT_PART_LIST = "split_list";
    public static final String TAG_SPLIT_PART_INDEX = "idx";

    public default void onResetSplitList(Player player, ItemStack stack){}
    public void onCompleteMergingSplitList(Player player, ItemStack stack, ListTag tagListMarged);
}