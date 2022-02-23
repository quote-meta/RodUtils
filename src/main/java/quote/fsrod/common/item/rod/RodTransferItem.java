package quote.fsrod.common.item.rod;

import java.util.Optional;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import quote.fsrod.common.core.helper.rod.SpaceReader;
import quote.fsrod.common.core.utils.ChatUtils;
import quote.fsrod.common.item.ModItems;
import quote.fsrod.common.item.utils.IItemHasSpaceInfoTag;
import quote.fsrod.common.item.utils.IItemHasUUID;
import quote.fsrod.common.item.utils.IItemNotifyServer;

public class RodTransferItem extends Item implements IItemHasSpaceInfoTag, IItemNotifyServer{

    public RodTransferItem(Properties p) {
        super(p);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int itemSlot, boolean isSelected) {
        if(!level.isClientSide){
            CompoundTag tag = stack.getOrCreateTag();
            IItemHasUUID.getOrCreateUUID(stack);
            int oldReach = tag.getInt(TAG_REACH_DISTANCE);
            int newReach = Mth.clamp(oldReach, 2, 10);
            tag.putInt(TAG_REACH_DISTANCE, newReach);
        }
        super.inventoryTick(stack, level, entity, itemSlot, isSelected);
    }

    @Override
    public void notified(Player player, ItemStack stack, CompoundTag tag) {
        Level level = player.level;

        Optional<BlockPos> possibleBlockPosNear = IItemHasSpaceInfoTag.getBlockPosNear(stack);
        Optional<BlockPos> possibleBlockPosEnd = IItemHasSpaceInfoTag.getBlockPosEnd(stack);
        Optional<BlockPos> possibleBlockPosScheduled = IItemHasSpaceInfoTag.getBlockPosScheduled(stack);
        Optional<Direction> possbleDirection = IItemHasSpaceInfoTag.getFacingScheduled(stack);

        possibleBlockPosNear.ifPresent(blockPosNear -> {
            possibleBlockPosEnd.ifPresent(blockPosEnd -> {
                possibleBlockPosScheduled.ifPresent(blockPosScheduled -> {
                    possbleDirection.ifPresent(direction -> {
                        AABB aabbSrc = new AABB(blockPosNear, blockPosEnd).expandTowards(1, 1, 1);
                        AABB aabbDst = SpaceReader.getScheduledAABB(blockPosNear, blockPosEnd, direction, blockPosScheduled);
                        Direction directionSrc = SpaceReader.getFacingAABB(blockPosNear, blockPosEnd);
                        Direction directionDst = direction;
                        Rotation rotation1 = SpaceReader.getRotation(directionSrc, directionDst);
                        Rotation rotation2 = SpaceReader.getRotation(directionDst, directionSrc);

                        if(aabbSrc.intersects(aabbDst)){
                            String path = stack.getItem().getRegistryName().getPath();
                            ChatUtils.sendTranslatedChat(player, ChatFormatting.RED, "message.fsrod." + path + ".warning.ranges_interfere");
                            return;
                        }

                        for(BlockPos src: BlockPos.betweenClosed(blockPosNear, blockPosEnd)){
                            BlockPos srcRelative = src.subtract(blockPosNear);
                            BlockPos posRotated = srcRelative.rotate(rotation1);
                            BlockPos dst = blockPosScheduled.offset(posRotated);

                            if(!level.isEmptyBlock(dst) || !level.isEmptyBlock(src)){
                                BlockState blockState1 = level.getBlockState(src).rotate(level, src, rotation1);
                                BlockEntity blockEntity1 = level.getBlockEntity(src);
                                CompoundTag tag1 = new CompoundTag();
                                if(blockEntity1 != null){
                                    tag1 = blockEntity1.saveWithFullMetadata();
                                    level.removeBlockEntity(src);
                                }

                                BlockState blockState2 = level.getBlockState(dst).rotate(level, dst, rotation2);
                                BlockEntity blockEntity2 = level.getBlockEntity(dst);
                                CompoundTag tag2 = new CompoundTag();
                                if(blockEntity2 != null){
                                    tag2 = blockEntity2.saveWithFullMetadata();
                                    level.removeBlockEntity(dst);
                                }

                                level.setBlockAndUpdate(dst, blockState1);
                                if(blockEntity1 != null){
                                    BlockEntity blockEntity1Sub = BlockEntity.loadStatic(dst, blockState1, tag1);
                                    level.setBlockEntity(blockEntity1Sub);
                                }
                                level.setBlockAndUpdate(src, blockState2);
                                if(blockEntity2 != null){
                                    BlockEntity blockEntity2Sub = BlockEntity.loadStatic(src, blockState2, tag2);
                                    level.setBlockEntity(blockEntity2Sub);
                                }
                            }
                        }
                    });
                });
            });
        });
    }

    @Override
    public CompoundTag getNotifyTag(Player player, ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag().copy();
        return tag;
    }
    
    public static boolean isItemOf(ItemStack stack){
        return stack.getItem() == ModItems.rodTransfer;
    }
}
