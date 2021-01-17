package quote.fsrod.common.item.rod;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IRarity;
import quote.fsrod.common.block.BlockMeasurement;
import quote.fsrod.common.block.ModBlocks;
import quote.fsrod.common.core.handler.ConfigHandler;
import quote.fsrod.common.item.AbstractModItem;
import quote.fsrod.common.lib.LibItemName;

public class ItemRodMeasurement extends AbstractModItem {

    public ItemRodMeasurement() {
        super(LibItemName.ROD_MEASUREMENT);
        setMaxStackSize(1);
    }

    @Override
    public IRarity getForgeRarity(ItemStack stack){
        return EnumRarity.EPIC;
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (hand == EnumHand.OFF_HAND) return EnumActionResult.SUCCESS;
        BlockPos posBase = pos.offset(facing);
        if (worldIn.getBlockState(posBase).getBlock() == ModBlocks.blockMeasurement) {
            posBase = pos;
        }
        worldIn.setBlockState(posBase, ModBlocks.blockMeasurement.getStateFromMeta(0));
        EnumFacing playerFacing = player.getHorizontalFacing();
        if (player.isSneaking()) {
            playerFacing = EnumFacing.UP;
            if (facing == EnumFacing.DOWN) {
                playerFacing = EnumFacing.DOWN;
            }
        }
        int maxCount = ConfigHandler.rodMeasurementMaxLength;
        for (int i = 0; i < maxCount; i++) {
            BlockPos posTarget = posBase.offset(playerFacing,i);
            IBlockState targetState = worldIn.getBlockState(posTarget);
            if (targetState.getBlock().canPlaceBlockAt(worldIn,posTarget)) {
                worldIn.setBlockState(posTarget, ModBlocks.blockMeasurement.getStateFromMeta(i % (BlockMeasurement.getTypeLength() - 1)));
            }
        }
        return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
    }
}