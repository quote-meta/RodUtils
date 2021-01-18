package quote.fsrod.client.core.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
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
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
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
import quote.fsrod.common.core.handler.ModSoundHandler;
import quote.fsrod.common.core.network.ModPacketHandler;
import quote.fsrod.common.core.network.item.CPacketItemUpdateNBT;
import quote.fsrod.common.core.network.item.CPacketItemUpdateSplitNBTList;
import quote.fsrod.common.core.network.item.CPacketRodReincarnationStartBuilding;
import quote.fsrod.common.core.utils.ChatUtils;
import quote.fsrod.common.core.utils.ModLogger;
import quote.fsrod.common.item.rod.ItemRodReincarnation;
import quote.fsrod.common.item.utils.IItemHasSplitNBTList;
import quote.fsrod.common.item.utils.IItemHasUUID;
import quote.fsrod.common.structure.BasicStrucure;

public class RodReincarnationHelper {

    public static final String PATH_REINCARNATION_HOME = "share_data/fancifulspecters/reincarnation/";

    private RodReincarnationHelper(){}

    @OnlyIn(Dist.CLIENT)
    public static void onMouseEvent(MouseScrollEvent event, ItemStack stack, PlayerEntity player){
        if (event.getScrollDelta() != 0 && player.isSneaking()){
            CompoundNBT nbt = stack.getOrCreateTag();
            if(!nbt.hasUniqueId(IItemHasUUID.NBT_UUID)) return;
            UUID uuid = IItemHasUUID.getUUID(stack);

            int oldReach = nbt.getInt(ItemRodReincarnation.NBT_REACH_DISTANCE);
            int newReach = (int)MathHelper.clamp(oldReach + event.getScrollDelta(), 2, 10);
            nbt.putInt(ItemRodReincarnation.NBT_REACH_DISTANCE, newReach);

            ModPacketHandler.INSTANCE.sendToServer(new CPacketItemUpdateNBT(nbt, uuid, CPacketItemUpdateNBT.Operation.ADD));
            event.setCanceled(true);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void onRightClickTargetBlock(BlockPos blockPos, ItemStack stack, PlayerEntity player){
        CompoundNBT tag = stack.getOrCreateTag();

        if(!hasFilePathNBT(stack)){
            ChatUtils.sendTranslatedChat(player, TextFormatting.WHITE, "fs.message.rodreincarnation.warning.noFileName");
            return;
        }
        if(existsFile(stack)){
            // FILE => WORLD
            if(tag.contains(ItemRodReincarnation.NBT_DATA) && !tag.contains(IItemHasSplitNBTList.NBT_SPLIT)){
                
                CompoundNBT blockPosScheduledNBT = tag.getCompound(ItemRodReincarnation.NBT_POINT_SCHEDULED);
                if(blockPosScheduledNBT.isEmpty()){
                    // saveScheduled
                    tag.put(ItemRodReincarnation.NBT_POINT_SCHEDULED, NBTUtil.writeBlockPos(blockPos));
                    tag.putInt(ItemRodReincarnation.NBT_POINT_SCHEDULED_FACING, player.getHorizontalFacing().ordinal());
                    sendPosNBTTagToServer(stack);
                    return;
                }

                BlockPos blockPosScheduled = NBTUtil.readBlockPos(blockPosScheduledNBT);
                if(blockPos.equals(blockPosScheduled)){
                    // build
                    
                    Direction facingScheduled = getFacingScheduled(stack);
                    if(facingScheduled == null){ // safe code
                        sendRemoveNBTTagToServer(stack, ItemRodReincarnation.NBT_POINT_SCHEDULED);
                        sendRemoveNBTTagToServer(stack, ItemRodReincarnation.NBT_POINT_SCHEDULED_FACING);
                        sendPosNBTTagToServer(stack);
                        return;
                    }
                    
                    Rotation rotation = getRotation(Direction.EAST, facingScheduled);
                    UUID uuid = IItemHasUUID.getUUID(stack);
                    ModPacketHandler.INSTANCE.sendToServer(new CPacketRodReincarnationStartBuilding(CPacketRodReincarnationStartBuilding.Operation.TRUE_BUILD, blockPosScheduled, rotation, uuid));
                }
                else{
                    // saveScheduled
                    tag.put(ItemRodReincarnation.NBT_POINT_SCHEDULED, NBTUtil.writeBlockPos(blockPos));
                    tag.putInt(ItemRodReincarnation.NBT_POINT_SCHEDULED_FACING, player.getHorizontalFacing().ordinal());
                    sendPosNBTTagToServer(stack);
                }
            }
            else{
                // Load
                sendRemoveNBTTagToServer(stack, ItemRodReincarnation.NBT_POINT_SCHEDULED);
                sendRemoveNBTTagToServer(stack, ItemRodReincarnation.NBT_POINT_SCHEDULED_FACING);
                sendRemoveNBTTagToServer(stack, ItemRodReincarnation.NBT_POINT_NEAR);
                sendRemoveNBTTagToServer(stack, ItemRodReincarnation.NBT_POINT_END);
                handleloading(player, stack);
            }
        }
        else{
            // WORLD => FILE
            sendRemoveNBTTagToServer(stack, ItemRodReincarnation.NBT_POINT_SCHEDULED);
            sendRemoveNBTTagToServer(stack, ItemRodReincarnation.NBT_POINT_SCHEDULED_FACING);
            sendRemoveNBTTagToServer(stack, ItemRodReincarnation.NBT_DATA);

            CompoundNBT blockPosNearNBT = tag.getCompound(ItemRodReincarnation.NBT_POINT_NEAR);
            if(blockPosNearNBT.isEmpty()){
                // saveNear
                tag.put(ItemRodReincarnation.NBT_POINT_NEAR, NBTUtil.writeBlockPos(blockPos));
                sendPosNBTTagToServer(stack);
                return;
            }
            
            CompoundNBT blockPosEndNBT = tag.getCompound(ItemRodReincarnation.NBT_POINT_END);
            if(blockPosEndNBT.isEmpty()){
                // saveEnd
                BlockPos blockPosNear = NBTUtil.readBlockPos(blockPosNearNBT);
                int sizeX = Math.abs((blockPos.getX() - blockPosNear.getX()));
                int sizeY = Math.abs((blockPos.getY() - blockPosNear.getY()));
                int sizeZ = Math.abs((blockPos.getZ() - blockPosNear.getZ()));
                int maxLength = ConfigHandler.COMMON.rodReincarnationMaxLength.get();
                if(sizeX > maxLength || sizeY > maxLength || sizeZ > maxLength){
                    ChatUtils.sendTranslatedChat(player, TextFormatting.RED, "fs.message.rodreincarnation.warning.rangeTooLarge", maxLength);
                    return;
                }
                tag.put(ItemRodReincarnation.NBT_POINT_END, NBTUtil.writeBlockPos(blockPos));
                sendPosNBTTagToServer(stack);
                return;
            }

            BlockPos blockPosNear = NBTUtil.readBlockPos(blockPosNearNBT);
            BlockPos blockPosEnd = NBTUtil.readBlockPos(blockPosEndNBT);
            if(blockPos.equals(blockPosNear)){
                // SAVE
                handleSaving(player, blockPosNear, blockPosEnd, stack);
            }
            else{
                ChatUtils.sendTranslatedChat(player, TextFormatting.WHITE, "fs.message.rodreincarnation.warning.clickToSave");
            }
        }
    }

    private static boolean hasFilePathNBT(ItemStack stack){
        return !getFileName(stack).isEmpty();
    }

    private static boolean existsFile(ItemStack stack){
        // get saves directory : "%GAME_DIR%/saves"
        File savesDir = Minecraft.getInstance().getSaveLoader().func_215781_c().toFile();
        String filePath = getFileName(stack);
        File file = new File(savesDir, PATH_REINCARNATION_HOME + filePath);

        try {
            if(!Files.exists(file.toPath().getParent())){
                Files.createDirectories(file.toPath().getParent());
                return false;
            }
            if(!Files.exists(file.toPath())){
                return false;
            }

        } catch (IOException e) {
            ModLogger.warning(e, file.toString());
            return false;
        }

        return true;
    }

    @OnlyIn(Dist.CLIENT)
    public static void onRightClickWithPressShift(ItemStack stack, PlayerEntity player){
        if(getBlockPosScheduled(stack) != null){
            sendRemoveNBTTagToServer(stack, ItemRodReincarnation.NBT_POINT_SCHEDULED);
            sendRemoveNBTTagToServer(stack, ItemRodReincarnation.NBT_POINT_SCHEDULED_FACING);
        }
        else{
            sendRemoveNBTTagToServer(stack, ItemRodReincarnation.NBT_POINT_NEAR);
            sendRemoveNBTTagToServer(stack, ItemRodReincarnation.NBT_POINT_END);
            ChatUtils.sendTranslatedChat(player, TextFormatting.WHITE, "fs.message.rodreincarnation.use.reset");
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

    public static void handleBuilding(PlayerEntity player, BlockPos posDst, Rotation rotation, ItemStack stackRod){
        World world = player.world;
        PlayerInventory inventory = player.inventory;
        int size = inventory.getSizeInventory();


        CompoundNBT tag = stackRod.getTag();
        if(tag == null || !tag.contains(ItemRodReincarnation.NBT_DATA)){
            ChatUtils.sendTranslatedChat(player, TextFormatting.RED, "fs.message.rodreincarnation.build.failed");
            return;
        }
        CompoundNBT tagData = tag.getCompound(ItemRodReincarnation.NBT_DATA);

        BasicStrucure structure = new BasicStrucure(tagData, new ResourceLocation(""));
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
                    BlockPos dst = posDst.add(posRelative);
                    if(!world.isAirBlock(dst)){
                        i++;
                        continue;
                    }
                    int stateNum = stateNums[i];
                    BlockState state = states.get(stateNum);
                    if(state != null){
                        BlockState stateRotated = state.rotate(rotation);
                        Block block = stateRotated.getBlock();
                        ItemStack stackSrc = new ItemStack(block);

                        if(player.isCreative()){
                            world.setBlockState(dst, stateRotated);
                            i++;
                            continue;
                        }
                        for(int j = 0; j < size; j++){
                            ItemStack stack = inventory.getStackInSlot(j);
                            if(stack.equals(stackSrc, false)){
                                stack.shrink(1);
                                world.setBlockState(dst, stateRotated);
                                break;
                            }
                        }
                    }
                    i++;
                }
            }
        }
        world.playSound((PlayerEntity)null, new BlockPos(player.getPositionVector()), ModSoundHandler.itemRodSuccess, SoundCategory.PLAYERS, 1.0f, (float)(1.0f + 0.05f*world.rand.nextGaussian()));

    }

    public static void handleloading(PlayerEntity player, ItemStack stack){
        CompoundNBT tag = loadNBT(stack);
        if(tag.isEmpty()){
            ChatUtils.sendTranslatedChat(player, TextFormatting.RED, "fs.message.rodreincarnation.use.load.failed");
            return;
        }

        CompoundNBT tagStack = stack.getOrCreateTag();
        tagStack.put(ItemRodReincarnation.NBT_DATA, tag);
        sendDataNBTTagToServer(stack);
        tagStack.remove(ItemRodReincarnation.NBT_DATA);
    }

    public static void handleSaving(PlayerEntity player, BlockPos posNear, BlockPos posEnd, ItemStack stack){
        BasicStrucure structure = new BasicStrucure(player.world, posNear, posEnd);

        CompoundNBT nbt = structure.serializeNBT();
        
        if(saveNBT(nbt, stack)){
            ChatUtils.sendTranslatedChat(player, TextFormatting.GREEN, "fs.message.rodreincarnation.use.save.success");
        }
        else{
            ChatUtils.sendTranslatedChat(player, TextFormatting.RED, "fs.message.rodreincarnation.use.save.failed");
        }
    }

    @Nonnull
    public static CompoundNBT loadNBT(ItemStack stack){
        // get saves directory : "%GAME_DIR%/saves"
        File savesDir = Minecraft.getInstance().getSaveLoader().func_215781_c().toFile();
        String filePath = getFileName(stack);
        File file = new File(savesDir, PATH_REINCARNATION_HOME + filePath);

        if(!file.exists()) return new CompoundNBT();

        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            return CompressedStreamTools.readCompressed(fileInputStream);
        } catch (IOException e) {
            ModLogger.warning(e, file.toString());
        }

        return new CompoundNBT();
    }

