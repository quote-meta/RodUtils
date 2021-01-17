package quote.fsrod.common.item.rod;

import java.util.UUID;

import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import quote.fsrod.common.core.utils.ModUtils;
import quote.fsrod.common.item.AbstractModItem;
import quote.fsrod.common.item.ModItems;
import quote.fsrod.common.item.utils.IItemHasUUID;
import quote.fsrod.common.lib.LibItemName;

public class ItemRodClone extends AbstractModItem implements IItemHasUUID{

    public static final String NBT_REACH_DISTANCE = "reachDistance";
    public static final String NBT_DIMENSION = "pointDimension";
    public static final String NBT_POINT_NEAR = "pointNear";
    public static final String NBT_POINT_END = "pointEnd";
    public static final String NBT_POINT_SCHEDULED = "pointScheduled";
    public static final String NBT_POINT_SCHEDULED_FACING = "pointScheduledFacing";

    public ItemRodClone(Item.Properties properties) {
        super(properties.maxStackSize(1).rarity(Rarity.EPIC));
    }

    // @Override
    // public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
    //     if(!worldIn.isRemote){
    //         CompoundNBT tag = stack.getOrCreateTag();
    //         UUID uuid = IItemHasUUID.getUUID(stack);
    //         if(uuid == null){
    //             tag.setUniqueId(NBT_UUID, UUID.randomUUID());
    //         }
    //         int oldReach = tag.getInt(NBT_REACH_DISTANCE);
    //         int newReach = MathHelper.clamp(oldReach, 2, 10);
    //         tag.putInt(NBT_REACH_DISTANCE, newReach);
    //     }
    //     super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
    // }

    public static boolean isRodClone(ItemStack stack){
        return stack != null && stack.getItem() == ModItems.rodClone && stack.getDamage() == 0;
    }

    public static boolean isRodTransfer(ItemStack stack){
        return stack != null && stack.getItem() == ModItems.rodClone && stack.getDamage() == 1;
    }


    // @Override
    // @OnlyIn(Dist.CLIENT)
    // public String getUnlocalizedName(ItemStack stack) {
    //     String s = "item.";
    //     int damage = stack.getItemDamage();
    //     return s + LibItemName.ROD_CLONE + damage;
    // }

    // @Override
    // @OnlyIn(Dist.CLIENT)
    // public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
    //     if (this.isInCreativeTab(tab)){
    //         items.add(new ItemStack(this, 1, 0));
    //         items.add(new ItemStack(this, 1, 1));
    //     }
    // } 
}