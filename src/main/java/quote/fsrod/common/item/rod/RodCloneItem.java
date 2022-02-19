package quote.fsrod.common.item.rod;

import java.util.Optional;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import quote.fsrod.common.core.helper.rod.SpaceReader;
import quote.fsrod.common.core.utils.ChatUtils;
import quote.fsrod.common.item.ModItems;
import quote.fsrod.common.item.utils.IItemHasSpaceInfoTag;
import quote.fsrod.common.item.utils.IItemHasUUID;
import quote.fsrod.common.item.utils.IItemNotifyServer;

public class RodCloneItem extends Item implements IItemHasSpaceInfoTag, IItemNotifyServer{

    public RodCloneItem(Properties p) {
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
        Inventory inventory = player.getInventory();
        int size = inventory.getContainerSize();

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
                        Rotation rotation = SpaceReader.getRotation(SpaceReader.getFacingAABB(blockPosNear, blockPosEnd), direction);

                        if(aabbSrc.intersects(aabbDst)){
                            String path = stack.getItem().getRegistryName().getPath();
                            ChatUtils.sendTranslatedChat(player, ChatFormatting.RED, "message.fsrod." + path + ".warning.rangesInterfere");
                            return;
                        }

                        for(BlockPos src: BlockPos.betweenClosed(blockPosNear, blockPosEnd)){
                            BlockPos srcRelative = src.subtract(blockPosNear);
                            BlockPos posRotated = srcRelative.rotate(rotation);
                            BlockPos dst = blockPosScheduled.offset(posRotated);

                            if(level.isEmptyBlock(dst) && !level.isEmptyBlock(src)){
                                BlockState blockState = level.getBlockState(src).rotate(level, src, rotation);
                                Block block = blockState.getBlock();
                                Item item = block.asItem();
                                
                                if(player.isCreative()){
                                    level.setBlockAndUpdate(dst, blockState);
                                    continue;
                                }
                                for(int i = 0; i < size; i++){
                                    ItemStack stackInventory = inventory.getItem(i);
                                    if(stackInventory.getItem() == item){
                                        stackInventory.shrink(1);
                                        level.setBlockAndUpdate(dst, blockState);
                                        break;
                                    }
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
        return stack.getItem() == ModItems.rodClone;
    }
}
