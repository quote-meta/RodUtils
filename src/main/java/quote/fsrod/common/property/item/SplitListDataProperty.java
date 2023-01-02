package quote.fsrod.common.property.item;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import quote.fsrod.common.lib.LibMisc;
import quote.fsrod.common.lib.LibProperty;

public class SplitListDataProperty implements ISplitListDataProperty, ICapabilitySerializable<Tag> {
    
    public static final ResourceLocation ID = new ResourceLocation(LibMisc.MOD_ID, LibProperty.SPLIT_DATA);

    private static Capability<ISplitListDataProperty> INSTANCE = CapabilityManager.get(new CapabilityToken<>(){});

    //
    // ================== Parameters =======================
    //

    private final Map<UUID, CompoundTag> splitTagMap;

    //
    // ================== Utility ==========================
    //

    public static void addCapability(ItemStack itemStack, AttachCapabilitiesEvent<ItemStack> event){
        SplitListDataProperty property = new SplitListDataProperty(itemStack);
        property.init();
        event.addCapability(ID, property);
    }

    public static Optional<ISplitListDataProperty> of(ItemStack stack){
        return Optional.ofNullable(stack.getCapability(INSTANCE, null).orElse(null));
    }

    //
    // ===================== Accessor ========================
    //

    @Override
    public CompoundTag getTag(UUID uuid) {
        return splitTagMap.getOrDefault(uuid, new CompoundTag());
    }

    @Override
    public void removeTag(UUID uuid) {
        splitTagMap.remove(uuid);
    }

    //
    // =====================================================
    //



    public SplitListDataProperty(ItemStack itemStack){
        this.splitTagMap = new HashMap<>();
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
