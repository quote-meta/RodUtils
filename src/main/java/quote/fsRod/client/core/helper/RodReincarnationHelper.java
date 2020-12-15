package quote.fsRod.client.core.helper;

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
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import quote.fsRod.common.RodUtils;
import quote.fsRod.common.core.handler.ConfigHandler;
import quote.fsRod.common.core.handler.ModSoundHandler;
import quote.fsRod.common.core.network.ModPacketHandler;
import quote.fsRod.common.core.network.item.CPacketItemUpdateNBT;
import quote.fsRod.common.core.network.item.CPacketItemUpdateSplitNBTList;
import quote.fsRod.common.core.network.item.CPacketRodReincarnationStartBuilding;
import quote.fsRod.common.core.utils.ChatUtils;
import quote.fsRod.common.core.utils.ModLogger;
import quote.fsRod.common.core.utils.ModUtils;
import quote.fsRod.common.item.rod.ItemRodReincarnation;
import quote.fsRod.common.item.utils.IItemHasSplitNBTList;
import quote.fsRod.common.item.utils.IItemHasUUID;
import quote.fsRod.common.structure.BasicStrucure;

public class RodReincarnationHelper {

    public static final String PATH_REINCARNATION_HOME = "share_data/fancifulspecters/reincarnation/";

    private RodReincarnationHelper(){}

    @SideOnly(Side.CLIENT)
    public static void onMouseEvent(MouseEvent event, ItemStack stack, EntityPlayer player){
        if (event.getDwheel() != 0 && player.isSneaking()){
            NBTTagCompound nbt = ModUtils.getTagThoughAbsent(stack);
            if(!nbt.hasUniqueId(IItemHasUUID.NBT_UUID)) return;
            UUID uuid = IItemHasUUID.getUUID(stack);

            int oldReach = nbt.getInteger(ItemRodReincarnation.NBT_REACH_DISTANCE);
            int newReach = MathHelper.clamp(oldReach + event.getDwheel() / 120, 2, 10);
            nbt.setInteger(ItemRodReincarnation.NBT_REACH_DISTANCE, newReach);

            ModPacketHandler.INSTANCE.sendToServer(new CPacketItemUpdateNBT(nbt, uuid, CPacketItemUpdateNBT.Operation.ADD));
            event.setCanceled(true);
        }
    }

