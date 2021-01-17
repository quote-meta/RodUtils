package quote.fsrod.common.core.network.item;

import java.util.UUID;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;
import quote.fsrod.client.core.helper.RodReincarnationHelper;
import quote.fsrod.common.item.utils.IItemHasUUID;

public class CPacketRodReincarnationStartBuilding {

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

    public static CPacketRodReincarnationStartBuilding decode(PacketBuffer buf) {
        return new CPacketRodReincarnationStartBuilding(
            Operation.values()[buf.readInt()],
            buf.readBlockPos(),
            Rotation.values()[buf.readInt()],
            buf.readUniqueId()
        );
    }

    public static void encode(CPacketRodReincarnationStartBuilding msg, PacketBuffer buf) {
        buf.writeInt(msg.operation.ordinal());
        buf.writeBlockPos(msg.posDst);
        buf.writeInt(msg.rotation.ordinal());
        buf.writeUniqueId(msg.uuid);
    }

    public static void handle(CPacketRodReincarnationStartBuilding message, Supplier<NetworkEvent.Context> ctx){
        if (ctx.get().getDirection().getReceptionSide().isServer()){
            ctx.get().enqueueWork(() ->{
                PlayerEntity player = ctx.get().getSender();
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
            });
            ctx.get().setPacketHandled(true);
        }
    }

    public enum Operation{
        SET_FAKE,
        TRUE_BUILD
    }
}