package quote.fsrod.common.core.network.item;

import java.util.UUID;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import quote.fsrod.common.item.utils.IItemHasUUID;

// HACK: サーバとのアイテムの同期がずれると危険かもしれない
public class CPacketItemUpdateTag {
    
    private CompoundTag tagMerge;
    private UUID uuid;
    private Operation operation;

    public CPacketItemUpdateTag(CompoundTag tagMerge, @Nonnull UUID uuid, Operation operation) {
        this.tagMerge = tagMerge;
        this.uuid = uuid;
        this.operation = operation;
    }

    
    public CPacketItemUpdateTag(FriendlyByteBuf buf) {
        this.tagMerge = buf.readNbt();
        this.uuid = buf.readUUID();
        int ordinal = buf.readInt();
        if(ordinal < 0 || ordinal >= Operation.values().length){
            this.operation = Operation.None;
        }
        else{
            this.operation = Operation.values()[ordinal];
        }
    }

    public void encode(FriendlyByteBuf buf){
        buf.writeNbt(tagMerge);
        buf.writeUUID(uuid);
        buf.writeInt(operation.ordinal());
    }

    public static class Handler {

        public static void onMessage(CPacketItemUpdateTag message, Supplier<NetworkEvent.Context> ctx) {
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
                                        handleUpdateTag(stack, message.tagMerge, message.operation);
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

        private static void handleUpdateTag(ItemStack stack, CompoundTag tag, Operation operation){
            CompoundTag itemTag = stack.getOrCreateTag();
            switch (operation) {
            case ADD:
                itemTag.merge(tag);
                break;
                
            case REMOVE:
                for (String key : tag.getAllKeys()) {
                    itemTag.remove(key);
                }
                break;

            default:
                break;
            }
        }
    }

    public enum Operation{
        None,
        ADD,
        REMOVE,
    }
}
