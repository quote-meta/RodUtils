package quote.fsrod.common.item;

import java.util.List;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent.MouseScrollEvent;
import quote.fsrod.common.core.network.ModPacketHandler;
import quote.fsrod.common.core.network.item.CPacketItemUpdateTag;
import quote.fsrod.common.item.utils.IItemHasUUID;
import quote.fsrod.common.item.utils.IItemTrackedPlayerInventory;

public class CharmUranusItem extends Item implements IItemTrackedPlayerInventory, IItemHasUUID{

    public static final String TAG_VELOCITY = "uranus_velocity";
    public static final String TAG_IS_ALWAYS = "uranus_is_always";

    public CharmUranusItem(Properties p) {
        super(p);
    }

    @SuppressWarnings("resource")
    public static void updatePlayerState(Player player, ItemStack stack){
        player.fallDistance = 0;
        if(!player.level.isClientSide) return;

        player.getAbilities().mayfly = true;
        if(stack.getOrCreateTag().getBoolean(TAG_IS_ALWAYS)){
            player.getAbilities().flying = true;
        }
        if(!player.getAbilities().flying) return;

        float speedModifier = stack.getOrCreateTag().getFloat(TAG_VELOCITY);
        if(Minecraft.getInstance().options.keySprint.isDown()){
            speedModifier *= 2f;
        }

        player.getAbilities().setFlyingSpeed(0.05f * speedModifier);

        Vec3 deltaMovement = player.getDeltaMovement();
        if(Minecraft.getInstance().options.keyJump.isDown()){
            deltaMovement.add(0, 0.2f * speedModifier, 0);
        }

        if(Minecraft.getInstance().options.keyShift.isDown()){
            deltaMovement.add(0, -0.2f * speedModifier, 0);
        }

        if(player.zza == 0 && player.xxa == 0){
            deltaMovement.multiply(0.5f, 1.0f, 0.5f);
        }

        player.setDeltaMovement(deltaMovement);
        player.onUpdateAbilities();
    }

    public static void resetPlayerState(Player player){
        if(!player.level.isClientSide) return;

        player.getAbilities().mayfly = false;
        player.getAbilities().flying = false;
        player.getAbilities().setFlyingSpeed(0.05f);
        player.onUpdateAbilities();
    }

    @Override
    public void trakingTick(Player player, ItemStack stack) {
        updatePlayerState(player, stack);
    }

    @Override
    public void onStopTracking(Player player, ItemStack stack) {
        resetPlayerState(player);
    }

    //
    // ===================== Setting ========================
    //

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int itemSlot, boolean isSelected) {
        if(!level.isClientSide){
            CompoundTag tag = stack.getOrCreateTag();
            IItemHasUUID.getOrCreateUUID(stack);
            int oldVelocity = tag.getInt(TAG_VELOCITY);
            int newVelocity = Mth.clamp(oldVelocity, 1, 10);
            tag.putInt(TAG_VELOCITY, newVelocity);
        }
        super.inventoryTick(stack, level, entity, itemSlot, isSelected);
    }

    @OnlyIn(Dist.CLIENT)
    public static void onMouseScrollEvent(MouseScrollEvent event, ItemStack stack, Player player){
        if (event.getScrollDelta() != 0 && player.isShiftKeyDown()){
            if(!IItemHasUUID.hasUUID(stack)) return;
            CompoundTag tag = new CompoundTag();
            CompoundTag tagStack = stack.getOrCreateTag();
            UUID uuid = IItemHasUUID.getOrCreateUUID(stack);

            int oldVelocity = tagStack.getInt(TAG_VELOCITY);
            int newVelocity = (int)Mth.clamp(oldVelocity + event.getScrollDelta(), 1, 10);
            tag.putInt(TAG_VELOCITY, newVelocity);

            ModPacketHandler.CHANNEL.sendToServer(new CPacketItemUpdateTag(tag, uuid, CPacketItemUpdateTag.Operation.ADD));
            event.setCanceled(true);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide && isItemOf(player.getItemInHand(hand))){
            CompoundTag tag = player.getItemInHand(hand).getOrCreateTag();
            boolean isAlways = tag.getBoolean(TAG_IS_ALWAYS);
            tag.putBoolean(TAG_IS_ALWAYS, !isAlways);
        }

        return super.use(level, player, hand);
    }

    public static boolean isItemOf(ItemStack stack) {
        return stack != null && stack.getItem() == ModItems.charmUranus;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag tooltipFlag) {
        CompoundTag tag = stack.getTag();
        if (tag != null) {
            boolean isAlways = tag.getBoolean(TAG_IS_ALWAYS);
            float velocity = tag.getFloat(TAG_VELOCITY);
            tooltip.add(new TranslatableComponent(this.getDescriptionId() + ".info.1", isAlways ? "§l§6ON": "§l§bOFF"));
            tooltip.add(new TranslatableComponent(this.getDescriptionId() + ".info.2", "§l§bx" + velocity));
        }
        tooltip.add(new TranslatableComponent(this.getDescriptionId() + ".info.3"));
        tooltip.add(new TranslatableComponent(this.getDescriptionId() + ".info.4"));
    }
}
