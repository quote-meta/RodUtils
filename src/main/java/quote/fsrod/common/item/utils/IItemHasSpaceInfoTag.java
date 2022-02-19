package quote.fsrod.common.item.utils;

import java.util.Optional;
import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import quote.fsrod.common.core.network.ModPacketHandler;
import quote.fsrod.common.core.network.item.CPacketItemUpdateTag;

public interface IItemHasSpaceInfoTag extends IItemHasUUID{
    public static final String TAG_REACH_DISTANCE = "reach_distance";
    public static final String TAG_DIMENSION = "point_dimension";
    public static final String TAG_POINT_NEAR = "point_near";
    public static final String TAG_POINT_END = "point_end";
    public static final String TAG_POINT_SCHEDULED = "point_scheduled";
    public static final String TAG_POINT_SCHEDULED_FACING = "point_scheduled_facing";

    public static Optional<String> getDimension(ItemStack stack){
        CompoundTag tag = stack.getOrCreateTag();

        if(tag.contains(TAG_DIMENSION)){
            return Optional.of(tag.getString(TAG_DIMENSION));
        }
        return Optional.empty();
    }

    public static Optional<BlockPos> getBlockPosNear(ItemStack stack){
        CompoundTag tag = stack.getOrCreateTag();
        CompoundTag blockPosNearTag = tag.getCompound(TAG_POINT_NEAR);

        if(!blockPosNearTag.isEmpty()){
            return Optional.of(NbtUtils.readBlockPos(blockPosNearTag));
        }
        return Optional.empty();
    }

    public static Optional<BlockPos> getBlockPosEnd(ItemStack stack){
        CompoundTag tag = stack.getOrCreateTag();
        CompoundTag blockPosEndTag = tag.getCompound(TAG_POINT_END);

        if(!blockPosEndTag.isEmpty()){
            return Optional.of(NbtUtils.readBlockPos(blockPosEndTag));
        }
        return Optional.empty();
    }

    public static Optional<BlockPos> getBlockPosScheduled(ItemStack stack){
        CompoundTag tag = stack.getOrCreateTag();
        CompoundTag blockPosScheduledTag = tag.getCompound(TAG_POINT_SCHEDULED);

        if(!blockPosScheduledTag.isEmpty()){
            return Optional.of(NbtUtils.readBlockPos(blockPosScheduledTag));
        }
        return Optional.empty();
    }

    public static Optional<Direction> getFacingScheduled(ItemStack stack){
        CompoundTag tag = stack.getOrCreateTag();
        int ordinal = tag.getInt(TAG_POINT_SCHEDULED_FACING);
        if(ordinal < 0 || ordinal >= Direction.values().length) return Optional.empty();
        return Optional.of(Direction.values()[ordinal]);
    }

    public static void sendRemoveNBTTagToServer(ItemStack stack, String key){
        if(!IItemHasUUID.hasUUID(stack)) return;
        UUID uuid = IItemHasUUID.getUUID(stack);

        CompoundTag tagRemove = new CompoundTag();

        tagRemove.putInt(key, 0);

        ModPacketHandler.CHANNEL.sendToServer(new CPacketItemUpdateTag(tagRemove, uuid, CPacketItemUpdateTag.Operation.REMOVE));
    }

    public static void sendNBTTagToServer(ItemStack stack){
        if(!IItemHasUUID.hasUUID(stack)) return;
        UUID uuid = IItemHasUUID.getUUID(stack);
        
        CompoundTag tag = stack.getOrCreateTag();
        CompoundTag tagMerge = new CompoundTag();
        copyTagString(tagMerge, tag, IItemHasSpaceInfoTag.TAG_DIMENSION);
        copyTagCompound(tagMerge, tag, IItemHasSpaceInfoTag.TAG_POINT_NEAR);
        copyTagCompound(tagMerge, tag, IItemHasSpaceInfoTag.TAG_POINT_END);
        copyTagCompound(tagMerge, tag, IItemHasSpaceInfoTag.TAG_POINT_SCHEDULED);
        copyTagInt(tagMerge, tag, IItemHasSpaceInfoTag.TAG_POINT_SCHEDULED_FACING);
        ModPacketHandler.CHANNEL.sendToServer(new CPacketItemUpdateTag(tagMerge, uuid, CPacketItemUpdateTag.Operation.ADD));
    }

    private static void copyTagInt(CompoundTag tagDst, CompoundTag tagSrc, String key){
        tagDst.putInt(key, tagSrc.getInt(key));
    }

    private static void copyTagString(CompoundTag tagDst, CompoundTag tagSrc, String key){
        tagDst.putString(key, tagSrc.getString(key));
    }

    private static void copyTagCompound(CompoundTag tagDst, CompoundTag tagSrc, String key){
        Tag tag = tagSrc.get(key);
        if(tag == null) return;
        tagDst.put(key, tagSrc.get(key));
    }
}
