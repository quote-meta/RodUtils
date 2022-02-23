package quote.fsrod.common.core.helper.rod;

import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.InputEvent.MouseScrollEvent;
import quote.fsrod.common.core.helper.rod.state.RodState;
import quote.fsrod.common.core.helper.rod.state.RodStateLoadSpace;
import quote.fsrod.common.core.helper.rod.state.RodStateRecollection;
import quote.fsrod.common.core.helper.rod.state.RodStateSaveSpace;
import quote.fsrod.common.core.helper.rod.state.RodStateSettingEndPos;
import quote.fsrod.common.core.helper.rod.state.RodStateSettingScheduledPos;
import quote.fsrod.common.core.helper.rod.state.RodStateSettingStartPos;
import quote.fsrod.common.core.helper.rod.state.RodStateStandbyInput;
import quote.fsrod.common.core.network.ModPacketHandler;
import quote.fsrod.common.core.network.item.CPacketItemUpdateTag;
import quote.fsrod.common.core.utils.ChatUtils;
import quote.fsrod.common.item.rod.RodRecollectionItem;
import quote.fsrod.common.item.utils.IItemHasFileData;
import quote.fsrod.common.item.utils.IItemHasSpaceInfoTag;
import quote.fsrod.common.item.utils.IItemHasSplitTagList;
import quote.fsrod.common.item.utils.IItemHasStructureData;
import quote.fsrod.common.item.utils.IItemHasUUID;
import quote.fsrod.common.structure.BasicStructure;

public class RodRecollectionHelper {
    
    public static void onMouseScrollEvent(MouseScrollEvent event, ItemStack stack, Player player){
        if (event.getScrollDelta() != 0 && player.isShiftKeyDown()){
            if(!IItemHasUUID.hasUUID(stack)) return;
            CompoundTag tag = stack.getOrCreateTag();
            UUID uuid = IItemHasUUID.getOrCreateUUID(stack);

            int oldReach = tag.getInt(IItemHasSpaceInfoTag.TAG_REACH_DISTANCE);
            int newReach = (int)Mth.clamp(oldReach + event.getScrollDelta(), 2, 10);
            tag.putInt(IItemHasSpaceInfoTag.TAG_REACH_DISTANCE, newReach);

            ModPacketHandler.CHANNEL.sendToServer(new CPacketItemUpdateTag(tag, uuid, CPacketItemUpdateTag.Operation.ADD));
            event.setCanceled(true);
        }
    }

    public static void onRightClickItem(ItemStack stack, Player player){
        resetTagIfOtherDimension(stack, player);

        RodState state = getCurrentRodState(stack);
        if(player.isShiftKeyDown()){
            state.onRightClickWithPressShift(stack, player);
        }
        else{
            state.onRightClickTargetBlock(getBlockPosSeeing(stack, player, Minecraft.getInstance().getDeltaFrameTime()), stack, player);
        }
    }

    private static void resetTagIfOtherDimension(ItemStack stack, Player player){
        Optional<String> possibleDimension = IItemHasSpaceInfoTag.getDimension(stack);
        if(possibleDimension.isPresent() && !player.level.dimension().location().toString().equals(IItemHasSpaceInfoTag.getDimension(stack).get())){
            CompoundTag tag = stack.getOrCreateTag();
            tag.remove(IItemHasSpaceInfoTag.TAG_DIMENSION);
            tag.remove(IItemHasSpaceInfoTag.TAG_POINT_NEAR);
            tag.remove(IItemHasSpaceInfoTag.TAG_POINT_END);
            tag.remove(IItemHasSpaceInfoTag.TAG_POINT_SCHEDULED);
            tag.remove(IItemHasSpaceInfoTag.TAG_POINT_SCHEDULED_FACING);
            IItemHasSpaceInfoTag.sendRemoveNBTTagToServer(stack, IItemHasSpaceInfoTag.TAG_DIMENSION);
            IItemHasSpaceInfoTag.sendRemoveNBTTagToServer(stack, IItemHasSpaceInfoTag.TAG_POINT_NEAR);
            IItemHasSpaceInfoTag.sendRemoveNBTTagToServer(stack, IItemHasSpaceInfoTag.TAG_POINT_END);
            IItemHasSpaceInfoTag.sendRemoveNBTTagToServer(stack, IItemHasSpaceInfoTag.TAG_POINT_SCHEDULED);
            IItemHasSpaceInfoTag.sendRemoveNBTTagToServer(stack, IItemHasSpaceInfoTag.TAG_POINT_SCHEDULED_FACING);
        }
    }

