package quote.fsrod.common.item.utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IItemNotifyServer extends IItemHasUUID{
    public void notified(Player player, ItemStack stack, CompoundTag tag);
    public CompoundTag getNotifyTag(Player player, ItemStack stack);
}
