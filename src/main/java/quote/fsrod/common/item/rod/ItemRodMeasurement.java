package quote.fsrod.common.item.rod;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import quote.fsrod.common.block.BlockMeasurement;
import quote.fsrod.common.block.ModBlocks;
import quote.fsrod.common.core.handler.ConfigHandler;
import quote.fsrod.common.item.AbstractModItem;
import quote.fsrod.common.lib.LibItemName;

public class ItemRodMeasurement extends AbstractModItem {

    public ItemRodMeasurement(Item.Properties properties) {
        super(properties.maxStackSize(1).rarity(Rarity.EPIC));
    }

    // @Override
    // public EnumActionResult onItemUse(PlayerEntity player, World worldIn, BlockPos pos, EnumHand hand, Direction facing, float hitX, float hitY, float hitZ) {
    //     if (hand == EnumHand.OFF_HAND) return EnumActionResult.SUCCESS;
    //     BlockPos posBase = pos.offset(facing);
    //     if (worldIn.getBlockState(posBase).getBlock() == ModBlocks.blockMeasurement) {
    //         posBase = pos;
    //     }
    //     worldIn.setBlockState(posBase, ModBlocks.blockMeasurement.getStateFromMeta(0));
    //     Direction playerFacing = player.getHorizontalFacing();
    //     if (player.isSneaking()) {
    //         playerFacing = Direction.UP;
    //         if (facing == Direction.DOWN) {
    //             playerFacing = Direction.DOWN;
    //         }
    //     }
    //     int maxCount = ConfigHandler.rodMeasurementMaxLength;
    //     for (int i = 0; i < maxCount; i++) {
    //         BlockPos posTarget = posBase.offset(playerFacing,i);
    //         IBlockState targetState = worldIn.getBlockState(posTarget);
    //         if (targetState.getBlock().canPlaceBlockAt(worldIn,posTarget)) {
    //             worldIn.setBlockState(posTarget, ModBlocks.blockMeasurement.getStateFromMeta(i % (BlockMeasurement.getTypeLength() - 1)));
    //         }
    //     }
    //     return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
    // }
}