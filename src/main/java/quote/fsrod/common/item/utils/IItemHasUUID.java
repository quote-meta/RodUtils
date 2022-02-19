package quote.fsrod.common.item.utils;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public interface IItemHasUUID {
    public static final String TAG_UUID = "uuid";

    @Nonnull
    public static UUID getOrCreateUUID(ItemStack stack){
        CompoundTag tag = stack.getOrCreateTag();
        if(!tag.hasUUID(TAG_UUID)){
            UUID uuid = UUID.randomUUID();
            tag.putUUID(TAG_UUID, uuid);
        }
        return tag.getUUID(TAG_UUID);
    }

    @Nullable
    public static UUID getUUID(ItemStack stack){
        CompoundTag tag = stack.getTag();
        if(hasUUID(stack)){
            return tag.getUUID(TAG_UUID);
        }
        return null;
    }

    public static boolean hasUUID(ItemStack stack){
        CompoundTag tag = stack.getTag();
        return tag != null && tag.hasUUID(TAG_UUID);
    }
}
