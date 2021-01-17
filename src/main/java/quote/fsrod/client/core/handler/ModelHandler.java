package quote.fsrod.client.core.handler;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import quote.fsrod.client.model.IHasCustomModel;
import quote.fsrod.common.lib.LibMisc;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = LibMisc.MOD_ID)
public class ModelHandler {

    private ModelHandler(){}

    public static final ModelHandler INSTANCE = new ModelHandler();

    // @OnlyIn(Dist.CLIENT)
    // private final DefaultStateMapper stateMapper = new net.minecraft.client.renderer.block.statemap.DefaultStateMapper();

    // @SubscribeEvent
    // public static void registerCustomModels(ModelRegistryEvent event){
    //     for(Item item : Item) {
    //         if(item instanceof IHasCustomModel){
    //             ((IHasCustomModel)item).registerCustomModel();
    //         }
    //     }

    //     for(Block block : Block.REGISTRY) {
    //         if(block instanceof IHasCustomModel){
    //             ((IHasCustomModel)block).registerCustomModel();
    //         }
    //     }
    // }

    // @SuppressWarnings("deprecation")
    // @OnlyIn(Dist.CLIENT)
    // public void registerBlockStateModel(Block block, int numOfState){
    //     Map<IBlockState, ModelResourceLocation> mapStateModelLocations = stateMapper.putStateModelLocations(block);
    //     ModelLoader.setCustomStateMapper(block, stateMapper);
    //     Item item = Item.getItemFromBlock(block);
    //     for (int i = 0; i < numOfState; i++) {
    //         IBlockState state = block.getStateFromMeta(i);
    //         ModelLoader.setCustomModelResourceLocation(item, i, mapStateModelLocations.get(state));
    //     }
    // }
}