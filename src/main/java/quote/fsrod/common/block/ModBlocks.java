package quote.fsrod.common.block;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
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
@ObjectHolder(LibMisc.MOD_ID)
public final class ModBlocks {
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
        Block.Properties builder = Block.Properties.create(Material.ROCK).hardnessAndResistance(0).sound(SoundType.STONE);
        for (int i = 0; i <= 10; i++) {
            register(new BlockMeasurement(i, builder), LibBlockName.MEASURING_BLOCK + i);
        }
    }

    @SubscribeEvent
    public static void registerItemBlocks(RegistryEvent.Register<Item> event){
        itemRegistry = event.getRegistry();
        registerItemBlock(new ItemBlockHasMetadata(blockMeasurement0, ModItems.defaultBuilder()), blockMeasurement0.getRegistryName());
        registerItemBlock(new ItemBlockHasMetadata(blockMeasurement1, ModItems.defaultBuilder()), blockMeasurement1.getRegistryName());
        registerItemBlock(new ItemBlockHasMetadata(blockMeasurement2, ModItems.defaultBuilder()), blockMeasurement2.getRegistryName());
        registerItemBlock(new ItemBlockHasMetadata(blockMeasurement3, ModItems.defaultBuilder()), blockMeasurement3.getRegistryName());
        registerItemBlock(new ItemBlockHasMetadata(blockMeasurement4, ModItems.defaultBuilder()), blockMeasurement4.getRegistryName());
        registerItemBlock(new ItemBlockHasMetadata(blockMeasurement5, ModItems.defaultBuilder()), blockMeasurement5.getRegistryName());
        registerItemBlock(new ItemBlockHasMetadata(blockMeasurement6, ModItems.defaultBuilder()), blockMeasurement6.getRegistryName());
        registerItemBlock(new ItemBlockHasMetadata(blockMeasurement7, ModItems.defaultBuilder()), blockMeasurement7.getRegistryName());
        registerItemBlock(new ItemBlockHasMetadata(blockMeasurement8, ModItems.defaultBuilder()), blockMeasurement8.getRegistryName());
        registerItemBlock(new ItemBlockHasMetadata(blockMeasurement9, ModItems.defaultBuilder()), blockMeasurement9.getRegistryName());
        registerItemBlock(new ItemBlockHasMetadata(blockMeasurement10, ModItems.defaultBuilder()),blockMeasurement10.getRegistryName());
    }

    private static void register(Block block, String name){
        blockRegistry.register(block.setRegistryName(LibMisc.MOD_ID, name));
    }

    private static void registerItemBlock(Item itemBlock, ResourceLocation path){
        itemRegistry.register(itemBlock.setRegistryName(path));
    }

    private static void registerItemBlock(Item itemBlock, String name){
        registerItemBlock(itemBlock, new ResourceLocation(LibMisc.MOD_ID, name));
    }

    @Nonnull
    public static Block getBlockMeasurement(BlockMeasurement.Type type){
        switch (type) {
			default:
			case NUM_0: return blockMeasurement0;
			case NUM_1: return blockMeasurement1;
			case NUM_2: return blockMeasurement2;
			case NUM_3: return blockMeasurement3;
			case NUM_4: return blockMeasurement4;
			case NUM_5: return blockMeasurement5;
			case NUM_6: return blockMeasurement6;
			case NUM_7: return blockMeasurement7;
			case NUM_8: return blockMeasurement8;
			case NUM_9: return blockMeasurement9;
			case DELETING: return blockMeasurement10;
        }
    }
}