    @SideOnly(Side.CLIENT)
    public static void onRightClickTargetBlock(BlockPos blockPos, ItemStack stack, EntityPlayer player){
        NBTTagCompound tag = ModUtils.getTagThoughAbsent(stack);

        if(!hasFilePathNBT(stack)){
            ChatUtils.sendTranslatedChat(player, TextFormatting.WHITE, "fs.message.rodReincarnation.warning.noFileName");
            return;
        }
        if(existsFile(stack)){
            // FILE => WORLD
            if(tag.hasKey(ItemRodReincarnation.NBT_DATA) && !tag.hasKey(IItemHasSplitNBTList.NBT_SPLIT)){
                
                NBTTagCompound blockPosScheduledNBT = tag.getCompoundTag(ItemRodReincarnation.NBT_POINT_SCHEDULED);
                if(blockPosScheduledNBT.hasNoTags()){
                    // saveScheduled
                    tag.setTag(ItemRodReincarnation.NBT_POINT_SCHEDULED, NBTUtil.createPosTag(blockPos));
                    tag.setInteger(ItemRodReincarnation.NBT_POINT_SCHEDULED_FACING, player.getHorizontalFacing().ordinal());
                    sendPosNBTTagToServer(stack);
                    return;
                }

                BlockPos blockPosScheduled = NBTUtil.getPosFromTag(blockPosScheduledNBT);
                if(blockPos.equals(blockPosScheduled)){
                    // build
                    
                    EnumFacing facingScheduled = getFacingScheduled(stack);
                    if(facingScheduled == null){ // safe code
                        sendRemoveNBTTagToServer(stack, ItemRodReincarnation.NBT_POINT_SCHEDULED);
                        sendRemoveNBTTagToServer(stack, ItemRodReincarnation.NBT_POINT_SCHEDULED_FACING);
                        sendPosNBTTagToServer(stack);
                        return;
                    }
                    
                    Rotation rotation = getRotation(EnumFacing.EAST, facingScheduled);
                    UUID uuid = IItemHasUUID.getUUID(stack);
                    ModPacketHandler.INSTANCE.sendToServer(new CPacketRodReincarnationStartBuilding(CPacketRodReincarnationStartBuilding.Operation.TRUE_BUILD, blockPosScheduled, rotation, uuid));
                }
                else{
                    // saveScheduled
                    tag.setTag(ItemRodReincarnation.NBT_POINT_SCHEDULED, NBTUtil.createPosTag(blockPos));
                    tag.setInteger(ItemRodReincarnation.NBT_POINT_SCHEDULED_FACING, player.getHorizontalFacing().ordinal());
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

            NBTTagCompound blockPosNearNBT = tag.getCompoundTag(ItemRodReincarnation.NBT_POINT_NEAR);
            if(blockPosNearNBT.hasNoTags()){
                // saveNear
                tag.setTag(ItemRodReincarnation.NBT_POINT_NEAR, NBTUtil.createPosTag(blockPos));
                sendPosNBTTagToServer(stack);
                return;
            }
            
            NBTTagCompound blockPosEndNBT = tag.getCompoundTag(ItemRodReincarnation.NBT_POINT_END);
            if(blockPosEndNBT.hasNoTags()){
                // saveEnd
                BlockPos blockPosNear = NBTUtil.getPosFromTag(blockPosNearNBT);
                int sizeX = Math.abs((blockPos.getX() - blockPosNear.getX()));
                int sizeY = Math.abs((blockPos.getY() - blockPosNear.getY()));
                int sizeZ = Math.abs((blockPos.getZ() - blockPosNear.getZ()));
                if(sizeX > ConfigHandler.rodReincarnationMaxLength || sizeY > ConfigHandler.rodReincarnationMaxLength || sizeZ > ConfigHandler.rodReincarnationMaxLength){
                    ChatUtils.sendTranslatedChat(player, TextFormatting.RED, "fs.message.rodReincarnation.warning.rangeTooLarge", ConfigHandler.rodReincarnationMaxLength);
                    return;
                }
                tag.setTag(ItemRodReincarnation.NBT_POINT_END, NBTUtil.createPosTag(blockPos));
                sendPosNBTTagToServer(stack);
                return;
            }

            BlockPos blockPosNear = NBTUtil.getPosFromTag(blockPosNearNBT);
            BlockPos blockPosEnd = NBTUtil.getPosFromTag(blockPosEndNBT);
            if(blockPos.equals(blockPosNear)){
                // SAVE
                handleSaving(player, blockPosNear, blockPosEnd, stack);
            }
            else{
                ChatUtils.sendTranslatedChat(player, TextFormatting.WHITE, "fs.message.rodReincarnation.warning.clickToSave");
            }
        }
    }

    private static boolean hasFilePathNBT(ItemStack stack){
        return !getFileName(stack).isEmpty();
    }

    private static boolean existsFile(ItemStack stack){
        File savesDir = FMLClientHandler.instance().getSavesDir();
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

    @SideOnly(Side.CLIENT)
    public static void onRightClickWithPressShift(ItemStack stack, EntityPlayer player){
        if(getBlockPosScheduled(stack) != null){
            sendRemoveNBTTagToServer(stack, ItemRodReincarnation.NBT_POINT_SCHEDULED);
            sendRemoveNBTTagToServer(stack, ItemRodReincarnation.NBT_POINT_SCHEDULED_FACING);
        }
        else{
            sendRemoveNBTTagToServer(stack, ItemRodReincarnation.NBT_POINT_NEAR);
            sendRemoveNBTTagToServer(stack, ItemRodReincarnation.NBT_POINT_END);
            ChatUtils.sendTranslatedChat(player, TextFormatting.WHITE, "fs.message.rodReincarnation.use.reset");
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

    public static void handleBuilding(EntityPlayer player, BlockPos posDst, Rotation rotation, ItemStack stackRod){
        World world = player.world;
        InventoryPlayer inventory = player.inventory;
        int size = inventory.getSizeInventory();


        NBTTagCompound tag = stackRod.getTagCompound();
        if(tag == null || !tag.hasKey(ItemRodReincarnation.NBT_DATA)){
            ChatUtils.sendTranslatedChat(player, TextFormatting.RED, "fs.message.rodReincarnation.build.failed");
            return;
        }
        NBTTagCompound tagData = tag.getCompoundTag(ItemRodReincarnation.NBT_DATA);

        BasicStrucure structure = new BasicStrucure(tagData, new ResourceLocation(""));
        int sizeX = structure.getSizeX();
        int sizeY = structure.getSizeY();
        int sizeZ = structure.getSizeZ();
        List<IBlockState> states = structure.getStates();
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
                    IBlockState state = states.get(stateNum);
                    if(state != null){
                        IBlockState stateRotated = state.withRotation(rotation);
                        Block block = stateRotated.getBlock();
                        Item item = Item.getItemFromBlock(block);
                        int damage = block.damageDropped(stateRotated);

                        if(player.isCreative()){
                            world.setBlockState(dst, stateRotated);
                            i++;
                            continue;
                        }
                        for(int j = 0; j < size; j++){
                            ItemStack stack = inventory.getStackInSlot(j);
                            if(stack.getItem() == item && stack.getItemDamage() == damage){
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
        world.playSound((EntityPlayer)null, new BlockPos(player.getPositionVector()), ModSoundHandler.itemRodSuccess, SoundCategory.PLAYERS, 1.0f, (float)(1.0f + 0.05f*world.rand.nextGaussian()));

    }

    public static void handleloading(EntityPlayer player, ItemStack stack){
        NBTTagCompound tag = loadNBT(stack);
        if(tag.hasNoTags()){
            ChatUtils.sendTranslatedChat(player, TextFormatting.RED, "fs.message.rodReincarnation.use.load.failed");
            return;
        }

        NBTTagCompound tagStack = ModUtils.getTagThoughAbsent(stack);
        tagStack.setTag(ItemRodReincarnation.NBT_DATA, tag);
        sendDataNBTTagToServer(stack);
        tagStack.removeTag(ItemRodReincarnation.NBT_DATA);
    }

    public static void handleSaving(EntityPlayer player, BlockPos posNear, BlockPos posEnd, ItemStack stack){
        BasicStrucure structure = new BasicStrucure(player.world, posNear, posEnd);

        NBTTagCompound nbt = structure.serializeNBT();
        
        if(saveNBT(nbt, stack)){
            ChatUtils.sendTranslatedChat(player, TextFormatting.GREEN, "fs.message.rodReincarnation.use.save.success");
        }
        else{
            ChatUtils.sendTranslatedChat(player, TextFormatting.RED, "fs.message.rodReincarnation.use.save.failed");
        }
    }

    @Nonnull
    public static NBTTagCompound loadNBT(ItemStack stack){
        File savesDir = FMLClientHandler.instance().getSavesDir();
        String filePath = getFileName(stack);
        File file = new File(savesDir, PATH_REINCARNATION_HOME + filePath);

        if(!file.exists()) return new NBTTagCompound();

        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            return CompressedStreamTools.readCompressed(fileInputStream);
        } catch (IOException e) {
            ModLogger.warning(e, file.toString());
        }

        return new NBTTagCompound();
    }

    public static boolean saveNBT(NBTTagCompound tag, ItemStack stack){
        File savesDir = FMLClientHandler.instance().getSavesDir();
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

    public static void setFileName(ItemStack stack, String filename, @Nullable EntityPlayer player){
        if(filename.contains(" ") || filename.length() > 16){
            if(player != null){
                ChatUtils.sendTranslatedChat(player, TextFormatting.RED, "fs.message.rodReincarnation.warning.invalidFileName");
            }
            return;
        }
        NBTTagCompound tag = ModUtils.getTagThoughAbsent(stack);
        tag.setString(ItemRodReincarnation.NBT_FILE, filename);

        tag.removeTag(ItemRodReincarnation.NBT_DATA);
        tag.removeTag(ItemRodReincarnation.NBT_POINT_NEAR);
        tag.removeTag(ItemRodReincarnation.NBT_POINT_END);
        tag.removeTag(ItemRodReincarnation.NBT_POINT_SCHEDULED);
        tag.removeTag(ItemRodReincarnation.NBT_POINT_SCHEDULED_FACING);
    }

    @Nullable
    public static String getFileName(ItemStack stack){
        NBTTagCompound tag = ModUtils.getTagThoughAbsent(stack);
        return tag.getString(ItemRodReincarnation.NBT_FILE);
    }

    @Nullable
    public static BlockPos getBlockPosNear(ItemStack stack){
        NBTTagCompound tag = ModUtils.getTagThoughAbsent(stack);
        NBTTagCompound blockPosNearNBT = tag.getCompoundTag(ItemRodReincarnation.NBT_POINT_NEAR);
        if(!blockPosNearNBT.hasNoTags()){
            return NBTUtil.getPosFromTag(blockPosNearNBT);
        }
        return null;
    }

    @Nullable
    public static BlockPos getBlockPosEnd(ItemStack stack){
        NBTTagCompound tag = ModUtils.getTagThoughAbsent(stack);
        NBTTagCompound blockPosEndNBT = tag.getCompoundTag(ItemRodReincarnation.NBT_POINT_END);
        if(!blockPosEndNBT.hasNoTags()){
            return NBTUtil.getPosFromTag(blockPosEndNBT);
        }
        return null;
    }

    @Nullable
    public static BlockPos getBlockPosScheduled(ItemStack stack){
        NBTTagCompound tag = ModUtils.getTagThoughAbsent(stack);
        NBTTagCompound blockPosScheduledNBT = tag.getCompoundTag(ItemRodReincarnation.NBT_POINT_SCHEDULED);
        if(!blockPosScheduledNBT.hasNoTags()){
            return NBTUtil.getPosFromTag(blockPosScheduledNBT);
        }
        return null;
    }

    @Nullable
    public static EnumFacing getFacingScheduled(ItemStack stack){
        NBTTagCompound tag = ModUtils.getTagThoughAbsent(stack);
        return EnumFacing.values()[tag.getInteger(ItemRodReincarnation.NBT_POINT_SCHEDULED_FACING)];
    }

    @Nullable
    public static BlockPos getBlockPosData(ItemStack stack){
        NBTTagCompound tag = ModUtils.getTagThoughAbsent(stack);
        NBTTagCompound tagData = tag.getCompoundTag(ItemRodReincarnation.NBT_DATA);
        if(tagData.hasNoTags()) return null;
        return new BlockPos(
            tagData.getInteger(BasicStrucure.NBT_DATA_SIZE_X) - 1,
            tagData.getInteger(BasicStrucure.NBT_DATA_SIZE_Y) - 1,
            tagData.getInteger(BasicStrucure.NBT_DATA_SIZE_Z) - 1);
    }

    @Nonnull
    public static AxisAlignedBB getScheduledAABB(BlockPos posRelative, EnumFacing facingDst, BlockPos posDst){
        EnumFacing facingSource = EnumFacing.EAST;
        BlockPos posDiff = posRelative;
        Rotation rot = getRotation(facingSource, facingDst);
        BlockPos posRotated = posDiff.rotate(rot);

        return new AxisAlignedBB(posDst, posDst.add(posRotated)).expand(1, 1, 1);
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

        int distance = tag.getInteger(ItemRodReincarnation.NBT_REACH_DISTANCE);
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
    public static void sendDataNBTTagToServer(ItemStack stack){
        NBTTagCompound nbt = stack.getTagCompound();
        if(nbt == null || !nbt.hasUniqueId(IItemHasUUID.NBT_UUID)) return;
        UUID uuid = IItemHasUUID.getUUID(stack);

        NBTTagCompound nbtMerge = new NBTTagCompound();
        NBTTagCompound nbtData = nbt.getCompoundTag(ItemRodReincarnation.NBT_DATA);
        NBTTagList nbtStateNums = nbtData.getTagList(BasicStrucure.NBT_DATA_STATE_NUMS, 3);
        nbtData.removeTag(BasicStrucure.NBT_DATA_STATE_NUMS);
        nbtMerge.setTag(ItemRodReincarnation.NBT_DATA, nbtData);

        // data exclude stateNum
        ModPacketHandler.INSTANCE.sendToServer(new CPacketItemUpdateNBT(nbtMerge, uuid, CPacketItemUpdateNBT.Operation.ADD));
        // split stateNum
        int count = nbtStateNums.tagCount();
        int chunk = 2048;
        int split = MathHelper.ceil((float)count/chunk);
        UUID uuidSplitData = UUID.randomUUID();
        for(int i = 0; i < split; i++){
            NBTTagList nbtSplitList = new NBTTagList();
            for(int n = 0; n < chunk; n++){
                int idx = i*chunk + n;
                if(idx >= count) break;

                nbtSplitList.appendTag(new NBTTagInt(nbtStateNums.getIntAt(idx)));
            }
            ModPacketHandler.INSTANCE.sendToServer(new CPacketItemUpdateSplitNBTList(nbtSplitList, uuid, i, split, uuidSplitData));
        }
    }

    @SideOnly(Side.CLIENT)
    public static void sendPosNBTTagToServer(ItemStack stack){
        NBTTagCompound nbt = stack.getTagCompound();
        if(nbt == null || !nbt.hasUniqueId(IItemHasUUID.NBT_UUID)) return;
        UUID uuid = IItemHasUUID.getUUID(stack);

        NBTTagCompound nbtMerge = new NBTTagCompound();
        copyTagCompound(nbtMerge, nbt, ItemRodReincarnation.NBT_POINT_NEAR);
        copyTagCompound(nbtMerge, nbt, ItemRodReincarnation.NBT_POINT_END);
        copyTagCompound(nbtMerge, nbt, ItemRodReincarnation.NBT_POINT_SCHEDULED);
        copyTagInt(nbtMerge, nbt, ItemRodReincarnation.NBT_POINT_SCHEDULED_FACING);
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