    public static boolean saveNBT(CompoundNBT tag, ItemStack stack){
        // get saves directory : "%GAME_DIR%/saves"
        File savesDir = Minecraft.getInstance().getSaveLoader().func_215781_c().toFile();
        String filePath = getFileName(stack);
        File file = new File(savesDir, PATH_REINCARNATION_HOME + filePath);

        try {
            if(!file.createNewFile()){
                return false;
            }
        } catch (IOException e) {
            ModLogger.warning(e, file.toString());
        }


        try (FileOutputStream fileoutputstream = new FileOutputStream(file)) {
            CompressedStreamTools.writeCompressed(tag, fileoutputstream);
            return true;
        } catch (IOException e) {
            ModLogger.warning(e, file.toString());
        }

        return false;
    }

    public static void setFileName(ItemStack stack, String filename, @Nullable PlayerEntity player){
        if(filename.contains(" ") || filename.length() > 16){
            if(player != null){
                ChatUtils.sendTranslatedChat(player, TextFormatting.RED, "fs.message.rodreincarnation.warning.invalidFileName");
            }
            return;
        }
        CompoundNBT tag = stack.getOrCreateTag();
        tag.putString(ItemRodReincarnation.NBT_FILE, filename);

        tag.remove(ItemRodReincarnation.NBT_DATA);
        tag.remove(ItemRodReincarnation.NBT_POINT_NEAR);
        tag.remove(ItemRodReincarnation.NBT_POINT_END);
        tag.remove(ItemRodReincarnation.NBT_POINT_SCHEDULED);
        tag.remove(ItemRodReincarnation.NBT_POINT_SCHEDULED_FACING);
    }

