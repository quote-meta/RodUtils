package quote.fsRod.client.core.helper;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import quote.fsRod.common.RodUtils;
import quote.fsRod.common.core.handler.ConfigHandler;
import quote.fsRod.common.core.network.ModPacketHandler;
import quote.fsRod.common.core.network.item.CPacketItemUpdateNBT;
import quote.fsRod.common.core.network.item.CPacketRodCloneStartBuilding;
import quote.fsRod.common.core.utils.ChatUtils;
import quote.fsRod.common.core.utils.ModUtils;
import quote.fsRod.common.item.rod.ItemRodClone;
import quote.fsRod.common.item.utils.IItemHasUUID;

public class RodCloneHelper {

    private RodCloneHelper(){}

    @SideOnly(Side.CLIENT)
    public static void onMouseEvent(MouseEvent event, ItemStack stack, EntityPlayer player){
        if (event.getDwheel() != 0 && player.isSneaking()){
            NBTTagCompound nbt = ModUtils.getTagThoughAbsent(stack);
            if(!nbt.hasUniqueId(IItemHasUUID.NBT_UUID)) return;
            UUID uuid = IItemHasUUID.getUUID(stack);

            int oldReach = nbt.getInteger(ItemRodClone.NBT_REACH_DISTANCE);
            int newReach = MathHelper.clamp(oldReach + event.getDwheel() / 120, 2, 10);
            nbt.setInteger(ItemRodClone.NBT_REACH_DISTANCE, newReach);

            ModPacketHandler.INSTANCE.sendToServer(new CPacketItemUpdateNBT(nbt, uuid, CPacketItemUpdateNBT.Operation.ADD));
            event.setCanceled(true);
        }
    }

    @SideOnly(Side.CLIENT)
    public static void onRightClickTargetBlock(BlockPos blockPos, ItemStack stack, EntityPlayer player){
        NBTTagCompound tag = ModUtils.getTagThoughAbsent(stack);

        Integer dimension = RodCloneHelper.getDimension(stack);
        if(dimension == null || !dimension.equals(player.dimension)){
            // reset all settings
            sendRemoveNBTTagToServer(stack, ItemRodClone.NBT_DIMENSION);
            sendRemoveNBTTagToServer(stack, ItemRodClone.NBT_POINT_SCHEDULED_FACING);
            sendRemoveNBTTagToServer(stack, ItemRodClone.NBT_POINT_SCHEDULED);
            sendRemoveNBTTagToServer(stack, ItemRodClone.NBT_POINT_SCHEDULED_FACING);
            sendRemoveNBTTagToServer(stack, ItemRodClone.NBT_POINT_SCHEDULED);
        }

        NBTTagCompound blockPosNearNBT = tag.getCompoundTag(ItemRodClone.NBT_POINT_NEAR);
        if(blockPosNearNBT.hasNoTags()){
            // saveNear
            tag.setInteger(ItemRodClone.NBT_DIMENSION, player.dimension);
            tag.setTag(ItemRodClone.NBT_POINT_NEAR, NBTUtil.createPosTag(blockPos));
            sendNBTTagToServer(stack);
            return;
        }
        
        NBTTagCompound blockPosEndNBT = tag.getCompoundTag(ItemRodClone.NBT_POINT_END);
        if(blockPosEndNBT.hasNoTags()){
            // saveEnd
            BlockPos blockPosNear = NBTUtil.getPosFromTag(blockPosNearNBT);
            int sizeX = Math.abs((blockPos.getX() - blockPosNear.getX()));
            int sizeY = Math.abs((blockPos.getY() - blockPosNear.getY()));
            int sizeZ = Math.abs((blockPos.getZ() - blockPosNear.getZ()));
            if(sizeX > ConfigHandler.rodCloneMaxLength || sizeY > ConfigHandler.rodCloneMaxLength || sizeZ > ConfigHandler.rodCloneMaxLength){
                ChatUtils.sendTranslatedChat(player, TextFormatting.RED, "fs.message.rodClone.warning.rangeTooLarge", ConfigHandler.rodCloneMaxLength);
                return;
            }
            tag.setTag(ItemRodClone.NBT_POINT_END, NBTUtil.createPosTag(blockPos));
            sendNBTTagToServer(stack);
            return;
        }

        NBTTagCompound blockPosScheduledNBT = tag.getCompoundTag(ItemRodClone.NBT_POINT_SCHEDULED);
        if(blockPosScheduledNBT.hasNoTags()){
            // saveScheduled
            tag.setTag(ItemRodClone.NBT_POINT_SCHEDULED, NBTUtil.createPosTag(blockPos));
            tag.setInteger(ItemRodClone.NBT_POINT_SCHEDULED_FACING, player.getHorizontalFacing().ordinal());
            sendNBTTagToServer(stack);
            return;
        }

        BlockPos blockPosScheduled = NBTUtil.getPosFromTag(blockPosScheduledNBT);
        if(blockPos.equals(blockPosScheduled)){
            // build
            BlockPos blockPosNear = NBTUtil.getPosFromTag(blockPosNearNBT);
            BlockPos blockPosEnd = NBTUtil.getPosFromTag(blockPosEndNBT);
            
            EnumFacing facingScheduled = getFacingScheduled(stack);
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
            tag.setTag(ItemRodClone.NBT_POINT_SCHEDULED, NBTUtil.createPosTag(blockPos));
            tag.setInteger(ItemRodClone.NBT_POINT_SCHEDULED_FACING, player.getHorizontalFacing().ordinal());
            sendNBTTagToServer(stack);
        }
    }

