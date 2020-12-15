package quote.fsRod.common.item.rod;

import java.util.List;
import java.util.UUID;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import quote.fsRod.common.core.utils.ChatUtils;
import quote.fsRod.common.core.utils.ModUtils;
import quote.fsRod.common.item.AbstractModItem;
import quote.fsRod.common.item.ModItems;
import quote.fsRod.common.item.utils.EnumModRarity;
import quote.fsRod.common.item.utils.IItemHasSplitNBTList;
import quote.fsRod.common.item.utils.IItemHasUUID;
import quote.fsRod.common.lib.LibItemName;
import quote.fsRod.common.structure.BasicStrucure;

public class ItemRodReincarnation extends AbstractModItem implements IItemHasSplitNBTList {

    public static final String NBT_REACH_DISTANCE = "reachDistance";
    public static final String NBT_FILE = "fileName";
    public static final String NBT_POINT_NEAR = "pointNear";
    public static final String NBT_POINT_END = "pointEnd";
    public static final String NBT_POINT_SCHEDULED = "pointScheduled";
    public static final String NBT_POINT_SCHEDULED_FACING = "pointScheduledFacing";

    public static final String NBT_DATA = "structureData";

    public ItemRodReincarnation() {
        super(LibItemName.ROD_REINCARNATION);
        setHasSubtypes(false);
        setMaxStackSize(1);
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (!worldIn.isRemote) {
            NBTTagCompound tag = ModUtils.getTagThoughAbsent(stack);
            UUID uuid = IItemHasUUID.getUUID(stack);
            if (uuid == null) {
                tag.setUniqueId(NBT_UUID, UUID.randomUUID());
            }
            int oldReach = tag.getInteger(NBT_REACH_DISTANCE);
            int newReach = MathHelper.clamp(oldReach, 2, 10);
            tag.setInteger(NBT_REACH_DISTANCE, newReach);
        }
        super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public void onCompleteMergingSplitList(EntityPlayer player, ItemStack stack, NBTTagList nbtListMarged) {
        NBTTagCompound nbt = ModUtils.getTagThoughAbsent(stack);
        NBTTagCompound nbtData = nbt.getCompoundTag(NBT_DATA);
        nbtData.setTag(BasicStrucure.NBT_DATA_STATE_NUMS, nbtListMarged);
        ChatUtils.sendTranslatedChat(player, TextFormatting.GREEN, "fs.message.rodReincarnation.use.load.success");
    }

    public static boolean isRodReincarnation(ItemStack stack) {
        return stack != null && stack.getItem() == ModItems.rodReincarnation;
    }

    @Override
    public IRarity getForgeRarity(ItemStack stack) {
        return EnumModRarity.LEGENDARY;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getUnlocalizedName(ItemStack stack) {
        String s = "item.";
        return s + LibItemName.ROD_REINCARNATION;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format(this.getUnlocalizedName() + ".info"));
        NBTTagCompound tag = stack.getTagCompound();
        String fileName = "";
        if (tag != null) {
            fileName = tag.getString(NBT_FILE);
        }
        if (fileName.isEmpty()) {
            tooltip.add(I18n.format(this.getUnlocalizedName() + ".info.nofilename"));
        } else {
            tooltip.add(I18n.format(this.getUnlocalizedName() + ".info.filename", fileName));
        }
    }
}