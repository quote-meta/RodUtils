package quote.fsrod.common.item.block;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class ItemBlockHasMetadata extends AbstractModItemBlock {

    public ItemBlockHasMetadata(Block block) {
        super(block);
        setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName(stack) + getDamage(stack);
    }
}