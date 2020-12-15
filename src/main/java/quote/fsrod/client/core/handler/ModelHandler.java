package quote.fsrod.client.core.handler;

import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
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
    }
}