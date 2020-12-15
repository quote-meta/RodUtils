package quote.fsRod.common.item;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import quote.fsRod.common.item.rod.ItemRodClone;
import quote.fsRod.common.item.rod.ItemRodReincarnation;
import quote.fsRod.common.lib.LibMisc;

@Mod.EventBusSubscriber(modid = LibMisc.MOD_ID)
public final class ModItems {
    public static Item rodClone;
    public static Item rodReincarnation;

    private static IForgeRegistry<Item> registry;

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event){
        registry = event.getRegistry();

        rodClone = new ItemRodClone();
        rodReincarnation = new ItemRodReincarnation();

        register(rodClone);
        register(rodReincarnation);
    }

    private static void register(Item item){
        registry.register(item);
    }
}