package quote.fsrod.common.item.rod;

import java.util.List;
import java.util.Optional;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import quote.fsrod.common.core.handler.ModSoundHandler;
import quote.fsrod.common.core.helper.rod.SpaceReader;
import quote.fsrod.common.core.utils.ChatUtils;
import quote.fsrod.common.item.ModItems;
import quote.fsrod.common.item.utils.IItemHasSpaceInfoTag;
import quote.fsrod.common.item.utils.IItemHasStructureData;
import quote.fsrod.common.item.utils.IItemHasUUID;
import quote.fsrod.common.item.utils.IItemNotifyServer;
import quote.fsrod.common.property.item.IStructureDataProperty;
import quote.fsrod.common.property.item.StructureDataProperty;
import quote.fsrod.common.structure.BasicStructure;

public class RodRecollectionItem extends Item implements IItemHasSpaceInfoTag, IItemNotifyServer, IItemHasStructureData{

    public RodRecollectionItem(Properties p) {
        super(p);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag tooltipFlag) {
        tooltip.add(new TranslatableComponent(this.getDescriptionId() + ".info"));
        CompoundTag tag = stack.getTag();
        String fileName = "";
        if (tag != null) {
            fileName = tag.getString(TAG_FILE_NAME);
        }
        if (fileName.isEmpty()) {
            tooltip.add(new TranslatableComponent(this.getDescriptionId() + ".info.nofilename"));
        } else {
            tooltip.add(new TranslatableComponent(this.getDescriptionId() + ".info.filename", fileName));
        }
    }

    public static final String PATH = "share_data/fancifulspecters/recollection/";

    @Override
    public String getBasePath() {
        return PATH;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int itemSlot, boolean isSelected) {
        if(!level.isClientSide){
            CompoundTag tag = stack.getOrCreateTag();
            IItemHasUUID.getOrCreateUUID(stack);
            int oldReach = tag.getInt(TAG_REACH_DISTANCE);
            int newReach = Mth.clamp(oldReach, 2, 10);
            tag.putInt(TAG_REACH_DISTANCE, newReach);

            if(StructureDataProperty.of(stack).flatMap(IStructureDataProperty::getStructureData).isEmpty()){
                tag.putBoolean(TAG_STRUCTURE_DATA_LOADED, false);
            }
        }
        super.inventoryTick(stack, level, entity, itemSlot, isSelected);
    }

    @Override
    public void notified(Player player, ItemStack stack, CompoundTag tagPacket) {
        Level level = player.level;
        Inventory inventory = player.getInventory();
        int size = inventory.getContainerSize();

        Optional<IStructureDataProperty> property = StructureDataProperty.of(stack);
        Optional<BasicStructure> possibleStructure = property.flatMap(IStructureDataProperty::getStructureData);
        Optional<BlockPos> possbleBlockPosScheduled = IItemHasSpaceInfoTag.getBlockPosScheduled(stack);
        Optional<Direction> possbleDirection = IItemHasSpaceInfoTag.getFacingScheduled(stack);

        if(!possibleStructure.isPresent() || !possbleBlockPosScheduled.isPresent() || !possbleDirection.isPresent()){
            String path = stack.getItem().getRegistryName().getPath();
            ChatUtils.sendTranslatedChat(player, ChatFormatting.RED, "message.fsrod." + path + ".use.build.failed");
            return;
        }

        BlockPos posDst = possbleBlockPosScheduled.get();
        Direction direction = possbleDirection.get();
        Rotation rotation = SpaceReader.getRotation(Direction.EAST, direction);

        BasicStructure structure = possibleStructure.get();
        int sizeX = structure.getSizeX();
        int sizeY = structure.getSizeY();
        int sizeZ = structure.getSizeZ();
        List<BlockState> states = structure.getStates();
        int[] stateNums = structure.getSteteNums();

        int i = 0;
        // use fori roop to correct rotate 
        // can't use foreach AABB
        for (int z = 0; z < sizeZ; z++) {
            for (int y = 0; y < sizeY; y++) {
                for (int x = 0; x < sizeX; x++) {
                    BlockPos posRelative = new BlockPos(x, y, z).rotate(rotation);
                    BlockPos dst = posDst.offset(posRelative);
                    if(!level.isEmptyBlock(dst)){
                        i++;
                        continue;
                    }
                    int stateNum = stateNums[i];
                    BlockState state = states.get(stateNum);
                    if(state != null){
                        BlockState stateRotated = state.rotate(level, dst, rotation);
                        Block block = stateRotated.getBlock();
                        ItemStack stackSrc = new ItemStack(block);

                        if(player.isCreative()){
                            level.setBlockAndUpdate(dst, stateRotated);
                            i++;
                            continue;
                        }
                        for(int j = 0; j < size; j++){
                            ItemStack stackInventory = inventory.getItem(j);
                            if(stackInventory.equals(stackSrc, false)){
                                stackInventory.shrink(1);
                                level.setBlockAndUpdate(dst, stateRotated);
                                break;
                            }
                        }
                    }
                    i++;
                }
            }
        }
        level.playSound((Player)null, new BlockPos(player.position()), ModSoundHandler.itemRodSuccess, SoundSource.PLAYERS, 1.0f, (float)(1.0f + 0.05f*level.random.nextGaussian()));
    }

    @Override
    public CompoundTag getNotifyTag(Player player, ItemStack stack) {
        CompoundTag tag = new CompoundTag();
        return tag;
    }

    @Override
    public void onCompleteMergingSplitList(Player player, ItemStack stack, ListTag nbtListMarged) {
        StructureDataProperty.of(stack).ifPresent(p -> p.completeMergingStructureData(nbtListMarged));
        stack.getOrCreateTag().putBoolean(TAG_STRUCTURE_DATA_LOADED, true);

        String path = stack.getItem().getRegistryName().getPath();
        ChatUtils.sendTranslatedChat(player, ChatFormatting.GREEN, "message.fsrod." + path + ".use.load.success");
    }

    public static boolean isItemOf(ItemStack stack){
        return stack.getItem() == ModItems.rodRecollection;
    }
}
