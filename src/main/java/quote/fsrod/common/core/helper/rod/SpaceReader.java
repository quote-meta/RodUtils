package quote.fsrod.common.core.helper.rod;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.AABB;

public class SpaceReader {

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
