package quote.fsrod.data;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import quote.fsrod.common.lib.LibMisc;
import quote.fsrod.data.recipes.ModRecipeProvider;

@Mod.EventBusSubscriber(modid = LibMisc.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {
    @SubscribeEvent
    public static void getherData(GatherDataEvent event){
        if(event.includeServer()) {

        }
        if(event.includeClient()) {
            event.getGenerator().addProvider(new ModRecipeProvider(event.getGenerator()));
            event.getGenerator().addProvider(new BlockStateGenerator(event.getGenerator(), event.getExistingFileHelper()));
            event.getGenerator().addProvider(new ItemModelGenerator(event.getGenerator(), event.getExistingFileHelper()));
        }
    }
}