package quote.fsrod.common.block;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import quote.fsrod.common.item.block.ItemBlockHasMetadata;
import quote.fsrod.common.lib.LibMisc;

@Mod.EventBusSubscriber(modid = LibMisc.MOD_ID)
public class ModBlocks {
    private ModBlocks(){}

    public static Block blockMeasurement;

    private static IForgeRegistry<Block> blockRegistry;
    private static IForgeRegistry<Item> itemRegistry;

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event){
        blockRegistry = event.getRegistry();
        
        blockMeasurement = new BlockMeasurement();

        register(blockMeasurement);
    }

    @SubscribeEvent
    public static void registerItemBlocks(RegistryEvent.Register<Item> event){
        itemRegistry = event.getRegistry();
        
        registerItemBlock(new ItemBlockHasMetadata(blockMeasurement));
    }

    private static void register(Block block){
        blockRegistry.register(block);
    }

    private static void registerItemBlock(Item itemBlock){
        itemRegistry.register(itemBlock);
    }
}