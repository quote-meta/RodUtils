package quote.fsrod.common.item.rod;

import java.util.UUID;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
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

    public ItemRodClone() {
        super(LibItemName.ROD_CLONE);
        setHasSubtypes(true);
        setMaxStackSize(1);
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if(!worldIn.isRemote){
            NBTTagCompound tag = ModUtils.getTagThoughAbsent(stack);
            UUID uuid = IItemHasUUID.getUUID(stack);
            if(uuid == null){
                tag.setUniqueId(NBT_UUID, UUID.randomUUID());
            }
            int oldReach = tag.getInteger(NBT_REACH_DISTANCE);
            int newReach = MathHelper.clamp(oldReach, 2, 10);
            tag.setInteger(NBT_REACH_DISTANCE, newReach);
        }
        super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    public static boolean isRodClone(ItemStack stack){
        return stack != null && stack.getItem() == ModItems.rodClone && stack.getItemDamage() == 0;
    }

    public static boolean isRodTransfer(ItemStack stack){
        return stack != null && stack.getItem() == ModItems.rodClone && stack.getItemDamage() == 1;
    }

    @Override
    public IRarity getForgeRarity(ItemStack stack){
        return EnumRarity.EPIC;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getUnlocalizedName(ItemStack stack) {
        String s = "item.";
        int damage = stack.getItemDamage();
        return s + LibItemName.ROD_CLONE + damage;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)){
            items.add(new ItemStack(this, 1, 0));
            items.add(new ItemStack(this, 1, 1));
        }
    } 

    @SideOnly(Side.CLIENT)
    @Override
    public void registerCustomModel() {
        ModelLoader.setCustomModelResourceLocation(
            this, 0, new ModelResourceLocation(getRegistryName() + "_clone", "inventory")
        );
        ModelLoader.setCustomModelResourceLocation(
            this, 1, new ModelResourceLocation(getRegistryName() + "_transfer", "inventory")
        );
    }
}