package quote.fsrod.common.property.item;

import java.util.UUID;

import net.minecraft.nbt.CompoundTag;

public interface ISplitListDataProperty {
    public CompoundTag getTag(UUID uuid);
    public void removeTag(UUID uuid);
}
