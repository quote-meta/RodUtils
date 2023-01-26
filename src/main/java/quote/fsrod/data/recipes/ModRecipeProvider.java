package quote.fsrod.data.recipes;

import java.util.function.Consumer;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import quote.fsrod.common.item.ModItems;
import quote.fsrod.common.lib.LibMisc;

public class ModRecipeProvider extends RecipeProvider{
    public ModRecipeProvider(DataGenerator generator) {
		super(generator);
	}

    private ResourceLocation prefix(String path){
        return new ResourceLocation(LibMisc.MOD_ID, path);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> comsumer) {
        ShapedRecipeBuilder.shaped(ModItems.rodClone)
            .pattern(" DE")
            .pattern("PGD")
            .pattern("GP ")
            .define('P', Ingredient.of(Items.ENDER_PEARL))
            .define('D', Ingredient.of(Tags.Items.GEMS_DIAMOND))
            .define('G', Ingredient.of(Tags.Items.INGOTS_GOLD))
            .define('E', Ingredient.of(Tags.Items.GEMS_EMERALD))
            .unlockedBy("has_item", has(Tags.Items.GEMS_EMERALD))
            .save(comsumer, prefix("rod_clone"));
        
        ShapedRecipeBuilder.shaped(ModItems.rodTransfer)
            .pattern(" ED")
            .pattern("PGE")
            .pattern("GP ")
            .define('P', Ingredient.of(Items.ENDER_PEARL))
            .define('D', Ingredient.of(Tags.Items.GEMS_DIAMOND))
            .define('G', Ingredient.of(Tags.Items.INGOTS_GOLD))
            .define('E', Ingredient.of(Tags.Items.GEMS_EMERALD))
            .unlockedBy("has_item", has(Tags.Items.GEMS_EMERALD))
            .save(comsumer, prefix("rod_transfer"));
        
        ShapelessRecipeBuilder.shapeless(ModItems.rodClone)
            .requires(Ingredient.of(ModItems.rodTransfer))
            .unlockedBy("has_item", has(ModItems.rodTransfer))
            .save(comsumer, prefix("rod_clone_compatible"));
        
        ShapelessRecipeBuilder.shapeless(ModItems.rodTransfer)
            .requires(Ingredient.of(ModItems.rodClone))
            .unlockedBy("has_item", has(ModItems.rodClone))
            .save(comsumer, prefix("rod_transfer_compatible"));
        
        ShapedRecipeBuilder.shaped(ModItems.rodMeasurement)
            .pattern(" DE")
            .pattern("PTD")
            .pattern("CP ")
            .define('P', Ingredient.of(Items.ENDER_PEARL))
            .define('D', Ingredient.of(Tags.Items.GEMS_DIAMOND))
            .define('T', Ingredient.of(ModItems.rodTransfer))
            .define('C', Ingredient.of(ModItems.rodClone))
            .define('E', Ingredient.of(Tags.Items.GEMS_EMERALD))
            .unlockedBy("has_item", has(Tags.Items.GEMS_EMERALD))
            .save(comsumer, prefix("rod_measurement1"));
        
        ShapedRecipeBuilder.shaped(ModItems.rodMeasurement)
            .pattern(" DE")
            .pattern("PCD")
            .pattern("TP ")
            .define('P', Ingredient.of(Items.ENDER_PEARL))
            .define('D', Ingredient.of(Tags.Items.GEMS_DIAMOND))
            .define('T', Ingredient.of(ModItems.rodTransfer))
            .define('C', Ingredient.of(ModItems.rodClone))
            .define('E', Ingredient.of(Tags.Items.GEMS_EMERALD))
            .unlockedBy("has_item", has(Tags.Items.GEMS_EMERALD))
            .save(comsumer, prefix("rod_measurement2"));

        ShapedRecipeBuilder.shaped(ModItems.rodRecollection)
            .pattern(" DS")
            .pattern("PGD")
            .pattern("GP ")
            .define('P', Ingredient.of(Items.ENDER_PEARL))
            .define('D', Ingredient.of(Tags.Items.GEMS_DIAMOND))
            .define('G', Ingredient.of(Tags.Items.INGOTS_GOLD))
            .define('S', Ingredient.of(Items.NETHER_STAR))
            .unlockedBy("has_item", has(Items.NETHER_STAR))
            .save(comsumer, prefix("rod_recollection"));

        ShapedRecipeBuilder.shaped(ModItems.charmUranus)
            .pattern("NDN")
            .pattern("FBF")
            .pattern(" D ")
            .define('B', Ingredient.of(Blocks.BEACON))
            .define('N', Ingredient.of(Blocks.NETHERITE_BLOCK))
            .define('D', Ingredient.of(Blocks.DIAMOND_BLOCK))
            .define('F', Ingredient.of(Tags.Items.FEATHERS))
            .unlockedBy("has_item", has(Blocks.BEACON))
            .save(comsumer, prefix("charm_uranus"));
    }
}
