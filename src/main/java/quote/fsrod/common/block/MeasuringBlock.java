package quote.fsrod.common.block;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ticks.ScheduledTick;

public class MeasuringBlock extends Block{

    private static final Map<Type, MeasuringBlock> typeToBlockMap = new HashMap<>();

    public final Type type;

    public MeasuringBlock(Properties p, Type type) {
        super(p);
        this.type = type;
        typeToBlockMap.put(type, this);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if(newState.getBlock() instanceof MeasuringBlock) return;
        breakBlockChain(level, pos.above());
        breakBlockChain(level, pos.below());
        breakBlockChain(level, pos.east());
        breakBlockChain(level, pos.west());
        breakBlockChain(level, pos.north());
        breakBlockChain(level, pos.south());
    }

    private void breakBlockChain(Level level, BlockPos comparisonPos) {
        if (level.getBlockState(comparisonPos).getBlock() instanceof MeasuringBlock) {
            level.setBlockAndUpdate(comparisonPos, ModBlocks.measuringBlockDeleting.defaultBlockState());
            level.getBlockTicks().schedule(new ScheduledTick<>(ModBlocks.measuringBlockDeleting, comparisonPos, 1L, 1L));
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
        if(state.getBlock() == ModBlocks.measuringBlockDeleting){
            breakBlockChain(level, pos.above());
            breakBlockChain(level, pos.below());
            breakBlockChain(level, pos.east());
            breakBlockChain(level, pos.west());
            breakBlockChain(level, pos.north());
            breakBlockChain(level, pos.south());
            level.destroyBlock(pos, false);
        }
    }
    
    public float getShadeBrightness(BlockState state, BlockGetter getter, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter getter, BlockPos pos) {
        return true;
    }

    public static Optional<MeasuringBlock> getMeasuringBlock(Type type){
        MeasuringBlock block = typeToBlockMap.get(type);
        if(block != null){
            return Optional.of(block);
        }
        return Optional.empty();
    }

    public static int getTypeLength() {
        return Type.METADATA_LOOKUP.length;
    }

    public static Type getType(int i){
        if(i>=0 && i<getTypeLength()){
            return Type.METADATA_LOOKUP[i];
        }
        return Type.NUM_0;
    }
    
    enum Type{
        NUM_0,
        NUM_1,
        NUM_2,
        NUM_3,
        NUM_4,
        NUM_5,
        NUM_6,
        NUM_7,
        NUM_8,
        NUM_9,
        DELETING;

        private static final MeasuringBlock.Type[] METADATA_LOOKUP = values();
    }
}
