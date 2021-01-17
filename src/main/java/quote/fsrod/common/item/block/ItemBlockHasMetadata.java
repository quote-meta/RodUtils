package quote.fsrod.common.item.block;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemBlockHasMetadata extends AbstractModItemBlock {

    public ItemBlockHasMetadata(Block block, Item.Properties properties) {
        super(block, properties);
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return super.getTranslationKey(stack) + getDamage(stack);
    }
}