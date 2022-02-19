package quote.fsrod.data;

import javax.annotation.Nonnull;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import quote.fsrod.common.block.ModBlocks;
import quote.fsrod.common.lib.LibMisc;

public class BlockStateGenerator extends BlockStateProvider {

    public BlockStateGenerator(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, LibMisc.MOD_ID, exFileHelper);
    }

    @Nonnull
    @Override
    public String getName() {
        return "RodUtils blockstate generator";
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlock(ModBlocks.measuringBlock0);
        simpleBlock(ModBlocks.measuringBlock1);
        simpleBlock(ModBlocks.measuringBlock2);
        simpleBlock(ModBlocks.measuringBlock3);
        simpleBlock(ModBlocks.measuringBlock4);
        simpleBlock(ModBlocks.measuringBlock5);
        simpleBlock(ModBlocks.measuringBlock6);
        simpleBlock(ModBlocks.measuringBlock7);
        simpleBlock(ModBlocks.measuringBlock8);
        simpleBlock(ModBlocks.measuringBlock9);
        simpleBlock(ModBlocks.measuringBlockDeleting);
    }
}