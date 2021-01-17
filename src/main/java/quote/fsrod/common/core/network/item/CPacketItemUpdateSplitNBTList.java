package quote.fsrod.common.core.network.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import quote.fsrod.common.item.utils.IItemHasSplitNBTList;
import quote.fsrod.common.item.utils.IItemHasUUID;

// HACK: サーバとのアイテムの同期がずれると危険かもしれない
public class CPacketItemUpdateSplitNBTList {

    private CompoundNBT nbtPart;
    private UUID uuid;
    private int max;
    private int tagType;
    private UUID uuidSplit;


    public CPacketItemUpdateSplitNBTList(CompoundNBT nbtPart, @Nonnull UUID uuidItem, int max, int tagType, @Nonnull UUID uuidSplitNBTList) {
        this.nbtPart = nbtPart;
        this.uuid = uuidItem;
        this.max = max;
        this.tagType = tagType;
        this.uuidSplit = uuidSplitNBTList;
    }

    public CPacketItemUpdateSplitNBTList(ListNBT nbtList, @Nonnull UUID uuidItem, int idx, int max, @Nonnull UUID uuidSplitNBTList) {
        this.nbtPart = new CompoundNBT();
        nbtPart.put(IItemHasSplitNBTList.NBT_SPLIT_PART_LIST, nbtList);
        nbtPart.putInt(IItemHasSplitNBTList.NBT_SPLIT_PART_INDEX, idx);

        this.uuid = uuidItem;
        this.max = max;
        this.tagType = nbtList.getTagType();
        this.uuidSplit = uuidSplitNBTList;
    }

    public static CPacketItemUpdateSplitNBTList decode(PacketBuffer buf) {
        return new CPacketItemUpdateSplitNBTList(
            buf.readCompoundTag(),
            buf.readUniqueId(),
            buf.readInt(),
            buf.readInt(),
            buf.readUniqueId()
        );
    }

    public static void encode(CPacketItemUpdateSplitNBTList msg, PacketBuffer buf) {
        buf.writeCompoundTag(msg.nbtPart);
        buf.writeUniqueId(msg.uuid);
        buf.writeInt(msg.max);
        buf.writeInt(msg.tagType);
        buf.writeUniqueId(msg.uuidSplit);
    }

    public static void handle(CPacketItemUpdateSplitNBTList message, Supplier<NetworkEvent.Context> ctx){
        if (ctx.get().getDirection().getReceptionSide().isServer()){
            ctx.get().enqueueWork(() ->{
                PlayerEntity player = ctx.get().getSender();
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
            });
            ctx.get().setPacketHandled(true);
        }
    }

    private static void handleSplitNBTList(PlayerEntity player, ItemStack stack, CompoundNBT nbtPart, int max, int tagType, UUID uuidSplit){
        //get
        CompoundNBT nbt = stack.getOrCreateTag();
        CompoundNBT nbtSplit = nbt.getCompound(IItemHasSplitNBTList.NBT_SPLIT);
        UUID uuidSplitStack = nbtSplit.getUniqueId(IItemHasSplitNBTList.NBT_SPLIT_UUID);
        int maxSplitStack = nbtSplit.getInt(IItemHasSplitNBTList.NBT_SPLIT_MAX);
        int tagTypeStack = nbtSplit.getInt(IItemHasSplitNBTList.NBT_SPLIT_TAG_TYPE);
        ListNBT nbtSplitParts = nbtSplit.getList(IItemHasSplitNBTList.NBT_SPLIT_PARTS, 10);

        //check same
        if(!uuidSplitStack.equals(uuidSplit) || maxSplitStack != max || tagTypeStack != tagType){
            //reset
            nbtSplit = new CompoundNBT();
            nbtSplitParts = new ListNBT();
            uuidSplitStack = uuidSplit;
            maxSplitStack = max;
            tagTypeStack = tagType;

            nbtSplit.putUniqueId(IItemHasSplitNBTList.NBT_SPLIT_UUID, uuidSplitStack);
            nbtSplit.putInt(IItemHasSplitNBTList.NBT_SPLIT_MAX, maxSplitStack);
            nbtSplit.putInt(IItemHasSplitNBTList.NBT_SPLIT_TAG_TYPE, tagTypeStack);
            nbtSplit.put(IItemHasSplitNBTList.NBT_SPLIT_PARTS, nbtSplitParts);

            ((IItemHasSplitNBTList)stack.getItem()).onResetSplitList(player, stack);
        }

        //append
        nbtSplitParts.add(nbtPart);

        //set
        nbt.put(IItemHasSplitNBTList.NBT_SPLIT, nbtSplit);
        
        //check complete
        if(nbtSplitParts.size() == maxSplitStack){
            //sort
            List<CompoundNBT> completedSplitParts = new ArrayList<>();
            for (INBT nbtSplitPart : nbtSplitParts) {
                if(nbtSplitPart instanceof CompoundNBT){
                    completedSplitParts.add((CompoundNBT)nbtSplitPart);
                }
            }
            Collections.sort(
                completedSplitParts,
                (nbt1, nbt2) -> nbt1.getInt(IItemHasSplitNBTList.NBT_SPLIT_PART_INDEX) - nbt2.getInt(IItemHasSplitNBTList.NBT_SPLIT_PART_INDEX)
            );

            //check
            if(completedSplitParts.size() == maxSplitStack){
                //merge
                ListNBT nbtListMerged = new ListNBT();
                
                for (CompoundNBT nbtSplitPart : completedSplitParts) {
                    ListNBT nbtList = nbtSplitPart.getList(IItemHasSplitNBTList.NBT_SPLIT_PART_LIST, tagTypeStack);
                    for (INBT nbtBase : nbtList) {
                        nbtListMerged.add(nbtBase);
                    }
                }
                //hook
                ((IItemHasSplitNBTList)stack.getItem()).onCompleteMergingSplitList(player, stack, nbtListMerged);
            }
            nbt.remove(IItemHasSplitNBTList.NBT_SPLIT);
            if(nbt.isEmpty()){
                stack.setTag(null);
            }
        }
    }

    public enum Operation{
        SET_FAKE,
        TRUE_BUILD
    }
}