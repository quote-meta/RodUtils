package quote.fsrod.client.core.helper;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceContext.BlockMode;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent.MouseScrollEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import quote.fsrod.common.RodUtils;
import quote.fsrod.common.core.handler.ConfigHandler;
import quote.fsrod.common.core.network.ModPacketHandler;
import quote.fsrod.common.core.network.item.CPacketItemUpdateNBT;
import quote.fsrod.common.core.network.item.CPacketRodCloneStartBuilding;
import quote.fsrod.common.core.utils.ChatUtils;
import quote.fsrod.common.item.rod.ItemRodClone;
import quote.fsrod.common.item.utils.IItemHasUUID;

public class RodCloneHelper {

    private RodCloneHelper(){}

    @OnlyIn(Dist.CLIENT)
    public static void onMouseEvent(MouseScrollEvent event, ItemStack stack, PlayerEntity player){
        if (event.getScrollDelta() != 0 && player.isSneaking()){
            CompoundNBT nbt = stack.getOrCreateTag();
            if(!nbt.hasUniqueId(IItemHasUUID.NBT_UUID)) return;
            UUID uuid = IItemHasUUID.getUUID(stack);

            int oldReach = nbt.getInt(ItemRodClone.NBT_REACH_DISTANCE);
            int newReach = (int)MathHelper.clamp(oldReach + event.getScrollDelta() / 120, 2, 10);
            nbt.putInt(ItemRodClone.NBT_REACH_DISTANCE, newReach);

            ModPacketHandler.INSTANCE.sendToServer(new CPacketItemUpdateNBT(nbt, uuid, CPacketItemUpdateNBT.Operation.ADD));
            event.setCanceled(true);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void onRightClickTargetBlock(BlockPos blockPos, ItemStack stack, PlayerEntity player){
        CompoundNBT tag = stack.getOrCreateTag();

        Integer dimension = RodCloneHelper.getDimension(stack);
        if(dimension == null || !dimension.equals(player.dimension.getId())){
            // reset all settings
            sendRemoveNBTTagToServer(stack, ItemRodClone.NBT_DIMENSION);
            sendRemoveNBTTagToServer(stack, ItemRodClone.NBT_POINT_SCHEDULED_FACING);
            sendRemoveNBTTagToServer(stack, ItemRodClone.NBT_POINT_SCHEDULED);
            sendRemoveNBTTagToServer(stack, ItemRodClone.NBT_POINT_SCHEDULED_FACING);
            sendRemoveNBTTagToServer(stack, ItemRodClone.NBT_POINT_SCHEDULED);
        }

        CompoundNBT blockPosNearNBT = tag.getCompound(ItemRodClone.NBT_POINT_NEAR);
        if(blockPosNearNBT.isEmpty()){
            // saveNear
            tag.putInt(ItemRodClone.NBT_DIMENSION, player.dimension.getId());
            tag.put(ItemRodClone.NBT_POINT_NEAR, NBTUtil.writeBlockPos(blockPos));
            sendNBTTagToServer(stack);
            return;
        }
        
        CompoundNBT blockPosEndNBT = tag.getCompound(ItemRodClone.NBT_POINT_END);
        if(blockPosEndNBT.isEmpty()){
            // saveEnd
            BlockPos blockPosNear = NBTUtil.readBlockPos(blockPosNearNBT);
            int sizeX = Math.abs((blockPos.getX() - blockPosNear.getX()));
            int sizeY = Math.abs((blockPos.getY() - blockPosNear.getY()));
            int sizeZ = Math.abs((blockPos.getZ() - blockPosNear.getZ()));
            int maxLength = ConfigHandler.COMMON.rodCloneMaxLength.get();
            if(sizeX > maxLength || sizeY > maxLength || sizeZ > maxLength){
                ChatUtils.sendTranslatedChat(player, TextFormatting.RED, "fs.message.rodClone.warning.rangeTooLarge", maxLength);
                return;
            }
            tag.put(ItemRodClone.NBT_POINT_END, NBTUtil.writeBlockPos(blockPos));
            sendNBTTagToServer(stack);
            return;
        }

        CompoundNBT blockPosScheduledNBT = tag.getCompound(ItemRodClone.NBT_POINT_SCHEDULED);
        if(blockPosScheduledNBT.isEmpty()){
            // saveScheduled
            tag.put(ItemRodClone.NBT_POINT_SCHEDULED, NBTUtil.writeBlockPos(blockPos));
            tag.putInt(ItemRodClone.NBT_POINT_SCHEDULED_FACING, player.getHorizontalFacing().ordinal());
            sendNBTTagToServer(stack);
            return;
        }

        BlockPos blockPosScheduled = NBTUtil.readBlockPos(blockPosScheduledNBT);
        if(blockPos.equals(blockPosScheduled)){
            // build
            BlockPos blockPosNear = NBTUtil.readBlockPos(blockPosNearNBT);
            BlockPos blockPosEnd = NBTUtil.readBlockPos(blockPosEndNBT);
            
            Direction facingScheduled = getFacingScheduled(stack);
            if(facingScheduled == null){ // safe code
                sendRemoveNBTTagToServer(stack, ItemRodClone.NBT_POINT_SCHEDULED);
                sendRemoveNBTTagToServer(stack, ItemRodClone.NBT_POINT_SCHEDULED_FACING);
                sendNBTTagToServer(stack);
                return;
            }
            
            Rotation rotation = getRotation(getFacingAABB(blockPosNear, blockPosEnd), facingScheduled);
            if(ItemRodClone.isRodClone(stack)){
                ModPacketHandler.INSTANCE.sendToServer(new CPacketRodCloneStartBuilding(CPacketRodCloneStartBuilding.Operation.TRUE_BUILD, blockPosNear, blockPosEnd, blockPosScheduled, rotation));
            }
            else if(ItemRodClone.isRodTransfer(stack)){
                ModPacketHandler.INSTANCE.sendToServer(new CPacketRodCloneStartBuilding(CPacketRodCloneStartBuilding.Operation.TRANSFER, blockPosNear, blockPosEnd, blockPosScheduled, rotation));
            }
        }
        else{
            // saveScheduled
            tag.put(ItemRodClone.NBT_POINT_SCHEDULED, NBTUtil.writeBlockPos(blockPos));
            tag.putInt(ItemRodClone.NBT_POINT_SCHEDULED_FACING, player.getHorizontalFacing().ordinal());
            sendNBTTagToServer(stack);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void onRightClickWithPressShift(ItemStack stack, PlayerEntity player){
        if(getBlockPosScheduled(stack) != null){
            sendRemoveNBTTagToServer(stack, ItemRodClone.NBT_POINT_SCHEDULED);
            sendRemoveNBTTagToServer(stack, ItemRodClone.NBT_POINT_SCHEDULED_FACING);
        }
        else{
            sendRemoveNBTTagToServer(stack, ItemRodClone.NBT_POINT_NEAR);
            sendRemoveNBTTagToServer(stack, ItemRodClone.NBT_POINT_END);
            ChatUtils.sendTranslatedChat(player, TextFormatting.WHITE, "fs.message.rodClone.use.reset");
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void onRightClickBlock(RightClickBlock event, ItemStack stack, PlayerEntity player){
        if(player.isSneaking()){
            onRightClickWithPressShift(stack, player);
        }
        else{
            onRightClickTargetBlock(event.getPos(), stack, player);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void onRightClickItem(ItemStack stack, PlayerEntity player){
        if(player.isSneaking()){
            onRightClickWithPressShift(stack, player);
        }
        else{
            onRightClickTargetBlock(getBlockPosSeeing(stack, player, Minecraft.getInstance().getRenderPartialTicks()), stack, player);
        }
    }

    public static void handleBuilding(PlayerEntity player, BlockPos posNear, BlockPos posEnd, BlockPos posDst, Rotation rotation){
        World world = player.world;
        PlayerInventory inventory = player.inventory;
        int size = inventory.getSizeInventory();
        

        Direction facingDst = rotation.rotate(getFacingAABB(posNear, posEnd));
        AxisAlignedBB aabb = getScheduledAABB(posNear, posEnd, facingDst, posDst);
        if(aabb.intersects(new AxisAlignedBB(posNear, posEnd).expand(1, 1, 1))){
            ChatUtils.sendTranslatedChat(player, TextFormatting.RED, "fs.message.rodClone.warning.rangesInterfere");
            return;
        }

        BlockPos.getAllInBox(posNear, posEnd).forEach(src -> {
            BlockPos srcRelative = src.subtract(posNear);
            BlockPos posRotated = srcRelative.rotate(rotation);
            BlockPos dst = posDst.add(posRotated);
            
            if(world.isAirBlock(dst) && !world.isAirBlock(src)){
                BlockState blockState = world.getBlockState(src).rotate(rotation);
                Block block = blockState.getBlock();
                ItemStack stackSrc = new ItemStack(block);
                
                if(player.isCreative()){
                    world.setBlockState(dst, blockState);
                    return;
                }
                for(int i = 0; i < size; i++){
                    ItemStack stack = inventory.getStackInSlot(i);
                    if(stack.equals(stackSrc, false)){
                        stack.shrink(1);
                        world.setBlockState(dst, blockState);
                        break;
                    }
                }
            }
        });
    }

    public static void handleTransfering(PlayerEntity player, BlockPos posNear, BlockPos posEnd, BlockPos posDst, Rotation rotation){
        World world = player.world;

        Direction facingDst = rotation.rotate(getFacingAABB(posNear, posEnd));
        AxisAlignedBB aabb = getScheduledAABB(posNear, posEnd, facingDst, posDst);
        if(aabb.intersects(new AxisAlignedBB(posNear, posEnd).expand(1, 1, 1))){
            ChatUtils.sendTranslatedChat(player, TextFormatting.RED, "fs.message.rodClone.warning.rangesInterfere");
            return;
        }

        final Rotation rotation2;
        if(rotation == Rotation.CLOCKWISE_90){
            rotation2 = Rotation.COUNTERCLOCKWISE_90;
        }
        else if(rotation == Rotation.COUNTERCLOCKWISE_90){
            rotation2 = Rotation.CLOCKWISE_90;
        }
        else{
            rotation2 = rotation;
        }

        BlockPos.getAllInBox(posNear, posEnd).forEach(src -> {
            BlockPos srcRelative = src.subtract(posNear);
            BlockPos posRotated = srcRelative.rotate(rotation);
            BlockPos dst = posDst.add(posRotated);
            
            if(!world.isAirBlock(dst) || !world.isAirBlock(src)){
                BlockState blockState1 = world.getBlockState(src).rotate(rotation);
                TileEntity tileEntity1 = world.getTileEntity(src);
                CompoundNBT tag1 = new CompoundNBT();
                if(tileEntity1 != null){
                    tileEntity1.write(tag1);
                    world.removeTileEntity(src);
                }
                BlockState blockState2 = world.getBlockState(dst).rotate(rotation2);
                TileEntity tileEntity2 = world.getTileEntity(dst);
                CompoundNBT tag2 = new CompoundNBT();
                if(tileEntity2 != null){
                    tileEntity2.write(tag2);
                    world.removeTileEntity(dst);
                }

                world.setBlockState(dst, blockState1);
                if(tileEntity1 != null){
                    TileEntity tileEntity1Sub = TileEntity.create(tag1);
                    world.setTileEntity(dst, tileEntity1Sub);
                    tileEntity1Sub.setPos(dst);
                    tileEntity1Sub.setWorld(world);
                }
                world.setBlockState(src, blockState2);
                if(tileEntity2 != null){
                    TileEntity tileEntity2Sub = TileEntity.create(tag2);
                    world.setTileEntity(src, tileEntity2Sub);
                    tileEntity2Sub.setPos(src);
                    tileEntity2Sub.setWorld(world);
                }
            }
        });
    }

    @Nullable
    public static Integer getDimension(ItemStack stack){
        CompoundNBT tag = stack.getOrCreateTag();
        if(tag.contains(ItemRodClone.NBT_DIMENSION)){
            return tag.getInt(ItemRodClone.NBT_DIMENSION);
        }
        return null;
    }

    @Nullable
    public static BlockPos getBlockPosNear(ItemStack stack){
        CompoundNBT tag = stack.getOrCreateTag();
        CompoundNBT blockPosNearNBT = tag.getCompound(ItemRodClone.NBT_POINT_NEAR);
        if(!blockPosNearNBT.isEmpty()){
            return NBTUtil.readBlockPos(blockPosNearNBT);
        }
        return null;
    }

    @Nullable
    public static BlockPos getBlockPosEnd(ItemStack stack){
        CompoundNBT tag = stack.getOrCreateTag();
        CompoundNBT blockPosEndNBT = tag.getCompound(ItemRodClone.NBT_POINT_END);
        if(!blockPosEndNBT.isEmpty()){
            return NBTUtil.readBlockPos(blockPosEndNBT);
        }
        return null;
    }

    @Nullable
    public static BlockPos getBlockPosScheduled(ItemStack stack){
        CompoundNBT tag = stack.getOrCreateTag();
        CompoundNBT blockPosScheduledNBT = tag.getCompound(ItemRodClone.NBT_POINT_SCHEDULED);
        if(!blockPosScheduledNBT.isEmpty()){
            return NBTUtil.readBlockPos(blockPosScheduledNBT);
        }
        return null;
    }

    @Nullable
    public static Direction getFacingScheduled(ItemStack stack){
        CompoundNBT tag = stack.getOrCreateTag();
        return Direction.values()[tag.getInt(ItemRodClone.NBT_POINT_SCHEDULED_FACING)];
    }

    @Nullable
    public static AxisAlignedBB getScheduledAABB(ItemStack stack, PlayerEntity player, BlockPos posSeeing){
        BlockPos posNear = getBlockPosNear(stack);
        BlockPos posEnd = getBlockPosEnd(stack);
        if(posNear == null || posEnd == null) return null;
        Direction facingPlayer = player.getHorizontalFacing();
        
        return getScheduledAABB(posNear, posEnd, facingPlayer, posSeeing);
    }

    @Nonnull
    public static AxisAlignedBB getScheduledAABB(BlockPos posNear, BlockPos posEnd, Direction facingDst, BlockPos posDst){
        Direction facingSource = getFacingAABB(posNear, posEnd);
        BlockPos posDiff = posEnd.subtract(posNear);
        Rotation rot = getRotation(facingSource, facingDst);
        BlockPos posRotated = posDiff.rotate(rot);

        return new AxisAlignedBB(posDst, posDst.add(posRotated)).expand(1, 1, 1);
    }

    public static BlockPos getRotatedBlockPos(BlockPos src, Rotation rot){
        if(rot == Rotation.CLOCKWISE_180){
            return new BlockPos(-src.getX(), src.getY(), -src.getZ());
        }
        else if(rot == Rotation.CLOCKWISE_90){
            return new BlockPos(-src.getZ(), src.getY(), src.getX());
        }
        else if(rot == Rotation.COUNTERCLOCKWISE_90){
            return new BlockPos(src.getZ(), src.getY(), -src.getX());
        }
        return src;
    }

    @Nonnull
    public static Direction getFacingAABB(BlockPos posNear, BlockPos posEnd){
        if(posEnd.getX() >= posNear.getX()){
            if(posEnd.getZ() >= posNear.getZ()){
                return Direction.EAST;
            }
            return Direction.NORTH;
        }
        else{
            if(posEnd.getZ() >= posNear.getZ()){
                return Direction.SOUTH;
            }
            return Direction.WEST;
        }
    }
    
    public static Rotation getRotation(Direction facingSource, Direction facingTarget){
        int rotationInt = facingTarget.getHorizontalIndex() - facingSource.getHorizontalIndex();
        if(rotationInt > 2){
            rotationInt -= 4;
        }
        if(rotationInt < -1){
            rotationInt += 4;
        }
        if(rotationInt == -1){
            return Rotation.COUNTERCLOCKWISE_90;
        }
        else if(rotationInt == 1){
            return Rotation.CLOCKWISE_90;
        }
        else if(rotationInt == 2){
            return Rotation.CLOCKWISE_180;
        }
        return Rotation.NONE;
    }

    @OnlyIn(Dist.CLIENT)
    public static BlockPos getBlockPosSeeing(ItemStack stack, PlayerEntity player, float partialTicks){
        CompoundNBT tag = stack.getOrCreateTag();

        int distance = tag.getInt(ItemRodClone.NBT_REACH_DISTANCE);
        BlockPos blockPos = null;
        RayTraceResult objectMouseOver = RodUtils.proxy.getObjectMouseOver();
        boolean isLookingAir = true;
        if(objectMouseOver != null && objectMouseOver.getType() == Type.BLOCK){
            blockPos = new BlockPos(objectMouseOver.getHitVec());
            isLookingAir = player.world.isAirBlock(blockPos);
        }
        if (isLookingAir){
            Vec3d vec3d = player.getEyePosition(partialTicks);
            Vec3d vec3d1 = player.getLook(partialTicks).scale(distance);
            Vec3d vec3d2 = vec3d.add(vec3d1);
            objectMouseOver = player.world.rayTraceBlocks(new RayTraceContext(vec3d, vec3d2, BlockMode.COLLIDER, FluidMode.NONE, player));
            if(objectMouseOver != null && objectMouseOver.getType() == Type.BLOCK){
                blockPos = new BlockPos(objectMouseOver.getHitVec());
            }
            else{
                blockPos = new BlockPos(vec3d2);
            }
        }

        return blockPos;
    }

    @OnlyIn(Dist.CLIENT)
    public static void sendRemoveNBTTagToServer(ItemStack stack, String key){
        CompoundNBT nbt = stack.getTag();
        if(nbt == null || !nbt.hasUniqueId(IItemHasUUID.NBT_UUID)) return;
        UUID uuid = IItemHasUUID.getUUID(stack);

        CompoundNBT nbtRemove = new CompoundNBT();
        nbtRemove.putInt(key, 0);

        ModPacketHandler.INSTANCE.sendToServer(new CPacketItemUpdateNBT(nbtRemove, uuid, CPacketItemUpdateNBT.Operation.REMOVE));
    }

    @OnlyIn(Dist.CLIENT)
    public static void sendNBTTagToServer(ItemStack stack){
        CompoundNBT nbt = stack.getTag();
        if(nbt == null || !nbt.hasUniqueId(IItemHasUUID.NBT_UUID)) return;
        UUID uuid = IItemHasUUID.getUUID(stack);

        CompoundNBT nbtMerge = new CompoundNBT();
        copyTagInt(nbtMerge, nbt, ItemRodClone.NBT_DIMENSION);
        copyTagCompound(nbtMerge, nbt, ItemRodClone.NBT_POINT_NEAR);
        copyTagCompound(nbtMerge, nbt, ItemRodClone.NBT_POINT_END);
        copyTagCompound(nbtMerge, nbt, ItemRodClone.NBT_POINT_SCHEDULED);
        copyTagInt(nbtMerge, nbt, ItemRodClone.NBT_POINT_SCHEDULED_FACING);
        ModPacketHandler.INSTANCE.sendToServer(new CPacketItemUpdateNBT(nbtMerge, uuid, CPacketItemUpdateNBT.Operation.ADD));
    }

    private static void copyTagInt(CompoundNBT nbtDst, CompoundNBT nbtSource, String key){
        nbtDst.putInt(key, nbtSource.getInt(key));
    }

    private static void copyTagCompound(CompoundNBT nbtDst, CompoundNBT nbtSource, String key){
        INBT tag = nbtSource.get(key);
        if(tag == null) return;
        nbtDst.put(key, nbtSource.get(key));
    }
}