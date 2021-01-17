package quote.fsrod.common.core.network.item;

import java.util.UUID;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import quote.fsrod.common.item.utils.IItemHasUUID;

// HACK: サーバとのアイテムの同期がずれると危険かもしれない
public class CPacketItemUpdateNBT {

    private CompoundNBT nbtMerge;
    private UUID uuid;
    private Operation operation;

    public CPacketItemUpdateNBT(CompoundNBT nbtMerge, @Nonnull UUID uuid, Operation operation) {
        this.nbtMerge = nbtMerge;
        this.uuid = uuid;
        this.operation = operation;
    }

    public static CPacketItemUpdateNBT decode(PacketBuffer buf) {
        return new CPacketItemUpdateNBT(
            buf.readCompoundTag(),
            buf.readUniqueId(),
            Operation.values()[buf.readInt()]
        );
    }

    public static void encode(CPacketItemUpdateNBT msg, PacketBuffer buf) {
        buf.writeCompoundTag(msg.nbtMerge);
        buf.writeUniqueId(msg.uuid);
        buf.writeInt(msg.operation.ordinal());
    }

    public static void handle(CPacketItemUpdateNBT message, Supplier<NetworkEvent.Context> ctx){
        if (ctx.get().getDirection().getReceptionSide().isServer()){
            ctx.get().enqueueWork(() ->{
                PlayerEntity player = ctx.get().getSender();
                int size = player.inventory.getSizeInventory();
                for(int i = 0; i < size; i++){
                    ItemStack stack = player.inventory.getStackInSlot(i);
                    if(stack.getItem() instanceof IItemHasUUID){
                        UUID uuidInventory = IItemHasUUID.getUUID(stack);
                        if(message.uuid.equals(uuidInventory)){
                            handleUpdateNBT(stack, message.nbtMerge, message.operation);
                            return;
                        }
                    }
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }

    private static void handleUpdateNBT(ItemStack stack, CompoundNBT tag, Operation operation){
        CompoundNBT nbt = stack.getOrCreateTag();
        switch (operation) {
        case ADD:
            nbt.merge(tag);
            break;
            
        case REMOVE:
            for (String key : tag.keySet()) {
                nbt.remove(key);
            }
            break;

        default:
            break;
        }
    }

    public enum Operation{
        ADD,
        REMOVE,
    }
}