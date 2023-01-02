package quote.fsrod.common.core.network.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import quote.fsrod.common.item.utils.IItemHasSplitTagList;
import quote.fsrod.common.item.utils.IItemHasUUID;

// HACK: サーバとのアイテムの同期がずれると危険かもしれない
public class CPacketItemUpdateSplitTagList {

    private CompoundTag tagPart;
    private UUID uuid;
    private int max;
    private int tagType;
    private UUID uuidSplit;


    public CPacketItemUpdateSplitTagList(CompoundTag tagPart, @Nonnull UUID uuidItem, int max, int tagType, @Nonnull UUID uuidSplitTagList) {
        this.tagPart = tagPart;
        this.uuid = uuidItem;
        this.max = max;
        this.tagType = tagType;
        this.uuidSplit = uuidSplitTagList;
    }

    public CPacketItemUpdateSplitTagList(ListTag tagList, @Nonnull UUID uuidItem, int idx, int max, @Nonnull UUID uuidSplitTagList) {
        this.tagPart = new CompoundTag();
        tagPart.put(IItemHasSplitTagList.TAG_SPLIT_PART_LIST, tagList);
        tagPart.putInt(IItemHasSplitTagList.TAG_SPLIT_PART_INDEX, idx);

        this.uuid = uuidItem;
        this.max = max;
        this.tagType = tagList.getElementType();
        this.uuidSplit = uuidSplitTagList;
    }

    public CPacketItemUpdateSplitTagList(FriendlyByteBuf buf) {
        this(
            buf.readNbt(),
            buf.readUUID(),
            buf.readInt(),
            buf.readInt(),
            buf.readUUID()
        );
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeNbt(tagPart);
        buf.writeUUID(uuid);
        buf.writeInt(max);
        buf.writeInt(tagType);
        buf.writeUUID(uuidSplit);
    }

    public static class Handler {
        public static void onMessage(CPacketItemUpdateSplitTagList message, Supplier<NetworkEvent.Context> ctx) {
            if(ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) 
            ctx.get().enqueueWork(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Player player = ctx.get().getSender();
                            int size = player.getInventory().getContainerSize();
                            for(int i = 0; i < size; i++){
                                ItemStack stack = player.getInventory().getItem(i);
                                if(stack.getItem() instanceof IItemHasUUID){
                                    UUID uuidInventory = IItemHasUUID.getUUID(stack);
                                    if(message.uuid.equals(uuidInventory)){
                                        handleSplitTagList(player, stack, message.tagPart, message.max, message.tagType, message.uuidSplit);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            );
        }

        private static void handleSplitTagList(Player player, ItemStack stack, CompoundTag tagPart, int max, int tagType, UUID uuidSplit){
            //get
            CompoundTag tagSplit = ((IItemHasSplitTagList)stack.getItem()).getTagSplit(player, stack);
            UUID uuidSplitStack = tagSplit.isEmpty() ? UUID.randomUUID(): tagSplit.getUUID(IItemHasSplitTagList.TAG_SPLIT_UUID);
            int maxSplitStack = tagSplit.getInt(IItemHasSplitTagList.TAG_SPLIT_MAX);
            int tagTypeStack = tagSplit.getInt(IItemHasSplitTagList.TAG_SPLIT_TAG_TYPE);
            ListTag tagSplitParts = tagSplit.getList(IItemHasSplitTagList.TAG_SPLIT_PARTS, 10);
    
            //check same
            if(!uuidSplitStack.equals(uuidSplit) || maxSplitStack != max || tagTypeStack != tagType){
                //reset
                tagSplit = new CompoundTag();
                tagSplitParts = new ListTag();
                uuidSplitStack = uuidSplit;
                maxSplitStack = max;
                tagTypeStack = tagType;
    
                tagSplit.putUUID(IItemHasSplitTagList.TAG_SPLIT_UUID, uuidSplitStack);
                tagSplit.putInt(IItemHasSplitTagList.TAG_SPLIT_MAX, maxSplitStack);
                tagSplit.putInt(IItemHasSplitTagList.TAG_SPLIT_TAG_TYPE, tagTypeStack);
                tagSplit.put(IItemHasSplitTagList.TAG_SPLIT_PARTS, tagSplitParts);
    
                ((IItemHasSplitTagList)stack.getItem()).onResetSplitList(player, stack);
            }
    
            //append
            tagSplitParts.add(tagPart);
    
            //set
            ((IItemHasSplitTagList)stack.getItem()).putTagSplit(player, stack, tagSplit);
            
            //check complete
            if(tagSplitParts.size() == maxSplitStack){
                //sort
                List<CompoundTag> completedSplitParts = new ArrayList<>();
                for (Tag tagSplitPart : tagSplitParts) {
                    if(tagSplitPart instanceof CompoundTag){
                        completedSplitParts.add((CompoundTag)tagSplitPart);
                    }
                }
                Collections.sort(
                    completedSplitParts,
                    (tag1, tag2) -> tag1.getInt(IItemHasSplitTagList.TAG_SPLIT_PART_INDEX) - tag2.getInt(IItemHasSplitTagList.TAG_SPLIT_PART_INDEX)
                );
    
                //check
                if(completedSplitParts.size() == maxSplitStack){
                    //merge
                    ListTag tagListMerged = new ListTag();
                    
                    for (CompoundTag tagSplitPart : completedSplitParts) {
                        ListTag tagList = tagSplitPart.getList(IItemHasSplitTagList.TAG_SPLIT_PART_LIST, tagTypeStack);
                        for (Tag tagBase : tagList) {
                            tagListMerged.add(tagBase);
                        }
                    }
                    //hook
                    ((IItemHasSplitTagList)stack.getItem()).onCompleteMergingSplitList(player, stack, tagListMerged);
                }
                ((IItemHasSplitTagList)stack.getItem()).removeTagSplit(player, stack);
            }
        }
    }

    public enum Operation{
        SET_FAKE,
        TRUE_BUILD
    }
}