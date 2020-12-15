package quote.fsRod.common.core.network.item;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import quote.fsRod.client.core.helper.RodCloneHelper;

public class CPacketRodCloneStartBuilding implements IMessage {

    private Operation operation;

    private BlockPos posNear;
    private BlockPos posEnd;
    private BlockPos posDst;
    private Rotation rotation;

    public CPacketRodCloneStartBuilding() {
    }

    public CPacketRodCloneStartBuilding(Operation operation, BlockPos posNear, BlockPos posEnd, BlockPos posDst, Rotation rotation) {
        this.operation = operation;
        this.posNear = posNear;
        this.posEnd = posEnd;
        this.posDst = posDst;
        this.rotation = rotation;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int x1 = buf.readInt();
        int y1 = buf.readInt();
        int z1 = buf.readInt();
        int x2 = buf.readInt();
        int y2 = buf.readInt();
        int z2 = buf.readInt();
        int x3 = buf.readInt();
        int y3 = buf.readInt();
        int z3 = buf.readInt();
        
        posNear = new BlockPos(x1, y1, z1);
        posEnd = new BlockPos(x2, y2, z2);
        posDst = new BlockPos(x3, y3, z3);
        operation = Operation.values()[buf.readInt()];
        rotation = Rotation.values()[buf.readInt()];
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(posNear.getX());
        buf.writeInt(posNear.getY());
        buf.writeInt(posNear.getZ());
        buf.writeInt(posEnd.getX());
        buf.writeInt(posEnd.getY());
        buf.writeInt(posEnd.getZ());
        buf.writeInt(posDst.getX());
        buf.writeInt(posDst.getY());
        buf.writeInt(posDst.getZ());
        buf.writeInt(operation.ordinal());
        buf.writeInt(rotation.ordinal());
    }

    public static class Handler implements IMessageHandler<CPacketRodCloneStartBuilding, IMessage> {

        @Override
        public IMessage onMessage(CPacketRodCloneStartBuilding message, MessageContext ctx) {
            if (ctx.side.isClient()) return null;
            MinecraftServer ms = FMLCommonHandler.instance().getMinecraftServerInstance();
            ms.addScheduledTask(() -> {
                EntityPlayer player = ctx.getServerHandler().player;
                switch (message.operation) {
                    case TRUE_BUILD:
                        RodCloneHelper.handleBuilding(player, message.posNear, message.posEnd, message.posDst, message.rotation);
                        break;
                    case TRANSFER:
                        RodCloneHelper.handleTransfering(player, message.posNear, message.posEnd, message.posDst, message.rotation);
                        break;
                    default:
                        break;
                }
            }
            );
            return null;
        }
    }

    public enum Operation{
        SET_FAKE,
        TRUE_BUILD,
        TRANSFER,
    }
}