package quote.fsrod.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import quote.fsrod.common.item.ModItems;
import quote.fsrod.common.lib.LibMisc;

public class ItemModelGenerator extends ItemModelProvider {

    public ItemModelGenerator(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, LibMisc.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        single(ModItems.rodClone);
        single(ModItems.rodTransfer);
        single(ModItems.rodMeasurement);
        single(ModItems.charmUranus);
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