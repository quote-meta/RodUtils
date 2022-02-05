package quote.fsrod.common.property.player;

import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import quote.fsrod.common.item.utils.IItemTrackedPlayerInventory;
import quote.fsrod.common.lib.LibMisc;
import quote.fsrod.common.lib.LibProperty;

public class PlayerProperty implements IPlayerProperty, ICapabilitySerializable<Tag> {

    public static final ResourceLocation ID = new ResourceLocation(LibMisc.MOD_ID, LibProperty.PLAYER);

    private static Capability<IPlayerProperty> INSTANCE = CapabilityManager.get(new CapabilityToken<>(){});

    //
    // ================== Parameters =======================
    //

    private final Player player;
    private NonNullList<ItemStack> lastTrackedSelectionItemStacks;

    //
    // ================== Utility ==========================
    //

    public static void addCapability(Player player, AttachCapabilitiesEvent<Entity> event){
        PlayerProperty property = new PlayerProperty(player);
        property.init();
        event.addCapability(ID, property);
    }

    public static LazyOptional<IPlayerProperty> of(Player player){
        return player.getCapability(INSTANCE, null);
    }

    //
    // ===================== Accessor ========================
    //



    //
    // =====================================================
    //

    public PlayerProperty(Player player){
        this.player = player;
        lastTrackedSelectionItemStacks = NonNullList.withSize(Inventory.getSelectionSize(), ItemStack.EMPTY);
    }

    public void init(){
        //
    }

    @Override
    public void update(){
        for (int i = 0; i < lastTrackedSelectionItemStacks.size(); i++) {
            ItemStack newStack = player.getInventory().getItem(i);
            ItemStack trackingStack = lastTrackedSelectionItemStacks.get(i);
            
            if(trackingStack.getItem() == newStack.getItem()) {
                if(trackingStack.getItem() instanceof IItemTrackedPlayerInventory){
                    ((IItemTrackedPlayerInventory)trackingStack.getItem()).trakingTick(player, trackingStack);
                }
            }
            else{
                if(trackingStack.getItem() instanceof IItemTrackedPlayerInventory){
                    ((IItemTrackedPlayerInventory)trackingStack.getItem()).onStopTracking(player, trackingStack);
                }
    
                if(newStack.getItem() instanceof IItemTrackedPlayerInventory){
                    ((IItemTrackedPlayerInventory)newStack.getItem()).onStartTracking(player, newStack);
                }
            }

            lastTrackedSelectionItemStacks.set(i, newStack.copy());
        }
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