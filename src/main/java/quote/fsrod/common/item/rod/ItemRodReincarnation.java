package quote.fsrod.common.item.rod;

import java.util.List;
import java.util.UUID;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import quote.fsrod.common.core.utils.ChatUtils;
import quote.fsrod.common.item.AbstractModItem;
import quote.fsrod.common.item.ModItems;
import quote.fsrod.common.item.utils.IItemHasSplitNBTList;
import quote.fsrod.common.item.utils.IItemHasUUID;
import quote.fsrod.common.structure.BasicStrucure;

public class ItemRodReincarnation extends AbstractModItem implements IItemHasSplitNBTList {

    public static final String NBT_REACH_DISTANCE = "reachDistance";
    public static final String NBT_FILE = "fileName";
    public static final String NBT_POINT_NEAR = "pointNear";
    public static final String NBT_POINT_END = "pointEnd";
    public static final String NBT_POINT_SCHEDULED = "pointScheduled";
    public static final String NBT_POINT_SCHEDULED_FACING = "pointScheduledFacing";

    public static final String NBT_DATA = "structureData";

    public ItemRodReincarnation(Item.Properties properties) {
        super(properties.maxStackSize(1).rarity(Rarity.EPIC));
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (!worldIn.isRemote) {
            CompoundNBT tag = stack.getOrCreateTag();
            UUID uuid = IItemHasUUID.getUUID(stack);
            if (uuid == null) {
                tag.putUniqueId(NBT_UUID, UUID.randomUUID());
            }
            int oldReach = tag.getInt(NBT_REACH_DISTANCE);
            int newReach = MathHelper.clamp(oldReach, 2, 10);
            tag.putInt(NBT_REACH_DISTANCE, newReach);
        }
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public void onCompleteMergingSplitList(PlayerEntity player, ItemStack stack, ListNBT nbtListMarged) {
        CompoundNBT nbt = stack.getOrCreateTag();
        CompoundNBT nbtData = nbt.getCompound(NBT_DATA);
        nbtData.put(BasicStrucure.NBT_DATA_STATE_NUMS, nbtListMarged);
        ChatUtils.sendTranslatedChat(player, TextFormatting.GREEN, "fs.message.rodReincarnation.use.load.success");
    }

    public static boolean isRodReincarnation(ItemStack stack) {
        return stack != null && stack.getItem() == ModItems.rodReincarnation;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent(this.getTranslationKey() + ".info"));
        CompoundNBT tag = stack.getTag();
        String fileName = "";
        if (tag != null) {
            fileName = tag.getString(NBT_FILE);
        }
        if (fileName.isEmpty()) {
            tooltip.add(new TranslationTextComponent(this.getTranslationKey() + ".info.nofilename"));
        } else {
            tooltip.add(new TranslationTextComponent(this.getTranslationKey() + ".info.filename", fileName));
        }
    }
}