package quote.fsrod.common.core.network.item;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import quote.fsrod.common.item.utils.IItemHasUUID;
import quote.fsrod.common.item.utils.IItemNotifyServer;

public class CPacketItemNotify {
    private CompoundTag tag;
    private UUID uuid;

    public static Optional<CPacketItemNotify> createPacket(Player player, ItemStack stack){
        Item item = stack.getItem();
        if(!(item instanceof IItemNotifyServer) || !IItemHasUUID.hasUUID(stack)) return Optional.empty();
        CompoundTag tag = ((IItemNotifyServer)item).getNotifyTag(player, stack);
        UUID uuid = IItemHasUUID.getOrCreateUUID(stack);

        return Optional.of(new CPacketItemNotify(tag, uuid));
    }

    private CPacketItemNotify(CompoundTag tag, @Nonnull UUID uuid) {
        this.tag = tag;
        this.uuid = uuid;
    }

    public CPacketItemNotify(FriendlyByteBuf buf) {
        this.tag = buf.readNbt();
        this.uuid = buf.readUUID();
    }

    public void encode(FriendlyByteBuf buf){
        buf.writeNbt(tag);
        buf.writeUUID(uuid);
    }

    public static class Handler {

        public static void onMessage(CPacketItemNotify message, Supplier<NetworkEvent.Context> ctx) {
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
                                if(stack.getItem() instanceof IItemNotifyServer){
                                    UUID uuidInventory = IItemHasUUID.getUUID(stack);
                                    if(message.uuid.equals(uuidInventory)){
                                        ((IItemNotifyServer)stack.getItem()).notified(player, stack, message.tag);
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
    }
}
