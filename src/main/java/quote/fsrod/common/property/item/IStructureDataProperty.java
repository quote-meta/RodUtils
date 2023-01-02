package quote.fsrod.common.property.item;

import java.util.Optional;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import quote.fsrod.common.structure.BasicStructure;

public interface IStructureDataProperty {
    public void completeMergingStructureData(ListTag mergedTagList);
    public void bindStuctureData(BasicStructure structureData);

    public CompoundTag getTag();
    public Optional<BasicStructure> getStructureData();
}