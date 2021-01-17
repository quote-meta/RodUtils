package quote.fsrod.common.core.network.item;

import java.util.function.Supplier;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;
import quote.fsrod.client.core.helper.RodCloneHelper;

public class CPacketRodCloneStartBuilding {

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

    public static CPacketRodCloneStartBuilding decode(PacketBuffer buf) {
        return new CPacketRodCloneStartBuilding(
            Operation.values()[buf.readInt()],
            buf.readBlockPos(),
            buf.readBlockPos(),
            buf.readBlockPos(),
            Rotation.values()[buf.readInt()]
        );
    }

    public static void encode(CPacketRodCloneStartBuilding msg, PacketBuffer buf) {
        buf.writeInt(msg.operation.ordinal());
        buf.writeBlockPos(msg.posNear);
        buf.writeBlockPos(msg.posEnd);
        buf.writeBlockPos(msg.posDst);
        buf.writeInt(msg.rotation.ordinal());
    }

    public static void handle(CPacketRodCloneStartBuilding message, Supplier<NetworkEvent.Context> ctx){
        if (ctx.get().getDirection().getReceptionSide().isServer()){
            ctx.get().enqueueWork(() ->{
                PlayerEntity player = ctx.get().getSender();
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
            });
            ctx.get().setPacketHandled(true);
        }
    }

    public enum Operation{
        SET_FAKE,
        TRUE_BUILD,
        TRANSFER,
    }
}