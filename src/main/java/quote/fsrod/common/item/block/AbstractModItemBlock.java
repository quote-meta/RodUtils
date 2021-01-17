package quote.fsrod.common.item.block;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

public abstract class AbstractModItemBlock extends BlockItem {

    protected AbstractModItemBlock(Block block, Item.Properties properties) {
        super(block, properties);
    }
}