package quote.fsrod.common.item.rod;

import java.util.List;
import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent.MouseScrollEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import quote.fsrod.common.core.collision.CollisionCylinder;
import quote.fsrod.common.core.network.ModPacketHandler;
import quote.fsrod.common.core.network.item.CPacketItemUpdateTag;
import quote.fsrod.common.item.ModItems;
import quote.fsrod.common.item.utils.IItemHasUUID;

public class RodTeleportationItem extends Item implements IItemHasUUID{
    
    public static final String TAG_REACH_DISTANCE = "reach_distance";

    public RodTeleportationItem(Properties p) {
        super(p);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if(
            level.isClientSide ||
            hand != InteractionHand.MAIN_HAND ||
            stack == null
        ){
            return super.use(level, player, hand);
        }

        if(player.isShiftKeyDown()){
            shortTeleport(level, player, stack);
        }
        else{
            player.startUsingItem(hand);
        }

        return super.use(level, player, hand);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeLeft) {
        if (level.isClientSide) return;
        if (livingEntity instanceof Player){
            Player player = (Player)livingEntity;
            float strength = this.getUseDuration(stack) - timeLeft;
            float amplifier = 10.0f;
            strength *= amplifier;
            
            Vec3 near = player.position().add(0, player.getEyeHeight(), 0);
            Vec3 end = near.add(player.getLookAngle().scale(strength));
            BlockHitResult blockHitResult = level.clip(new ClipContext(near, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
            double nearestDistance = Float.MAX_VALUE;
            if(blockHitResult != null && blockHitResult.getType() == Type.BLOCK && blockHitResult.getLocation() != null){
                nearestDistance = blockHitResult.getLocation().distanceTo(near);
            }

            CollisionCylinder collision = new CollisionCylinder(near, end, 0.1f);
            List<Entity> collidedEntityList = collision.checkCollisionWithEntities(level, player, e -> e instanceof LivingEntity);
            Entity nearestEntity = null;
            for (Entity e : collidedEntityList) {
                double d = e.position().distanceTo(near);
                if(d < nearestDistance){
                    nearestEntity = e;
                    nearestDistance = d;
                }
            }

            if(nearestEntity != null){
                Entity entityHit = nearestEntity;
                if(entityHit instanceof LivingEntity){
                    Vec3 playerPrevPos = player.position();

                    teleport(player, entityHit.position());
                    teleport((LivingEntity)entityHit, playerPrevPos);
                    spawnFX(level, entityHit.position());
                    spawnFX(level, player.position());
                    sound(level, entityHit.position());
                    sound(level, player.position());
                }
            }
            else if(blockHitResult != null && blockHitResult.getType() == Type.BLOCK && blockHitResult.getBlockPos() != null && blockHitResult.getDirection() != null){
                BlockPos blockPos = blockHitResult.getBlockPos();
                BlockPos offsetBlockPos = blockPos.offset(blockHitResult.getDirection().getNormal());
                Vec3 teleportTo = new Vec3(offsetBlockPos.getX(), offsetBlockPos.getY(), offsetBlockPos.getZ()).add(0.5, 0, 0.5);

                Vec3 playerPrevPos = player.position();
                teleport(player, teleportTo);
                spawnFX(level, playerPrevPos);
                sound(level, teleportTo);
            }
        }
        super.releaseUsing(stack, level, livingEntity, timeLeft);
    }

    private void shortTeleport(Level level, Player player, ItemStack stack){
        float strength = 1.0f;
        CompoundTag tag = stack.getOrCreateTag();
        float amplifier = tag.getInt(TAG_REACH_DISTANCE);

        strength *= amplifier;
        Vec3 near = player.position().add(0, player.getEyeHeight(), 0);
        Vec3 direction = player.getLookAngle();
        
        Vec3 prevPos = player.position();
        Vec3 teleportPos = null;
        for(float d = 0; d < strength; d++){
            Vec3 vec = direction.scale(d);
            Vec3 tryVec = near.add(vec);
            BlockPos blockPos = new BlockPos(tryVec);
            BlockState blockState = level.getBlockState(blockPos);
            if(blockState.getBlock().defaultDestroyTime() < 0) break;

            player.setPos(tryVec);
            if(!obstructed(level, player)){
                teleportPos = player.position();
            }
        }
        if(teleportPos == null){
            player.setPos(prevPos);
        }
        else{
            teleport(player, teleportPos);
            spawnFX(level, prevPos);
            sound(level, teleportPos);
        }
    }

    private boolean obstructed(Level level, Player player){
        for(VoxelShape voxelshape : level.getBlockCollisions(player, player.getBoundingBox())) {
            if (!voxelshape.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private void teleport(LivingEntity entity, Vec3 to){
        entity.setPos(to.x, to.y, to.z);
        EntityTeleportEvent.EnderEntity event = new EntityTeleportEvent.EnderEntity(entity, to.x, to.y, to.z);
        if (MinecraftForge.EVENT_BUS.post(event)) return;
        entity.teleportTo(event.getTargetX(), event.getTargetY(), event.getTargetZ());
        entity.fallDistance = 0.0F;
    }

    private void spawnFX(Level level, Vec3 pos){
        // ModPacketHandler.INSTANCE.sendToAllAround(new PacketTeleportParticle(x, y, z), new TargetPoint(level.provider.getDimension(), x, y, z, 32));
    }

    private void sound(Level level, Vec3 pos){
        level.playSound((Player)null, pos.x, pos.y, pos.z, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.2F, 1.0F);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int itemSlot, boolean isSelected) {
        if(!level.isClientSide){
            CompoundTag tag = stack.getOrCreateTag();
            IItemHasUUID.getOrCreateUUID(stack);
            int oldReach = tag.getInt(TAG_REACH_DISTANCE);
            int newReach = Mth.clamp(oldReach, 8, 24);
            tag.putInt(TAG_REACH_DISTANCE, newReach);
        }
        super.inventoryTick(stack, level, entity, itemSlot, isSelected);
    }


    public static boolean isItemOf(ItemStack stack){
        return stack.getItem() == ModItems.rodTeleportation;
    }

    @OnlyIn(Dist.CLIENT)
    public static void onMouseScrollEvent(MouseScrollEvent event, ItemStack stack, Player player){
        if (event.getScrollDelta() != 0 && player.isShiftKeyDown()){
            if(!IItemHasUUID.hasUUID(stack)) return;
            CompoundTag tag = new CompoundTag();
            CompoundTag tagStack = stack.getOrCreateTag();
            UUID uuid = IItemHasUUID.getOrCreateUUID(stack);

            int oldReach = tagStack.getInt(TAG_REACH_DISTANCE);
            int newReach = (int)Mth.clamp(oldReach + event.getScrollDelta() * 2, 8, 24);
            tag.putInt(TAG_REACH_DISTANCE, newReach);

            ModPacketHandler.CHANNEL.sendToServer(new CPacketItemUpdateTag(tag, uuid, CPacketItemUpdateTag.Operation.ADD));
            event.setCanceled(true);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag tooltipFlag) {
        CompoundTag tag = stack.getTag();
        if (tag != null) {
            float reach = tag.getFloat(TAG_REACH_DISTANCE);
            tooltip.add(new TranslatableComponent(this.getDescriptionId() + ".info.1", "§l§bx" + reach));
        }
        tooltip.add(new TranslatableComponent(this.getDescriptionId() + ".info.2"));
        tooltip.add(new TranslatableComponent(this.getDescriptionId() + ".info.3"));
        tooltip.add(new TranslatableComponent(this.getDescriptionId() + ".info.4"));
    }
}
