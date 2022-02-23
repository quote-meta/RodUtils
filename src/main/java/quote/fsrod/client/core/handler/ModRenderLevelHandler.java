package quote.fsrod.client.core.handler;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import quote.fsrod.common.core.helper.rod.RodCloneHelper;
import quote.fsrod.common.core.helper.rod.RodRecollectionHelper;
import quote.fsrod.common.item.rod.RodCloneItem;
import quote.fsrod.common.item.rod.RodRecollectionItem;
import quote.fsrod.common.item.rod.RodTransferItem;

public class ModRenderLevelHandler {
    
    @SubscribeEvent
    public void onDrawLevelPost(RenderLevelLastEvent event){
        PoseStack stack = event.getPoseStack();
        PoseStack posestack = RenderSystem.getModelViewStack();
        posestack.pushPose();
        posestack.mulPoseMatrix(stack.last().pose());
        RenderSystem.applyModelViewMatrix();

        drawFakeBlocks(event);

        posestack.popPose();
        RenderSystem.applyModelViewMatrix();
    }

    @SuppressWarnings("resource")
    private void drawFakeBlocks(RenderLevelLastEvent event){
        Player player = Minecraft.getInstance().player;
        ItemStack stackMainHand = player.getItemInHand(InteractionHand.MAIN_HAND);
        float partialTicks = event.getPartialTick();
        if(RodCloneItem.isItemOf(stackMainHand) || RodTransferItem.isItemOf(stackMainHand)){
            RodCloneHelper.drawFakeBlocks(player, stackMainHand, partialTicks);
        }
        if(RodRecollectionItem.isItemOf(stackMainHand)){
            RodRecollectionHelper.drawFakeBlocks(player, stackMainHand, partialTicks);
        }
    }
}
