package quote.fsrod.client.core.handler;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import quote.fsrod.common.core.helper.rod.RodCloneHelper;
import quote.fsrod.common.core.helper.rod.RodRecollectionHelper;
import quote.fsrod.common.item.rod.RodCloneItem;
import quote.fsrod.common.item.rod.RodRecollectionItem;
import quote.fsrod.common.item.rod.RodTransferItem;

public class ModRenderLevelHandler {
    
    @SubscribeEvent
    @SuppressWarnings("resource")
    public void onDrawLevelPost(RenderLevelLastEvent event){
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        Vec3 vec = camera.getPosition();
        double x0 = vec.x;
        double y0 = vec.y;
        double z0 = vec.z;

        event.getPoseStack().pushPose();
        event.getPoseStack().translate(-x0, -y0, -z0);

        drawFakeBlocks(event);
        Minecraft.getInstance().renderBuffers().bufferSource().endBatch();

        event.getPoseStack().pushPose();
    }

    @SuppressWarnings("resource")
    private void drawFakeBlocks(RenderLevelLastEvent event){
        Player player = Minecraft.getInstance().player;
        ItemStack stackMainHand = player.getItemInHand(InteractionHand.MAIN_HAND);
        float partialTicks = event.getPartialTick();

        if(RodCloneItem.isItemOf(stackMainHand) || RodTransferItem.isItemOf(stackMainHand)){
            RodCloneHelper.drawFakeBlocks(player, stackMainHand, partialTicks, event);
        }
        if(RodRecollectionItem.isItemOf(stackMainHand)){
            RodRecollectionHelper.drawFakeBlocks(player, stackMainHand, partialTicks, event);
        }
    }
}