    public static RodState getCurrentRodState(ItemStack stack){
        CompoundTag tag = stack.getOrCreateTag();
        
        if(tag.getString(IItemHasFileData.TAG_FILE_NAME).isEmpty()) return new RodStateStandbyInput();
        if(IItemHasFileData.existsFile(stack)){
            // FILE => WORLD
            if(!tag.getCompound(IItemHasStructureData.TAG_STRUCTURE_DATA).isEmpty() && tag.getCompound(IItemHasSplitTagList.TAG_SPLIT).isEmpty()){
                if(tag.getCompound(IItemHasSpaceInfoTag.TAG_POINT_SCHEDULED).isEmpty()) return new RodStateSettingScheduledPos();
                return new RodStateRecollection();
            }
            else{
                return new RodStateLoadSpace();
            }
        }
        else{
            // WORLD => FILE
            if(tag.getCompound(IItemHasSpaceInfoTag.TAG_POINT_NEAR).isEmpty()) return new RodStateSettingStartPos();
            if(tag.getCompound(IItemHasSpaceInfoTag.TAG_POINT_END).isEmpty()) return new RodStateSettingEndPos();
            return new RodStateSaveSpace();
        }
    }

    public static void setFileName(ItemStack stack, String filename, @Nullable Player player){
        if(filename.contains(" ") || filename.length() > 16){
            if(player != null){
                String path = stack.getItem().getRegistryName().getPath();
                ChatUtils.sendTranslatedChat(player, ChatFormatting.RED, "message.fsrod." + path + ".invalid_file_name");
            }
            return;
        }
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString(RodRecollectionItem.TAG_FILE_NAME, filename);

        tag.remove(RodRecollectionItem.TAG_STRUCTURE_DATA);
        tag.remove(RodRecollectionItem.TAG_POINT_NEAR);
        tag.remove(RodRecollectionItem.TAG_POINT_END);
        tag.remove(RodRecollectionItem.TAG_POINT_SCHEDULED);
        tag.remove(RodRecollectionItem.TAG_POINT_SCHEDULED_FACING);
    }

    @Nullable
    public static BlockPos getBlockPosData(ItemStack stack){
        CompoundTag tag = stack.getOrCreateTag();
        CompoundTag tagData = tag.getCompound(IItemHasStructureData.TAG_STRUCTURE_DATA);
        if(tagData.isEmpty()) return null;
        return new BlockPos(
            tagData.getInt(BasicStructure.TAG_DATA_SIZE_X) - 1,
            tagData.getInt(BasicStructure.TAG_DATA_SIZE_Y) - 1,
            tagData.getInt(BasicStructure.TAG_DATA_SIZE_Z) - 1);
    }


    @SuppressWarnings("resource")
    public static BlockPos getBlockPosSeeing(ItemStack stack, Player player, float partialTicks){
        CompoundTag tag = stack.getOrCreateTag();

        int distance = tag.getInt(IItemHasSpaceInfoTag.TAG_REACH_DISTANCE);
        BlockPos blockPos = null;
        HitResult objectMouseOver = Minecraft.getInstance().hitResult;

        boolean isLookingAir = true;
        if (objectMouseOver instanceof BlockHitResult && ((BlockHitResult)objectMouseOver).getBlockPos() != null){
            blockPos = ((BlockHitResult)objectMouseOver).getBlockPos();
            isLookingAir = player.level.getBlockState(blockPos).isAir();
        }
        if (isLookingAir){
            Vec3 eyePos = player.getEyePosition(partialTicks);
            Vec3 viewVec = player.getViewVector(partialTicks).scale(distance);
            Vec3 viewPos = eyePos.add(viewVec);
            objectMouseOver = player.level.clip(new ClipContext(eyePos, viewPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null));
            if(objectMouseOver instanceof BlockHitResult && ((BlockHitResult)objectMouseOver).getBlockPos() != null){
                blockPos = ((BlockHitResult)objectMouseOver).getBlockPos();
            }
            else{
                blockPos = new BlockPos(viewPos);
            }
        }

        return blockPos;
    }
}
