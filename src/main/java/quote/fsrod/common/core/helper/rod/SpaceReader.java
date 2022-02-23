package quote.fsrod.common.core.helper.rod;

import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import quote.fsrod.common.item.utils.IItemHasSpaceInfoTag;

public class SpaceReader {

    @SuppressWarnings("resource")
    public static BlockPos getBlockPosSeeing(ItemStack stack, Player player, float partialTicks){
        CompoundTag tag = stack.getOrCreateTag();

        int distance = tag.getInt(IItemHasSpaceInfoTag.TAG_REACH_DISTANCE);
        BlockPos blockPos = null;
        HitResult objectMouseOver = Minecraft.getInstance().hitResult;

        boolean isLookingAir = true;
        if (objectMouseOver instanceof BlockHitResult && ((BlockHitResult)objectMouseOver).getBlockPos() != null){
            blockPos = ((BlockHitResult)objectMouseOver).getBlockPos();
            isLookingAir = player.level.getBlockState(blockPos).isAir();
        }
        if (isLookingAir){
            Vec3 eyePos = player.getEyePosition(partialTicks);
            Vec3 viewVec = player.getViewVector(partialTicks).scale(distance);
            Vec3 viewPos = eyePos.add(viewVec);
            objectMouseOver = player.level.clip(new ClipContext(eyePos, viewPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null));
            if(objectMouseOver instanceof BlockHitResult && ((BlockHitResult)objectMouseOver).getBlockPos() != null){
                blockPos = ((BlockHitResult)objectMouseOver).getBlockPos();
            }
            else{
                blockPos = new BlockPos(viewPos);
            }
        }

        return blockPos;
    }

    @Nonnull
    public static AABB getScheduledAABB(BlockPos posNear, BlockPos posEnd, Player player, BlockPos posSeeing){
        Direction direction = player.getDirection();
        
        return getScheduledAABB(posNear, posEnd, direction, posSeeing);
    }

    @Nonnull
    public static AABB getScheduledAABB(BlockPos posNear, BlockPos posEnd, Direction direction, BlockPos posScheduled){
        Direction facingSource = getFacingAABB(posNear, posEnd);
        BlockPos posDiff = posEnd.subtract(posNear);
        Rotation rot = getRotation(facingSource, direction);
        BlockPos posRotated = posDiff.rotate(rot);

        return new AABB(posScheduled, posScheduled.offset(posRotated)).expandTowards(1, 1, 1);
    }

    
    @Nonnull
    public static AABB getScheduledAABB(BlockPos posScheduled, BlockPos posDiff, Direction direction){
        Direction directionSrc = Direction.EAST;
        Rotation rot = getRotation(directionSrc, direction);
        BlockPos posRotated = posDiff.rotate(rot);

        return new AABB(posScheduled, posScheduled.offset(posRotated)).expandTowards(1, 1, 1);
    }

    @Nonnull
    public static Direction getFacingAABB(BlockPos posNear, BlockPos posEnd){
        if(posEnd.getX() >= posNear.getX()){
            if(posEnd.getZ() >= posNear.getZ()){
                return Direction.EAST;
            }
            return Direction.NORTH;
        }
        else{
            if(posEnd.getZ() >= posNear.getZ()){
                return Direction.SOUTH;
            }
            return Direction.WEST;
        }
    }

    @Nonnull
    public static Rotation getRotation(Direction directionFrom, Direction directionTo){
        int rotationInt = directionTo.get2DDataValue() - directionFrom.get2DDataValue();
        if(rotationInt > 2){
            rotationInt -= 4;
        }
        if(rotationInt < -1){
            rotationInt += 4;
        }
        if(rotationInt == -1){
            return Rotation.COUNTERCLOCKWISE_90;
        }
        else if(rotationInt == 1){
            return Rotation.CLOCKWISE_90;
        }
        else if(rotationInt == 2){
            return Rotation.CLOCKWISE_180;
        }
        return Rotation.NONE;
    }
}
