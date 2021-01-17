package quote.fsrod.common.item;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import quote.fsrod.common.item.rod.ItemRodClone;
import quote.fsrod.common.item.rod.ItemRodMeasurement;
import quote.fsrod.common.item.rod.ItemRodReincarnation;
import quote.fsrod.common.lib.LibMisc;

@Mod.EventBusSubscriber(modid = LibMisc.MOD_ID)
public final class ModItems {
    public static Item rodClone;
    public static Item rodReincarnation;
    public static Item rodMeasurement;

    private static IForgeRegistry<Item> registry;

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event){
        registry = event.getRegistry();

        rodClone = new ItemRodClone();
        rodReincarnation = new ItemRodReincarnation();
        rodMeasurement = new ItemRodMeasurement();

        register(rodClone);
        register(rodReincarnation);
        register(rodMeasurement);
    }

    private static void register(Item item){
        registry.register(item);
    }
}