package quote.fsrod.common.core.utils;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemUtils {
    
    private ItemUtils(){}

    public static boolean hasItem(IInventory inventory, Item item){
        int size = inventory.getSizeInventory();
        for (int i = 0; i < size; i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if(stack != null && stack.getItem() == item) return true;
        }
        return false;
    }
}