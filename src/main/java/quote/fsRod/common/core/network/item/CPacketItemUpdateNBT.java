package quote.fsRod.common.core.network.item;

import java.util.UUID;

import javax.annotation.Nonnull;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import quote.fsRod.common.core.utils.ModUtils;
import quote.fsRod.common.item.utils.IItemHasUUID;

// HACK: サーバとのアイテムの同期がずれると危険かもしれない
public class CPacketItemUpdateNBT implements IMessage {

    private NBTTagCompound nbtMerge;
    private UUID uuid;
    private Operation operation;

    public CPacketItemUpdateNBT() {
    }

    public CPacketItemUpdateNBT(NBTTagCompound nbtMerge, @Nonnull UUID uuid, Operation operation) {
        this.nbtMerge = nbtMerge;
        this.uuid = uuid;
        this.operation = operation;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        nbtMerge = ByteBufUtils.readTag(buf);
        uuid = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        operation = Operation.values()[buf.readInt()];
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, nbtMerge);
        ByteBufUtils.writeUTF8String(buf, uuid.toString());
        buf.writeInt(operation.ordinal());
    }

    public static class Handler implements IMessageHandler<CPacketItemUpdateNBT, IMessage> {

        @Override
        public IMessage onMessage(CPacketItemUpdateNBT message, MessageContext ctx) {
            if (ctx.side.isClient()) return null;
            MinecraftServer ms = FMLCommonHandler.instance().getMinecraftServerInstance();
            ms.addScheduledTask(() -> {
                EntityPlayer player = ctx.getServerHandler().player;
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
            }
            );
            return null;
        }

        private void handleUpdateNBT(ItemStack stack, NBTTagCompound tag, Operation operation){
            NBTTagCompound nbt = ModUtils.getTagThoughAbsent(stack);
            switch (operation) {
            case ADD:
                nbt.merge(tag);
                break;
                
            case REMOVE:
                for (String key : tag.getKeySet()) {
                    nbt.removeTag(key);
                }
                break;

            default:
                break;
            }
        }
    }

    public enum Operation{
        ADD,
        REMOVE,
    }
}