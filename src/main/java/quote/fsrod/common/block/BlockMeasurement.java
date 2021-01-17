package quote.fsrod.common.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.EnumProperty;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockMeasurement extends Block{
    public static final EnumProperty<BlockMeasurement.Type> VARIANT = EnumProperty.<BlockMeasurement.Type>create("variant", BlockMeasurement.Type.class);

    public final Type type;

    public BlockMeasurement(int meta, Block.Properties properties) {
        super(properties);
        setDefaultState(stateContainer.getBaseState().with(VARIANT, Type.DELETING));
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
            world.setBlockState(comparisonPos, stateContainer.getBaseState().with(VARIANT, Type.DELETING));
            world.getPendingBlockTicks().scheduleTick(comparisonPos, ModBlocks.blockMeasurement10, 0);
        }
    }

    @Override
    public void tick(BlockState state, World worldIn, BlockPos pos, Random random) {
        if(state.get(VARIANT) == Type.DELETING){
            breakBlockChain(worldIn, pos.up());
            breakBlockChain(worldIn, pos.down());
            breakBlockChain(worldIn, pos.east());
            breakBlockChain(worldIn, pos.west());
            breakBlockChain(worldIn, pos.north());
            breakBlockChain(worldIn, pos.south());
            worldIn.destroyBlock(pos, false);
        }
    }

    public static int getTypeLength() {
        return Type.METADATA_LOOKUP.length;
    }

    enum Type implements IStringSerializable {
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

        private static final BlockMeasurement.Type[] METADATA_LOOKUP = new BlockMeasurement.Type[values().length];

        private final int metadata;
        private final String name;

        Type(int metadataIn, String nameIn){
            this.metadata = metadataIn;
            this.name = nameIn;
        }

        public int getMetadata() {
            return metadata;
        }

        @Override
        public String getName() {
            return name;
        }

        public static Type byMetadata(int metadata)
        {
            if (metadata < 0 || metadata >= METADATA_LOOKUP.length){
                metadata = 0;
            }
            return METADATA_LOOKUP[metadata];
        }
    }
}