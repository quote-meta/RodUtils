package quote.fsrod.common.item.rod;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import quote.fsrod.common.block.MeasuringBlock;
import quote.fsrod.common.block.ModBlocks;
import quote.fsrod.common.core.handler.ConfigHandler;

public class RodMeasurementItem extends Item{

    public RodMeasurementItem(Properties p) {
        super(p);
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        if(context.getHand() != InteractionHand.MAIN_HAND) return InteractionResult.SUCCESS;
        Level level = context.getLevel();
        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos();
        BlockPos posBase = pos.relative(context.getClickedFace());

        if (level.getBlockState(posBase).getBlock() instanceof MeasuringBlock) {
            posBase = pos;
        }

        level.setBlockAndUpdate(posBase, ModBlocks.measuringBlock0.defaultBlockState());
        Direction direction = context.getHorizontalDirection();
        if(player.isShiftKeyDown()){
            direction = Direction.UP;
            if(context.getClickedFace() == Direction.DOWN){
                direction = Direction.DOWN;
            }
        }
        int maxCount = ConfigHandler.COMMON.rodMeasurementMaxLength.get();
        for (int i = 0; i < maxCount; i++){
            BlockPos posTarget = posBase.relative(direction, i);
            BlockState targetState = level.getBlockState(posTarget);
            if (targetState.getMaterial().isReplaceable()){
                Optional<MeasuringBlock> possibleBlock = MeasuringBlock.getMeasuringBlock(MeasuringBlock.getType(i % (MeasuringBlock.getTypeLength() - 1)));
                BlockState blockMeasurement = possibleBlock.orElse(ModBlocks.measuringBlockDeleting).defaultBlockState();
                level.setBlockAndUpdate(posTarget, blockMeasurement);
            }
        }

        return super.onItemUseFirst(stack, context);
    }
}