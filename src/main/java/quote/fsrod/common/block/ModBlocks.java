package quote.fsrod.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import quote.fsrod.common.item.ModItems;
import quote.fsrod.common.lib.LibBlockName;
import quote.fsrod.common.lib.LibMisc;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModBlocks {
    public static MeasuringBlock measuringBlock0;
    public static MeasuringBlock measuringBlock1;
    public static MeasuringBlock measuringBlock2;
    public static MeasuringBlock measuringBlock3;
    public static MeasuringBlock measuringBlock4;
    public static MeasuringBlock measuringBlock5;
    public static MeasuringBlock measuringBlock6;
    public static MeasuringBlock measuringBlock7;
    public static MeasuringBlock measuringBlock8;
    public static MeasuringBlock measuringBlock9;
    public static MeasuringBlock measuringBlockDeleting;

    private static IForgeRegistry<Block> blockRegistry;
    private static IForgeRegistry<Item> itemRegistry;

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event){
        blockRegistry = event.getRegistry();

        measuringBlock0 = new MeasuringBlock(BlockBehaviour.Properties.of(Material.CLAY, MaterialColor.COLOR_GREEN).isSuffocating(ModBlocks::never).isValidSpawn(ModBlocks::never).strength(0).noOcclusion(), MeasuringBlock.Type.NUM_0);
        measuringBlock1 = new MeasuringBlock(BlockBehaviour.Properties.of(Material.CLAY, MaterialColor.COLOR_RED).isSuffocating(ModBlocks::never).isValidSpawn(ModBlocks::never).strength(0).noOcclusion(), MeasuringBlock.Type.NUM_1);
        measuringBlock2 = new MeasuringBlock(BlockBehaviour.Properties.of(Material.CLAY, MaterialColor.COLOR_RED).isSuffocating(ModBlocks::never).isValidSpawn(ModBlocks::never).strength(0).noOcclusion(), MeasuringBlock.Type.NUM_2);
        measuringBlock3 = new MeasuringBlock(BlockBehaviour.Properties.of(Material.CLAY, MaterialColor.COLOR_RED).isSuffocating(ModBlocks::never).isValidSpawn(ModBlocks::never).strength(0).noOcclusion(), MeasuringBlock.Type.NUM_3);
        measuringBlock4 = new MeasuringBlock(BlockBehaviour.Properties.of(Material.CLAY, MaterialColor.COLOR_RED).isSuffocating(ModBlocks::never).isValidSpawn(ModBlocks::never).strength(0).noOcclusion(), MeasuringBlock.Type.NUM_4);
        measuringBlock5 = new MeasuringBlock(BlockBehaviour.Properties.of(Material.CLAY, MaterialColor.COLOR_GREEN).isSuffocating(ModBlocks::never).isValidSpawn(ModBlocks::never).strength(0).noOcclusion(), MeasuringBlock.Type.NUM_5);
        measuringBlock6 = new MeasuringBlock(BlockBehaviour.Properties.of(Material.CLAY, MaterialColor.COLOR_RED).isSuffocating(ModBlocks::never).isValidSpawn(ModBlocks::never).strength(0).noOcclusion(), MeasuringBlock.Type.NUM_6);
        measuringBlock7 = new MeasuringBlock(BlockBehaviour.Properties.of(Material.CLAY, MaterialColor.COLOR_RED).isSuffocating(ModBlocks::never).isValidSpawn(ModBlocks::never).strength(0).noOcclusion(), MeasuringBlock.Type.NUM_7);
        measuringBlock8 = new MeasuringBlock(BlockBehaviour.Properties.of(Material.CLAY, MaterialColor.COLOR_RED).isSuffocating(ModBlocks::never).isValidSpawn(ModBlocks::never).strength(0).noOcclusion(), MeasuringBlock.Type.NUM_8);
        measuringBlock9 = new MeasuringBlock(BlockBehaviour.Properties.of(Material.CLAY, MaterialColor.COLOR_RED).isSuffocating(ModBlocks::never).isValidSpawn(ModBlocks::never).strength(0).noOcclusion(), MeasuringBlock.Type.NUM_9);
        measuringBlockDeleting = new MeasuringBlock(BlockBehaviour.Properties.of(Material.AIR, MaterialColor.COLOR_BLACK).isSuffocating(ModBlocks::never).isValidSpawn(ModBlocks::never).strength(0).noOcclusion().isViewBlocking(ModBlocks::never), MeasuringBlock.Type.DELETING);

        register(LibBlockName.MEASURING_BLOCK_0, measuringBlock0);
        register(LibBlockName.MEASURING_BLOCK_1, measuringBlock1);
        register(LibBlockName.MEASURING_BLOCK_2, measuringBlock2);
        register(LibBlockName.MEASURING_BLOCK_3, measuringBlock3);
        register(LibBlockName.MEASURING_BLOCK_4, measuringBlock4);
        register(LibBlockName.MEASURING_BLOCK_5, measuringBlock5);
        register(LibBlockName.MEASURING_BLOCK_6, measuringBlock6);
        register(LibBlockName.MEASURING_BLOCK_7, measuringBlock7);
        register(LibBlockName.MEASURING_BLOCK_8, measuringBlock8);
        register(LibBlockName.MEASURING_BLOCK_9, measuringBlock9);
        register(LibBlockName.MEASURING_BLOCK_DELETING, measuringBlockDeleting);
    }

    @SubscribeEvent
    public static void registerItemBlocks(RegistryEvent.Register<Item> event){
        itemRegistry = event.getRegistry();

        registerItemBlock(measuringBlock0, ModItems.defaultProperty());
        registerItemBlock(measuringBlock1, ModItems.defaultProperty());
        registerItemBlock(measuringBlock2, ModItems.defaultProperty());
        registerItemBlock(measuringBlock3, ModItems.defaultProperty());
        registerItemBlock(measuringBlock4, ModItems.defaultProperty());
        registerItemBlock(measuringBlock5, ModItems.defaultProperty());
        registerItemBlock(measuringBlock6, ModItems.defaultProperty());
        registerItemBlock(measuringBlock7, ModItems.defaultProperty());
        registerItemBlock(measuringBlock8, ModItems.defaultProperty());
        registerItemBlock(measuringBlock9, ModItems.defaultProperty());
        registerItemBlock(measuringBlockDeleting, ModItems.defaultProperty());
    }

    private static void register(String name, Block block){
        blockRegistry.register(block.setRegistryName(LibMisc.MOD_ID, name));
    }

    private static void registerItemBlock(Block block, Item.Properties p){
        registerItemBlock(new BlockItem(block, p).setRegistryName(block.getRegistryName()));
    }

    private static void registerItemBlock(Item itemBlock){
        itemRegistry.register(itemBlock);
    }
  
    private static boolean never(BlockState state, BlockGetter reader, BlockPos pos) {
        return false;
    }
  
    private static boolean never(BlockState state, BlockGetter reader, BlockPos pos, EntityType<?> entityType) {
        return false;
    }
}