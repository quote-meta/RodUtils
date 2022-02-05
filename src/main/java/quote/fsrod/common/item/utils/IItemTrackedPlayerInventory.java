package quote.fsrod.common.item.utils;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IItemTrackedPlayerInventory {
    public default void trakingTick(Player player, ItemStack stack){};
    public default void onStartTracking(Player player, ItemStack stack){};
    public default void onStopTracking(Player player, ItemStack stack){};
}
