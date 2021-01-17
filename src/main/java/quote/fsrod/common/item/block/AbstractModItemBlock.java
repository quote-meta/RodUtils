package quote.fsrod.common.item.block;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public abstract class AbstractModItemBlock extends ItemBlock {

    protected AbstractModItemBlock(Block block) {
        super(block);
        setRegistryName(block.getRegistryName());
    }
}