package quote.fsrod.common.item;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import quote.fsrod.common.item.utils.IItemTrackedPlayerInventory;

public class CharmUranusItem extends Item implements IItemTrackedPlayerInventory{

    public CharmUranusItem(Properties p) {
        super(p);
    }

    @SuppressWarnings("resource")
    public static void updatePlayerState(Player player){
        player.fallDistance = 0;
        if(!player.level.isClientSide) return;

        player.getAbilities().mayfly = true;
        player.getAbilities().flying = true;
        player.getAbilities().setFlyingSpeed(0.2f);

        if(Minecraft.getInstance().options.keyJump.isDown()){
            player.setDeltaMovement(new Vec3(player.getDeltaMovement().x, 1.0f, player.getDeltaMovement().z));
        }

        if(Minecraft.getInstance().options.keyShift.isDown()){
            player.setDeltaMovement(new Vec3(player.getDeltaMovement().x, -1.0f, player.getDeltaMovement().z));
        }

        if(player.zza == 0 && player.xxa == 0){
            player.setDeltaMovement(player.getDeltaMovement().multiply(0.5f, 1.0f, 0.5f));
        }
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
        updatePlayerState(player);
    }

    @Override
    public void onStopTracking(Player player, ItemStack stack) {
        resetPlayerState(player);
    }
}
