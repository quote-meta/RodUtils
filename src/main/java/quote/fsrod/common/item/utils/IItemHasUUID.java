package quote.fsrod.common.item.utils;

import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public interface IItemHasUUID {
    public static final String NBT_UUID = "uuid";

    @Nullable
    public static UUID getUUID(ItemStack stack){
        NBTTagCompound tag = stack.getTagCompound();
        if(tag == null){
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }
        if(!tag.hasUniqueId(NBT_UUID)){
            UUID uuid = UUID.randomUUID();
            tag.setUniqueId(NBT_UUID, uuid);
        }
        return tag.getUniqueId(NBT_UUID);
    }
}