    @Nullable
    public static String getFileName(ItemStack stack){
        CompoundNBT tag = stack.getOrCreateTag();
        return tag.getString(ItemRodReincarnation.NBT_FILE);
    }

    @Nullable
    public static BlockPos getBlockPosNear(ItemStack stack){
        CompoundNBT tag = stack.getOrCreateTag();
        CompoundNBT blockPosNearNBT = tag.getCompound(ItemRodReincarnation.NBT_POINT_NEAR);
        if(!blockPosNearNBT.isEmpty()){
            return NBTUtil.readBlockPos(blockPosNearNBT);
        }
        return null;
    }

    @Nullable
    public static BlockPos getBlockPosEnd(ItemStack stack){
        CompoundNBT tag = stack.getOrCreateTag();
        CompoundNBT blockPosEndNBT = tag.getCompound(ItemRodReincarnation.NBT_POINT_END);
        if(!blockPosEndNBT.isEmpty()){
            return NBTUtil.readBlockPos(blockPosEndNBT);
        }
        return null;
    }

    @Nullable
    public static BlockPos getBlockPosScheduled(ItemStack stack){
        CompoundNBT tag = stack.getOrCreateTag();
        CompoundNBT blockPosScheduledNBT = tag.getCompound(ItemRodReincarnation.NBT_POINT_SCHEDULED);
        if(!blockPosScheduledNBT.isEmpty()){
            return NBTUtil.readBlockPos(blockPosScheduledNBT);
        }
        return null;
    }

