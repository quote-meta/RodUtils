package quote.fsrod.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import quote.fsrod.common.item.ModItems;
import quote.fsrod.common.item.block.ItemBlockHasMetadata;
import quote.fsrod.common.lib.LibBlockName;
import quote.fsrod.common.lib.LibMisc;

@Mod.EventBusSubscriber(modid = LibMisc.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModBlocks {
    private ModBlocks(){}

    @ObjectHolder(LibBlockName.MEASURING_BLOCK + "0")    public static Block blockMeasurement0;
    @ObjectHolder(LibBlockName.MEASURING_BLOCK + "1")    public static Block blockMeasurement1;
    @ObjectHolder(LibBlockName.MEASURING_BLOCK + "2")    public static Block blockMeasurement2;
    @ObjectHolder(LibBlockName.MEASURING_BLOCK + "3")    public static Block blockMeasurement3;
    @ObjectHolder(LibBlockName.MEASURING_BLOCK + "4")    public static Block blockMeasurement4;
    @ObjectHolder(LibBlockName.MEASURING_BLOCK + "5")    public static Block blockMeasurement5;
    @ObjectHolder(LibBlockName.MEASURING_BLOCK + "6")    public static Block blockMeasurement6;
    @ObjectHolder(LibBlockName.MEASURING_BLOCK + "7")    public static Block blockMeasurement7;
    @ObjectHolder(LibBlockName.MEASURING_BLOCK + "8")    public static Block blockMeasurement8;
    @ObjectHolder(LibBlockName.MEASURING_BLOCK + "9")    public static Block blockMeasurement9;
    @ObjectHolder(LibBlockName.MEASURING_BLOCK + "10")    public static Block blockMeasurement10;

    private static IForgeRegistry<Block> blockRegistry;
    private static IForgeRegistry<Item> itemRegistry;

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event){
        blockRegistry = event.getRegistry();
        Block.Properties builder = Block.Properties.create(Material.ROCK).doesNotBlockMovement().hardnessAndResistance(0).sound(SoundType.STONE);
        for (int i = 0; i <= 10; i++) {
            register(new BlockMeasurement(i, builder), LibBlockName.MEASURING_BLOCK + "i");
        }
    }

    @SubscribeEvent
    public static void registerItemBlocks(RegistryEvent.Register<Item> event){
        itemRegistry = event.getRegistry();
        
        registerItemBlock(new ItemBlockHasMetadata(blockMeasurement0, ModItems.defaultBuilder()));
        registerItemBlock(new ItemBlockHasMetadata(blockMeasurement1, ModItems.defaultBuilder()));
        registerItemBlock(new ItemBlockHasMetadata(blockMeasurement2, ModItems.defaultBuilder()));
        registerItemBlock(new ItemBlockHasMetadata(blockMeasurement3, ModItems.defaultBuilder()));
        registerItemBlock(new ItemBlockHasMetadata(blockMeasurement4, ModItems.defaultBuilder()));
        registerItemBlock(new ItemBlockHasMetadata(blockMeasurement5, ModItems.defaultBuilder()));
        registerItemBlock(new ItemBlockHasMetadata(blockMeasurement6, ModItems.defaultBuilder()));
        registerItemBlock(new ItemBlockHasMetadata(blockMeasurement7, ModItems.defaultBuilder()));
        registerItemBlock(new ItemBlockHasMetadata(blockMeasurement8, ModItems.defaultBuilder()));
        registerItemBlock(new ItemBlockHasMetadata(blockMeasurement9, ModItems.defaultBuilder()));
        registerItemBlock(new ItemBlockHasMetadata(blockMeasurement10, ModItems.defaultBuilder()));
    }

    private static void register(Block block, String name){
        blockRegistry.register(block.setRegistryName(LibMisc.MOD_ID, name));
    }

    private static void registerItemBlock(Item itemBlock){
        itemRegistry.register(itemBlock);
    }
}