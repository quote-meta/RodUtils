package quote.fsrod.common.item.rod;

import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import quote.fsrod.common.item.AbstractModItem;
import quote.fsrod.common.item.ModItems;
import quote.fsrod.common.item.utils.IItemHasUUID;

public class ItemRodTransfer extends AbstractModItem implements IItemHasUUID{

    public static final String NBT_REACH_DISTANCE = "reachDistance";
    public static final String NBT_DIMENSION = "pointDimension";
    public static final String NBT_POINT_NEAR = "pointNear";
    public static final String NBT_POINT_END = "pointEnd";
    public static final String NBT_POINT_SCHEDULED = "pointScheduled";
    public static final String NBT_POINT_SCHEDULED_FACING = "pointScheduledFacing";

    public ItemRodTransfer(Item.Properties properties) {
        super(properties.maxStackSize(1).rarity(Rarity.EPIC));
    }
    
    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if(!worldIn.isRemote){
            CompoundNBT tag = stack.getOrCreateTag();
            UUID uuid = IItemHasUUID.getUUID(stack);
            if(uuid == null){
                tag.putUniqueId(NBT_UUID, UUID.randomUUID());
            }
            int oldReach = tag.getInt(NBT_REACH_DISTANCE);
            int newReach = MathHelper.clamp(oldReach, 2, 10);
            tag.putInt(NBT_REACH_DISTANCE, newReach);
        }
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    public static boolean isRodTransfer(ItemStack stack){
        return stack != null && stack.getItem() == ModItems.rodTransfer;
    }
}