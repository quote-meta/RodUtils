package quote.fsrod.common.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockMeasurement extends Block{

    public final Type type;

    public BlockMeasurement(int meta, Block.Properties properties) {
        super(properties);
        this.type = Type.values()[meta];
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        breakBlockChain(worldIn, pos.up());
        breakBlockChain(worldIn, pos.down());
        breakBlockChain(worldIn, pos.east());
        breakBlockChain(worldIn, pos.west());
        breakBlockChain(worldIn, pos.north());
        breakBlockChain(worldIn, pos.south());
        super.onReplaced(state, worldIn, pos, newState, isMoving);
    }

    private void breakBlockChain(World world, BlockPos comparisonPos) {
        if (world.getBlockState(comparisonPos).getBlock() instanceof BlockMeasurement) {
            world.setBlockState(comparisonPos, ModBlocks.blockMeasurement10.getDefaultState());
            world.getPendingBlockTicks().scheduleTick(comparisonPos, ModBlocks.blockMeasurement10, 0);
        }
    }

    @Override
    public void tick(BlockState state, World worldIn, BlockPos pos, Random random) {
        if(state.getBlock() == ModBlocks.blockMeasurement10){
            breakBlockChain(worldIn, pos.up());
            breakBlockChain(worldIn, pos.down());
            breakBlockChain(worldIn, pos.east());
            breakBlockChain(worldIn, pos.west());
            breakBlockChain(worldIn, pos.north());
            breakBlockChain(worldIn, pos.south());
            worldIn.destroyBlock(pos, false);
        }
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
        return true;
    }

    @Override
    public boolean causesSuffocation(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return false;
    }

    @Override
    public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return false;
    }

    @Override
    public boolean canEntitySpawn(BlockState state, IBlockReader worldIn, BlockPos pos, EntityType<?> type) {
        return false;
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
        NUM_0(0, "num_0"),
        NUM_1(1, "num_1"),
        NUM_2(2, "num_2"),
        NUM_3(3, "num_3"),
        NUM_4(4, "num_4"),
        NUM_5(5, "num_5"),
        NUM_6(6, "num_6"),
        NUM_7(7, "num_7"),
        NUM_8(8, "num_8"),
        NUM_9(9, "num_9"),
        DELETING(10, "deleting");

        private static final BlockMeasurement.Type[] METADATA_LOOKUP = values();

        private final int metadata;
        private final String name;

        Type(int metadataIn, String nameIn){
            this.metadata = metadataIn;
            this.name = nameIn;
        }
    }
}