package quote.fsrod.common.item.utils;

import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public interface IItemHasUUID {
    public static final String NBT_UUID = "uuid";

    @Nullable
    public static UUID getUUID(ItemStack stack){
        CompoundNBT tag = stack.getOrCreateTag();
        if(!tag.hasUniqueId(NBT_UUID)){
            UUID uuid = UUID.randomUUID();
            tag.putUniqueId(NBT_UUID, uuid);
        }
        return tag.getUniqueId(NBT_UUID);
    }
}