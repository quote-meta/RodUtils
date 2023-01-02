package quote.fsrod.common.property.item;

import java.util.Optional;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import quote.fsrod.common.item.utils.IItemHasStructureData;
import quote.fsrod.common.lib.LibMisc;
import quote.fsrod.common.lib.LibProperty;
import quote.fsrod.common.structure.BasicStructure;

public class StructureDataProperty implements IStructureDataProperty, ICapabilitySerializable<Tag> {

    public static final ResourceLocation ID = new ResourceLocation(LibMisc.MOD_ID, LibProperty.STRUCTURE_DATA);

    private static Capability<IStructureDataProperty> INSTANCE = CapabilityManager.get(new CapabilityToken<>(){});

    //
    // ================== Parameters =======================
    //

    private final ItemStack itemStack;
    private BasicStructure structureData;

    private CompoundTag tag;

    //
    // ================== Utility ==========================
    //

    public static void addCapability(ItemStack itemStack, AttachCapabilitiesEvent<ItemStack> event){
        StructureDataProperty property = new StructureDataProperty(itemStack);
        property.init();
        event.addCapability(ID, property);
    }

    public static Optional<IStructureDataProperty> of(ItemStack stack){
        return Optional.ofNullable(stack.getCapability(INSTANCE, null).orElse(null));
    }

    //
    // ===================== Accessor ========================
    //

    @Override
    public CompoundTag getTag() {
        return tag;
    }

    @Override
    public Optional<BasicStructure> getStructureData() {
        return Optional.ofNullable(structureData);
    }

    //
    // =====================================================
    //

    @Override
    public void receiveSplittedStructureData() {
        
    }

    @Override
    public void completeMergingStructureData(ListTag mergedTagList) {
        CompoundTag tagStuctureData = this.itemStack.getOrCreateTag().getCompound(IItemHasStructureData.TAG_STRUCTURE_DATA);
        tagStuctureData.put(BasicStructure.TAG_DATA_STATE_NUMS, mergedTagList);
        structureData = new BasicStructure(tagStuctureData);

        bindStuctureData(structureData);
    }

    @Override
    public void bindStuctureData(BasicStructure structureData) {
        this.structureData = structureData;
    }

    public StructureDataProperty(ItemStack itemStack){
        this.itemStack = itemStack;
        this.tag = new CompoundTag();
    }

    public void init(){
        //
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == INSTANCE){
            return LazyOptional.of(() -> this).cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public Tag serializeNBT() {
        return new CompoundTag();
    }

    @Override
    public void deserializeNBT(Tag nbt) {
        
    }
}