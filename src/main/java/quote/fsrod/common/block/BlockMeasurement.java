package quote.fsrod.common.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import quote.fsrod.client.core.handler.ModelHandler;
import quote.fsrod.client.model.IHasCustomModel;
import quote.fsrod.common.lib.LibBlockName;
import quote.fsrod.common.lib.LibMisc;

public class BlockMeasurement extends Block implements IHasCustomModel {
    public static final PropertyEnum<BlockMeasurement.Type> VARIANT = PropertyEnum.<BlockMeasurement.Type>create("variant", BlockMeasurement.Type.class);

    public BlockMeasurement() {
        super(Material.ROCK);
        setRegistryName(new ResourceLocation(LibMisc.MOD_ID, LibBlockName.MEASURING_BLOCK));
        setUnlocalizedName(LibBlockName.MEASURING_BLOCK);
        setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, Type.DELETING));
        setCreativeTab(CreativeTabs.TOOLS);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        breakBlockChain(worldIn, pos.up());
        breakBlockChain(worldIn, pos.down());
        breakBlockChain(worldIn, pos.east());
        breakBlockChain(worldIn, pos.west());
        breakBlockChain(worldIn, pos.north());
        breakBlockChain(worldIn, pos.south());
        super.breakBlock(worldIn, pos, state);
    }

    private void breakBlockChain(World world, BlockPos comparisonPos) {
        if (world.getBlockState(comparisonPos).getBlock() == ModBlocks.blockMeasurement) {
            world.setBlockState(comparisonPos, ModBlocks.blockMeasurement.getStateFromMeta(Type.DELETING.ordinal()));
            world.scheduleBlockUpdate(comparisonPos, ModBlocks.blockMeasurement, 0, 0);
        }
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if(getMetaFromState(state) == Type.DELETING.ordinal()){
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
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, VARIANT);
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        if (itemIn.equals(getCreativeTabToDisplayOn())){
            items.add(new ItemStack(this, 1, 0));
            items.add(new ItemStack(this, 1, 1));
            items.add(new ItemStack(this, 1, 2));
            items.add(new ItemStack(this, 1, 3));
            items.add(new ItemStack(this, 1, 4));
            items.add(new ItemStack(this, 1, 5));
            items.add(new ItemStack(this, 1, 6));
            items.add(new ItemStack(this, 1, 7));
            items.add(new ItemStack(this, 1, 8));
            items.add(new ItemStack(this, 1, 9));
            items.add(new ItemStack(this, 1, 10));
        }
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(this, 1, state.getValue(VARIANT).getMetadata());
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        if (meta >= BlockMeasurement.Type.values().length) {
            meta = 0;
        }
        return getDefaultState().withProperty(VARIANT, BlockMeasurement.Type.values()[meta]);
    }

    @Override
    public int getMetaFromState(IBlockState state){
        return state.getValue(VARIANT).getMetadata();
    }

    @Override
    public int damageDropped(IBlockState state) {
        BlockMeasurement.Type type = state.getValue(VARIANT);
        return type.getMetadata();
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerCustomModel() {
        ModelHandler.INSTANCE.registerBlockStateModel(this, BlockMeasurement.Type.METADATA_LOOKUP.length);
    }

    public static int getTypeLength() {
        return Type.METADATA_LOOKUP.length;
    }

    enum Type implements IStringSerializable {
        NUM_0(0, "num_0", MapColor.SNOW),
        NUM_1(1, "num_1", MapColor.SNOW),
        NUM_2(2, "num_2", MapColor.SNOW),
        NUM_3(3, "num_3", MapColor.SNOW),
        NUM_4(4, "num_4", MapColor.SNOW),
        NUM_5(5, "num_5", MapColor.SNOW),
        NUM_6(6, "num_6", MapColor.SNOW),
        NUM_7(7, "num_7", MapColor.SNOW),
        NUM_8(8, "num_8", MapColor.SNOW),
        NUM_9(9, "num_9", MapColor.SNOW),
        DELETING(10, "deleting", MapColor.AIR);

        private static final BlockMeasurement.Type[] METADATA_LOOKUP = new BlockMeasurement.Type[values().length];

        private final int metadata;
        private final String name;
        private final MapColor color;

        Type(int metadataIn, String nameIn, MapColor color){
            this.metadata = metadataIn;
            this.name = nameIn;
            this.color = color;
        }

        public int getMetadata() {
            return metadata;
        }

        public MapColor getColor()
        {
            return color;
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