    @SideOnly(Side.CLIENT)
    public static void onRightClickWithPressShift(ItemStack stack, EntityPlayer player){
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

    @SideOnly(Side.CLIENT)
    public static void onRightClickBlock(RightClickBlock event, ItemStack stack, EntityPlayer player){
        if(player.isSneaking()){
            onRightClickWithPressShift(stack, player);
        }
        else{
            onRightClickTargetBlock(event.getPos(), stack, player);
        }
    }

    @SideOnly(Side.CLIENT)
    public static void onRightClickItem(ItemStack stack, EntityPlayer player){
        if(player.isSneaking()){
            onRightClickWithPressShift(stack, player);
        }
        else{
            onRightClickTargetBlock(getBlockPosSeeing(stack, player, Minecraft.getMinecraft().getRenderPartialTicks()), stack, player);
        }
    }

    public static void handleBuilding(EntityPlayer player, BlockPos posNear, BlockPos posEnd, BlockPos posDst, Rotation rotation){
        World world = player.world;
        InventoryPlayer inventory = player.inventory;
        int size = inventory.getSizeInventory();
        

        EnumFacing facingDst = rotation.rotate(getFacingAABB(posNear, posEnd));
        AxisAlignedBB aabb = getScheduledAABB(posNear, posEnd, facingDst, posDst);
        if(aabb.intersects(new AxisAlignedBB(posNear, posEnd).expand(1, 1, 1))){
            ChatUtils.sendTranslatedChat(player, TextFormatting.RED, "fs.message.rodClone.warning.rangesInterfere");
            return;
        }

        for(BlockPos src: BlockPos.getAllInBox(posNear, posEnd)){
            BlockPos srcRelative = src.subtract(posNear);
            BlockPos posRotated = srcRelative.rotate(rotation);
            BlockPos dst = posDst.add(posRotated);
            
            if(world.isAirBlock(dst) && !world.isAirBlock(src)){
                IBlockState blockState = world.getBlockState(src).withRotation(rotation);
                Block block = blockState.getBlock();
                Item item = Item.getItemFromBlock(block);
                int damage = block.damageDropped(blockState);
                
                if(player.isCreative()){
                    world.setBlockState(dst, blockState);
                    continue;
                }
                for(int i = 0; i < size; i++){
                    ItemStack stack = inventory.getStackInSlot(i);
                    if(stack.getItem() == item && stack.getItemDamage() == damage){
                        stack.shrink(1);
                        world.setBlockState(dst, blockState);
                        break;
                    }
                }
            }
        }
    }

    public static void handleTransfering(EntityPlayer player, BlockPos posNear, BlockPos posEnd, BlockPos posDst, Rotation rotation){
        World world = player.world;

        EnumFacing facingDst = rotation.rotate(getFacingAABB(posNear, posEnd));
        AxisAlignedBB aabb = getScheduledAABB(posNear, posEnd, facingDst, posDst);
        if(aabb.intersects(new AxisAlignedBB(posNear, posEnd).expand(1, 1, 1))){
            ChatUtils.sendTranslatedChat(player, TextFormatting.RED, "fs.message.rodClone.warning.rangesInterfere");
            return;
        }

        Rotation rotation2 = rotation;
        if(rotation == Rotation.CLOCKWISE_90){
            rotation2 = Rotation.COUNTERCLOCKWISE_90;
        }
        else if(rotation == Rotation.COUNTERCLOCKWISE_90){
            rotation2 = Rotation.CLOCKWISE_90;
        }

        for(BlockPos src: BlockPos.getAllInBox(posNear, posEnd)){
            BlockPos srcRelative = src.subtract(posNear);
            BlockPos posRotated = srcRelative.rotate(rotation);
            BlockPos dst = posDst.add(posRotated);
            
            if(!world.isAirBlock(dst) || !world.isAirBlock(src)){
                IBlockState blockState1 = world.getBlockState(src).withRotation(rotation);
                TileEntity tileEntity1 = world.getTileEntity(src);
                NBTTagCompound tag1 = new NBTTagCompound();
                if(tileEntity1 != null){
                    tileEntity1.writeToNBT(tag1);
                    world.removeTileEntity(src);
                }
                IBlockState blockState2 = world.getBlockState(dst).withRotation(rotation2);
                TileEntity tileEntity2 = world.getTileEntity(dst);
                NBTTagCompound tag2 = new NBTTagCompound();
                if(tileEntity2 != null){
                    tileEntity2.writeToNBT(tag2);
                    world.removeTileEntity(dst);
                }

                world.setBlockState(dst, blockState1);
                if(tileEntity1 != null){
                    TileEntity tileEntity1Sub = TileEntity.create(world, tag1);
                    world.setTileEntity(dst, tileEntity1Sub);
                    tileEntity1Sub.setPos(dst);
                    tileEntity1Sub.setWorld(world);
                }
                world.setBlockState(src, blockState2);
                if(tileEntity2 != null){
                    TileEntity tileEntity2Sub = TileEntity.create(world, tag2);
                    world.setTileEntity(src, tileEntity2Sub);
                    tileEntity2Sub.setPos(src);
                    tileEntity2Sub.setWorld(world);
                }
            }
        }
    }

    @Nullable
    public static Integer getDimension(ItemStack stack){
        NBTTagCompound tag = ModUtils.getTagThoughAbsent(stack);
        if(tag.hasKey(ItemRodClone.NBT_DIMENSION)){
            return tag.getInteger(ItemRodClone.NBT_DIMENSION);
        }
        return null;
    }

    @Nullable
    public static BlockPos getBlockPosNear(ItemStack stack){
        NBTTagCompound tag = ModUtils.getTagThoughAbsent(stack);
        NBTTagCompound blockPosNearNBT = tag.getCompoundTag(ItemRodClone.NBT_POINT_NEAR);
        if(!blockPosNearNBT.hasNoTags()){
            return NBTUtil.getPosFromTag(blockPosNearNBT);
        }
        return null;
    }

    @Nullable
    public static BlockPos getBlockPosEnd(ItemStack stack){
        NBTTagCompound tag = ModUtils.getTagThoughAbsent(stack);
        NBTTagCompound blockPosEndNBT = tag.getCompoundTag(ItemRodClone.NBT_POINT_END);
        if(!blockPosEndNBT.hasNoTags()){
            return NBTUtil.getPosFromTag(blockPosEndNBT);
        }
        return null;
    }

    @Nullable
    public static BlockPos getBlockPosScheduled(ItemStack stack){
        NBTTagCompound tag = ModUtils.getTagThoughAbsent(stack);
        NBTTagCompound blockPosScheduledNBT = tag.getCompoundTag(ItemRodClone.NBT_POINT_SCHEDULED);
        if(!blockPosScheduledNBT.hasNoTags()){
            return NBTUtil.getPosFromTag(blockPosScheduledNBT);
        }
        return null;
    }

    @Nullable
    public static EnumFacing getFacingScheduled(ItemStack stack){
        NBTTagCompound tag = ModUtils.getTagThoughAbsent(stack);
        return EnumFacing.values()[tag.getInteger(ItemRodClone.NBT_POINT_SCHEDULED_FACING)];
    }

    @Nullable
    public static AxisAlignedBB getScheduledAABB(ItemStack stack, EntityPlayer player, BlockPos posSeeing){
        BlockPos posNear = getBlockPosNear(stack);
        BlockPos posEnd = getBlockPosEnd(stack);
        if(posNear == null || posEnd == null) return null;
        EnumFacing facingPlayer = player.getHorizontalFacing();
        
        return getScheduledAABB(posNear, posEnd, facingPlayer, posSeeing);
    }

    @Nonnull
    public static AxisAlignedBB getScheduledAABB(BlockPos posNear, BlockPos posEnd, EnumFacing facingDst, BlockPos posDst){
        EnumFacing facingSource = getFacingAABB(posNear, posEnd);
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
    public static EnumFacing getFacingAABB(BlockPos posNear, BlockPos posEnd){
        if(posEnd.getX() >= posNear.getX()){
            if(posEnd.getZ() >= posNear.getZ()){
                return EnumFacing.EAST;
            }
            return EnumFacing.NORTH;
        }
        else{
            if(posEnd.getZ() >= posNear.getZ()){
                return EnumFacing.SOUTH;
            }
            return EnumFacing.WEST;
        }
    }
    
    public static Rotation getRotation(EnumFacing facingSource, EnumFacing facingTarget){
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

    @SideOnly(Side.CLIENT)
    public static BlockPos getBlockPosSeeing(ItemStack stack, EntityPlayer player, float partialTicks){
        NBTTagCompound tag = ModUtils.getTagThoughAbsent(stack);

        int distance = tag.getInteger(ItemRodClone.NBT_REACH_DISTANCE);
        BlockPos blockPos = null;
        RayTraceResult objectMouseOver = RodUtils.proxy.getObjectMouseOver();
        boolean isLookingAir = true;
        if(objectMouseOver != null && objectMouseOver.getBlockPos() != null){
            blockPos = objectMouseOver.getBlockPos();
            isLookingAir = player.world.isAirBlock(blockPos);
        }
        if (isLookingAir){
            Vec3d vec3d = player.getPositionEyes(partialTicks);
            Vec3d vec3d1 = player.getLook(partialTicks).scale(distance);
            Vec3d vec3d2 = vec3d.add(vec3d1);
            objectMouseOver = player.world.rayTraceBlocks(vec3d, vec3d2);
            if(objectMouseOver != null && objectMouseOver.getBlockPos() != null){
                blockPos = objectMouseOver.getBlockPos();
            }
            else{
                blockPos = new BlockPos(vec3d2);
            }
        }

        return blockPos;
    }

    @SideOnly(Side.CLIENT)
    public static void sendRemoveNBTTagToServer(ItemStack stack, String key){
        NBTTagCompound nbt = stack.getTagCompound();
        if(nbt == null || !nbt.hasUniqueId(IItemHasUUID.NBT_UUID)) return;
        UUID uuid = IItemHasUUID.getUUID(stack);

        NBTTagCompound nbtRemove = new NBTTagCompound();
        nbtRemove.setInteger(key, 0);

        ModPacketHandler.INSTANCE.sendToServer(new CPacketItemUpdateNBT(nbtRemove, uuid, CPacketItemUpdateNBT.Operation.REMOVE));
    }

    @SideOnly(Side.CLIENT)
    public static void sendNBTTagToServer(ItemStack stack){
        NBTTagCompound nbt = stack.getTagCompound();
        if(nbt == null || !nbt.hasUniqueId(IItemHasUUID.NBT_UUID)) return;
        UUID uuid = IItemHasUUID.getUUID(stack);

        NBTTagCompound nbtMerge = new NBTTagCompound();
        copyTagInt(nbtMerge, nbt, ItemRodClone.NBT_DIMENSION);
        copyTagCompound(nbtMerge, nbt, ItemRodClone.NBT_POINT_NEAR);
        copyTagCompound(nbtMerge, nbt, ItemRodClone.NBT_POINT_END);
        copyTagCompound(nbtMerge, nbt, ItemRodClone.NBT_POINT_SCHEDULED);
        copyTagInt(nbtMerge, nbt, ItemRodClone.NBT_POINT_SCHEDULED_FACING);
        ModPacketHandler.INSTANCE.sendToServer(new CPacketItemUpdateNBT(nbtMerge, uuid, CPacketItemUpdateNBT.Operation.ADD));
    }

    private static void copyTagInt(NBTTagCompound nbtDst, NBTTagCompound nbtSource, String key){
        nbtDst.setInteger(key, nbtSource.getInteger(key));
    }

    private static void copyTagCompound(NBTTagCompound nbtDst, NBTTagCompound nbtSource, String key){
        NBTBase tag = nbtSource.getTag(key);
        if(tag == null) return;
        nbtDst.setTag(key, nbtSource.getTag(key));
    }
}