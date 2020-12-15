package quote.fsRod.common.core.network.item;

import java.util.UUID;

import javax.annotation.Nonnull;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import quote.fsRod.client.core.helper.RodReincarnationHelper;
import quote.fsRod.common.item.utils.IItemHasUUID;

public class CPacketRodReincarnationStartBuilding implements IMessage {

    private Operation operation;

    private BlockPos posDst;
    private Rotation rotation;
    private UUID uuid;

    public CPacketRodReincarnationStartBuilding() {
    }

    public CPacketRodReincarnationStartBuilding(Operation operation, BlockPos posDst, Rotation rotation, @Nonnull UUID uuid) {
        this.operation = operation;
        this.posDst = posDst;
        this.rotation = rotation;
        this.uuid = uuid;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int x3 = buf.readInt();
        int y3 = buf.readInt();
        int z3 = buf.readInt();
        
        posDst = new BlockPos(x3, y3, z3);
        operation = Operation.values()[buf.readInt()];
        rotation = Rotation.values()[buf.readInt()];
        uuid = UUID.fromString(ByteBufUtils.readUTF8String(buf));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(posDst.getX());
        buf.writeInt(posDst.getY());
        buf.writeInt(posDst.getZ());
        buf.writeInt(operation.ordinal());
        buf.writeInt(rotation.ordinal());
        ByteBufUtils.writeUTF8String(buf, uuid.toString());
    }

    public static class Handler implements IMessageHandler<CPacketRodReincarnationStartBuilding, IMessage> {

        @Override
        public IMessage onMessage(CPacketRodReincarnationStartBuilding message, MessageContext ctx) {
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
                            switch (message.operation) {
                                case TRUE_BUILD:
                                    RodReincarnationHelper.handleBuilding(player, message.posDst, message.rotation, stack);
                                    break;
                                default:
                                    break;
                            }
                            return;
                        }
                    }
                }

            }
            );
            return null;
        }
    }

    public enum Operation{
        SET_FAKE,
        TRUE_BUILD
    }
}