package quote.fsrod.common.core.network.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import quote.fsrod.common.core.utils.ModUtils;
import quote.fsrod.common.item.utils.IItemHasSplitNBTList;
import quote.fsrod.common.item.utils.IItemHasUUID;

// HACK: サーバとのアイテムの同期がずれると危険かもしれない
public class CPacketItemUpdateSplitNBTList implements IMessage {

    private NBTTagCompound nbtPart;
    private UUID uuid;
    private int max;
    private int tagType;
    private UUID uuidSplit;


    public CPacketItemUpdateSplitNBTList() {
    }

    public CPacketItemUpdateSplitNBTList(NBTTagList nbtList, @Nonnull UUID uuidItem, int idx, int max, @Nonnull UUID uuidSplitNBTList) {
        this.nbtPart = new NBTTagCompound();
        nbtPart.setTag(IItemHasSplitNBTList.NBT_SPLIT_PART_LIST, nbtList);
        nbtPart.setInteger(IItemHasSplitNBTList.NBT_SPLIT_PART_INDEX, idx);

        this.uuid = uuidItem;
        this.max = max;
        this.tagType = nbtList.getTagType();
        this.uuidSplit = uuidSplitNBTList;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        nbtPart = ByteBufUtils.readTag(buf);
        uuid = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        max = buf.readInt();
        tagType = buf.readInt();
        uuidSplit = UUID.fromString(ByteBufUtils.readUTF8String(buf));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, nbtPart);
        ByteBufUtils.writeUTF8String(buf, uuid.toString());
        buf.writeInt(max);
        buf.writeInt(tagType);
        ByteBufUtils.writeUTF8String(buf, uuidSplit.toString());
    }

    public static class Handler implements IMessageHandler<CPacketItemUpdateSplitNBTList, IMessage> {

        @Override
        public IMessage onMessage(CPacketItemUpdateSplitNBTList message, MessageContext ctx) {
            if (ctx.side.isClient()) return null;
            MinecraftServer ms = FMLCommonHandler.instance().getMinecraftServerInstance();
            ms.addScheduledTask(() -> {
                EntityPlayer player = ctx.getServerHandler().player;
                int size = player.inventory.getSizeInventory();
                for(int i = 0; i < size; i++){
                    ItemStack stack = player.inventory.getStackInSlot(i);
                    if(stack.getItem() instanceof IItemHasSplitNBTList){
                        UUID uuidInventory = IItemHasUUID.getUUID(stack);
                        if(message.uuid.equals(uuidInventory)){
                            handleSplitNBTList(player, stack, message.nbtPart, message.max, message.tagType, message.uuidSplit);
                            return;
                        }
                    }
                }
            }
            );
            return null;
        }

        private void handleSplitNBTList(EntityPlayer player, ItemStack stack, NBTTagCompound nbtPart, int max, int tagType, UUID uuidSplit){
            //get
            NBTTagCompound nbt = ModUtils.getTagThoughAbsent(stack);
            NBTTagCompound nbtSplit = nbt.getCompoundTag(IItemHasSplitNBTList.NBT_SPLIT);
            UUID uuidSplitStack = nbtSplit.getUniqueId(IItemHasSplitNBTList.NBT_SPLIT_UUID);
            int maxSplitStack = nbtSplit.getInteger(IItemHasSplitNBTList.NBT_SPLIT_MAX);
            int tagTypeStack = nbtSplit.getInteger(IItemHasSplitNBTList.NBT_SPLIT_TAG_TYPE);
            NBTTagList nbtSplitParts = nbtSplit.getTagList(IItemHasSplitNBTList.NBT_SPLIT_PARTS, 10);

            //check same
            if(!uuidSplitStack.equals(uuidSplit) || maxSplitStack != max || tagTypeStack != tagType){
                //reset
                nbtSplit = new NBTTagCompound();
                nbtSplitParts = new NBTTagList();
                uuidSplitStack = uuidSplit;
                maxSplitStack = max;
                tagTypeStack = tagType;

                nbtSplit.setUniqueId(IItemHasSplitNBTList.NBT_SPLIT_UUID, uuidSplitStack);
                nbtSplit.setInteger(IItemHasSplitNBTList.NBT_SPLIT_MAX, maxSplitStack);
                nbtSplit.setInteger(IItemHasSplitNBTList.NBT_SPLIT_TAG_TYPE, tagTypeStack);
                nbtSplit.setTag(IItemHasSplitNBTList.NBT_SPLIT_PARTS, nbtSplitParts);

                ((IItemHasSplitNBTList)stack.getItem()).onResetSplitList(player, stack);
            }

            //append
            nbtSplitParts.appendTag(nbtPart);

            //set
            nbt.setTag(IItemHasSplitNBTList.NBT_SPLIT, nbtSplit);
            
            //check complete
            if(nbtSplitParts.tagCount() == maxSplitStack){
                //sort
                List<NBTTagCompound> completedSplitParts = new ArrayList<>();
                for (NBTBase nbtSplitPart : nbtSplitParts) {
                    if(nbtSplitPart instanceof NBTTagCompound){
                        completedSplitParts.add((NBTTagCompound)nbtSplitPart);
                    }
                }
                Collections.sort(
                    completedSplitParts,
                    (nbt1, nbt2) -> nbt1.getInteger(IItemHasSplitNBTList.NBT_SPLIT_PART_INDEX) - nbt2.getInteger(IItemHasSplitNBTList.NBT_SPLIT_PART_INDEX)
                );

                //check
                if(completedSplitParts.size() == maxSplitStack){
                    //merge
                    NBTTagList nbtListMerged = new NBTTagList();
                    
                    for (NBTTagCompound nbtSplitPart : completedSplitParts) {
                        NBTTagList nbtList = nbtSplitPart.getTagList(IItemHasSplitNBTList.NBT_SPLIT_PART_LIST, tagTypeStack);
                        for (NBTBase nbtBase : nbtList) {
                            nbtListMerged.appendTag(nbtBase);
                        }
                    }
                    //hook
                    ((IItemHasSplitNBTList)stack.getItem()).onCompleteMergingSplitList(player, stack, nbtListMerged);
                }
                nbt.removeTag(IItemHasSplitNBTList.NBT_SPLIT);
                if(nbt.hasNoTags()){
                    stack.setTagCompound(null);
                }
            }
        }
    }

    public enum Operation{
        SET_FAKE,
        TRUE_BUILD
    }
}