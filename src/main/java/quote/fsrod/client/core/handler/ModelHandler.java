package quote.fsrod.client.core.handler;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import quote.fsrod.client.model.IHasCustomModel;
import quote.fsrod.common.lib.LibMisc;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = LibMisc.MOD_ID)
public class ModelHandler {

    private ModelHandler(){}

    public static final ModelHandler INSTANCE = new ModelHandler();

    @SideOnly(Side.CLIENT)
    private final net.minecraft.client.renderer.block.statemap.DefaultStateMapper stateMapper = new net.minecraft.client.renderer.block.statemap.DefaultStateMapper();

    @SubscribeEvent
    public static void registerCustomModels(ModelRegistryEvent event){
        for(Item item : Item.REGISTRY) {
            if(item instanceof IHasCustomModel){
                ((IHasCustomModel)item).registerCustomModel();
            }
        }

        for(Block block : Block.REGISTRY) {
            if(block instanceof IHasCustomModel){
                ((IHasCustomModel)block).registerCustomModel();
            }
        }
    }

    @SuppressWarnings("deprecation")
    @SideOnly(Side.CLIENT)
    public void registerBlockStateModel(Block block, int numOfState){
        Map<IBlockState, ModelResourceLocation> mapStateModelLocations = stateMapper.putStateModelLocations(block);
        ModelLoader.setCustomStateMapper(block, stateMapper);
        Item item = Item.getItemFromBlock(block);
        for (int i = 0; i < numOfState; i++) {
            IBlockState state = block.getStateFromMeta(i);
            ModelLoader.setCustomModelResourceLocation(item, i, mapStateModelLocations.get(state));
        }
    }
}