    @Nullable
    public static Direction getFacingScheduled(ItemStack stack){
        CompoundNBT tag = stack.getOrCreateTag();
        return Direction.values()[tag.getInt(ItemRodReincarnation.NBT_POINT_SCHEDULED_FACING)];
    }

    @Nullable
    public static BlockPos getBlockPosData(ItemStack stack){
        CompoundNBT tag = stack.getOrCreateTag();
        CompoundNBT tagData = tag.getCompound(ItemRodReincarnation.NBT_DATA);
        if(tagData.isEmpty()) return null;
        return new BlockPos(
            tagData.getInt(BasicStrucure.NBT_DATA_SIZE_X) - 1,
            tagData.getInt(BasicStrucure.NBT_DATA_SIZE_Y) - 1,
            tagData.getInt(BasicStrucure.NBT_DATA_SIZE_Z) - 1);
    }

    @Nonnull
    public static AxisAlignedBB getScheduledAABB(BlockPos posRelative, Direction facingDst, BlockPos posDst){
        Direction facingSource = Direction.EAST;
        BlockPos posDiff = posRelative;
        Rotation rot = getRotation(facingSource, facingDst);
        BlockPos posRotated = posDiff.rotate(rot);

        return new AxisAlignedBB(posDst, posDst.add(posRotated)).expand(1, 1, 1);
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

        int distance = tag.getInt(ItemRodReincarnation.NBT_REACH_DISTANCE);
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
                //Todo: 校正が必要
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
    public static void sendDataNBTTagToServer(ItemStack stack){
        CompoundNBT nbt = stack.getTag();
        if(nbt == null || !nbt.hasUniqueId(IItemHasUUID.NBT_UUID)) return;
        UUID uuid = IItemHasUUID.getUUID(stack);

        CompoundNBT nbtMerge = new CompoundNBT();
        CompoundNBT nbtData = nbt.getCompound(ItemRodReincarnation.NBT_DATA);
        ListNBT nbtStateNums = nbtData.getList(BasicStrucure.NBT_DATA_STATE_NUMS, 3);
        nbtData.remove(BasicStrucure.NBT_DATA_STATE_NUMS);
        nbtMerge.put(ItemRodReincarnation.NBT_DATA, nbtData);

        // data exclude stateNum
        ModPacketHandler.INSTANCE.sendToServer(new CPacketItemUpdateNBT(nbtMerge, uuid, CPacketItemUpdateNBT.Operation.ADD));
        // split stateNum
        int count = nbtStateNums.size();
        int chunk = 2048;
        int split = MathHelper.ceil((float)count/chunk);
        UUID uuidSplitData = UUID.randomUUID();
        for(int i = 0; i < split; i++){
            ListNBT nbtSplitList = new ListNBT();
            for(int n = 0; n < chunk; n++){
                int idx = i*chunk + n;
                if(idx >= count) break;

                nbtSplitList.add(new IntNBT(nbtStateNums.getInt(idx)));
            }
            ModPacketHandler.INSTANCE.sendToServer(new CPacketItemUpdateSplitNBTList(nbtSplitList, uuid, i, split, uuidSplitData));
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void sendPosNBTTagToServer(ItemStack stack){
        CompoundNBT nbt = stack.getTag();
        if(nbt == null || !nbt.hasUniqueId(IItemHasUUID.NBT_UUID)) return;
        UUID uuid = IItemHasUUID.getUUID(stack);

        CompoundNBT nbtMerge = new CompoundNBT();
        copyTagCompound(nbtMerge, nbt, ItemRodReincarnation.NBT_POINT_NEAR);
        copyTagCompound(nbtMerge, nbt, ItemRodReincarnation.NBT_POINT_END);
        copyTagCompound(nbtMerge, nbt, ItemRodReincarnation.NBT_POINT_SCHEDULED);
        copyTagInt(nbtMerge, nbt, ItemRodReincarnation.NBT_POINT_SCHEDULED_FACING);
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