package quote.fsrod.common.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import quote.fsrod.common.item.rod.RodCloneItem;
import quote.fsrod.common.item.rod.RodMeasurementItem;
import quote.fsrod.common.item.rod.RodRecollectionItem;
import quote.fsrod.common.item.rod.RodTeleportationItem;
import quote.fsrod.common.item.rod.RodTransferItem;
import quote.fsrod.common.item.utils.ModRarities;
import quote.fsrod.common.lib.LibItemName;
import quote.fsrod.common.lib.LibMisc;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModItems {

    public static Item rodClone;
    public static Item rodTransfer;
    public static Item rodRecollection;
    public static Item rodMeasurement;
    public static Item rodTeleportation;

    public static Item charmUranus;

    private static IForgeRegistry<Item> registry;

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event){
        registry = event.getRegistry();

        rodClone = new RodCloneItem(unstackable().rarity(Rarity.EPIC));
        rodTransfer = new RodTransferItem(unstackable().rarity(Rarity.EPIC));
        rodRecollection = new RodRecollectionItem(unstackable().rarity(ModRarities.LEGENDARY));
        rodMeasurement = new RodMeasurementItem(unstackable().rarity(Rarity.EPIC));
        rodTeleportation = new RodTeleportationItem(unstackable().rarity(Rarity.EPIC));
        
        charmUranus = new CharmUranusItem(unstackable().rarity(ModRarities.LEGENDARY));

        register(LibItemName.ROD_CLONE, rodClone);
        register(LibItemName.ROD_TRANSFER, rodTransfer);
        register(LibItemName.ROD_MEASUREMENT, rodMeasurement);
        register(LibItemName.ROD_RECOLLECTION, rodRecollection);
        register(LibItemName.ROD_TELEPORTATION, rodTeleportation);

        register(LibItemName.CHARM_URANUS, charmUranus);
    }

    public static Item.Properties defaultProperty() {
		return new Item.Properties().tab(CreativeModeTab.TAB_TOOLS);
	}

	private static Item.Properties unstackable() {
		return defaultProperty().stacksTo(1);
	}

    private static void register(String name, Item item){
        registry.register(item.setRegistryName(LibMisc.MOD_ID, name));
    }
}