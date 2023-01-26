package quote.fsrod.common.property.item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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
import quote.fsrod.common.item.utils.IItemHasUUID;
import quote.fsrod.common.lib.LibMisc;
import quote.fsrod.common.lib.LibProperty;
import quote.fsrod.common.structure.BasicStructure;

public class StructureDataProperty implements IStructureDataProperty, ICapabilitySerializable<Tag> {

    public static final ResourceLocation ID = new ResourceLocation(LibMisc.MOD_ID, LibProperty.STRUCTURE_DATA);

    private static Capability<IStructureDataProperty> INSTANCE = CapabilityManager.get(new CapabilityToken<>(){});

    //
    // ================== Permanent Storage =======================
    // It is permanent on refreshing ItemStack Capability
    // delete on Save & Load
    // delete on storage unused long ticks
    //

    private static class PermanentStorage{
        private BasicStructure structureData;
        private CompoundTag tag;
        private final int lifeTimeMax = 100;
        private int lifeTime = lifeTimeMax;
        private final UUID uuid;
        private PermanentStorage(UUID uuid){
            this.uuid = uuid;
        }
    }

    private static final Map<UUID, PermanentStorage> permanentMap = new HashMap<>();
    
    private static PermanentStorage getOrCreateStorage(UUID uuid){
        PermanentStorage storage = permanentMap.computeIfAbsent(uuid, u -> new PermanentStorage(uuid));
        storage.lifeTime = storage.lifeTimeMax;
        return storage;
    }

    public static void removeUnusedStorage(){
        permanentMap.values().stream()
        .forEach(s -> {
            s.lifeTime--;
        });

        List<UUID> removeUUIDs = permanentMap.values().stream()
        .filter(s -> {
            return s.lifeTime <= 0;
        })
        .map(s -> {
            return s.uuid;
        })
        .toList();

        removeUUIDs.forEach(u -> {
            permanentMap.remove(u);
        });
    }

    //
    // ================== Parameters =======================
    //

    private final ItemStack itemStack;
    private UUID uuid;

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
        return getOrCreateStorage(uuid).tag;
    }

    @Override
    public Optional<BasicStructure> getStructureData() {
        return Optional.ofNullable(getOrCreateStorage(uuid).structureData);
    }

    //
    // =====================================================
    //

    @Override
    public void completeMergingStructureData(ListTag mergedTagList) {
        CompoundTag tagStuctureData = this.itemStack.getOrCreateTag().getCompound(IItemHasStructureData.TAG_STRUCTURE_DATA).copy();
        tagStuctureData.put(BasicStructure.TAG_DATA_STATE_NUMS, mergedTagList);
        BasicStructure structureData = new BasicStructure(tagStuctureData);

        bindStuctureData(structureData);
    }

    @Override
    public void bindStuctureData(BasicStructure structureData) {
        getOrCreateStorage(uuid).structureData = structureData;
    }

    public StructureDataProperty(ItemStack itemStack){
        this.itemStack = itemStack;
        this.uuid = IItemHasUUID.getUUID(itemStack);
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
        CompoundTag tag = new CompoundTag();
        tag.putUUID("uuid", uuid);
        return tag;
    }

    @Override
    public void deserializeNBT(Tag nbt) {
        if(nbt instanceof CompoundTag){
            this.uuid = ((CompoundTag)nbt).getUUID("uuid");
        }
    }
}