package quote.fsrod.common.item.rod;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Rarity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import quote.fsrod.common.block.BlockMeasurement;
import quote.fsrod.common.block.ModBlocks;
import quote.fsrod.common.core.handler.ConfigHandler;
import quote.fsrod.common.item.AbstractModItem;

public class ItemRodMeasurement extends AbstractModItem {

    public ItemRodMeasurement(Item.Properties properties) {
        super(properties.maxStackSize(1).rarity(Rarity.EPIC));
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        if (context.getHand() == Hand.OFF_HAND) return ActionResultType.SUCCESS;
        World worldIn = context.getWorld();
        PlayerEntity player = context.getPlayer();
        BlockPos pos = context.getPos();
        BlockPos posBase = pos.offset(context.getFace());

        if (worldIn.getBlockState(posBase).getBlock() instanceof BlockMeasurement) {
            posBase = pos;
        }
        
        worldIn.setBlockState(posBase, ModBlocks.blockMeasurement0.getDefaultState());
        Direction playerFacing = player.getHorizontalFacing();
        if (player.isSneaking()) {
            playerFacing = Direction.UP;
            if (context.getFace() == Direction.DOWN) {
                playerFacing = Direction.DOWN;
            }
        }
        int maxCount = ConfigHandler.COMMON.rodMeasurementMaxLength.get();
        for (int i = 0; i < maxCount; i++) {
            BlockPos posTarget = posBase.offset(playerFacing,i);
            BlockState targetState = worldIn.getBlockState(posTarget);
            if (targetState.getBlock().getMaterial(targetState).isReplaceable()) {
                worldIn.setBlockState(posTarget, ModBlocks.getBlockMeasurement(BlockMeasurement.getType(i % (BlockMeasurement.getTypeLength() - 1))).getDefaultState());
            }
        }
        return super.onItemUse(context);
    }
}