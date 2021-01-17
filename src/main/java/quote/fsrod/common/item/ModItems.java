package quote.fsrod.common.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import quote.fsrod.common.item.rod.ItemRodClone;
import quote.fsrod.common.item.rod.ItemRodMeasurement;
import quote.fsrod.common.item.rod.ItemRodReincarnation;
import quote.fsrod.common.lib.LibItemName;
import quote.fsrod.common.lib.LibMisc;

@Mod.EventBusSubscriber(modid = LibMisc.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(LibMisc.MOD_ID)
public final class ModItems {
    @ObjectHolder(LibItemName.ROD_CLONE)            public static Item rodClone;
    @ObjectHolder(LibItemName.ROD_REINCARNATION)    public static Item rodReincarnation;
    @ObjectHolder(LibItemName.ROD_MEASUREMENT)      public static Item rodMeasurement;

    private static IForgeRegistry<Item> registry;

	public static Item.Properties defaultBuilder() {
		return new Item.Properties().group(ItemGroup.TOOLS);
	}

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event){
        registry = event.getRegistry();

        register(new ItemRodClone(defaultBuilder()), LibItemName.ROD_CLONE);
        register(new ItemRodReincarnation(defaultBuilder()), LibItemName.ROD_REINCARNATION);
        register(new ItemRodMeasurement(defaultBuilder()), LibItemName.ROD_MEASUREMENT);
    }

    private static void register(Item item, String name){
        registry.register(item.setRegistryName(LibMisc.MOD_ID, name));
    }
}