package quote.fsrod.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import quote.fsrod.common.block.ModBlocks;
import quote.fsrod.common.item.ModItems;
import quote.fsrod.common.lib.LibMisc;

public class ItemModelGenerator extends ItemModelProvider {

    public ItemModelGenerator(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, LibMisc.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        toBlock(ModBlocks.measuringBlock0);
        toBlock(ModBlocks.measuringBlock1);
        toBlock(ModBlocks.measuringBlock2);
        toBlock(ModBlocks.measuringBlock3);
        toBlock(ModBlocks.measuringBlock4);
        toBlock(ModBlocks.measuringBlock5);
        toBlock(ModBlocks.measuringBlock6);
        toBlock(ModBlocks.measuringBlock7);
        toBlock(ModBlocks.measuringBlock8);
        toBlock(ModBlocks.measuringBlock9);
        toBlock(ModBlocks.measuringBlockDeleting);

        single(ModItems.rodClone);
        single(ModItems.rodTransfer);
        single(ModItems.rodMeasurement);
        single(ModItems.charmUranus);
    }

    private void toBlock(Block block) {
		toBlockModel(block, block.getRegistryName().getPath());
	}

    private void toBlockModel(Block block, String model) {
		toBlockModel(block, new ResourceLocation(LibMisc.MOD_ID, "block/" + model));
	}

    private void toBlockModel(Block block, ResourceLocation model) {
		withExistingParent(block.getRegistryName().getPath(), model);
	}

    private ItemModelBuilder generated(String name, ResourceLocation... layers) {
        ItemModelBuilder builder = withExistingParent(name, "item/generated");
        for (int i = 0; i < layers.length; i++) {
            builder = builder.texture("layer" + i, layers[i]);
        }
        return builder;
    }

    private void single(Item item) {
        generated(item.getRegistryName().getPath(), new ResourceLocation(LibMisc.MOD_ID, "items/" + item.getRegistryName().getPath()));
    }
}