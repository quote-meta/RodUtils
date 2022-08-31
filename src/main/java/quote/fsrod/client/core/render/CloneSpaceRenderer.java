package quote.fsrod.client.core.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import quote.fsrod.client.core.utils.ModBufferSource;
import quote.fsrod.client.core.utils.ModRenderType;
import quote.fsrod.common.core.helper.rod.SpaceReader;

public class CloneSpaceRenderer {

    public static void renderSpace(Level level, BlockPos blockPosNear, BlockPos blockPosEnd, Direction direction, BlockPos blockPosBase, RenderLevelLastEvent event){
        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
        BufferSource bufferSource = ModBufferSource.get();
        PoseStack poseStack = event.getPoseStack();
        Rotation rotation = SpaceReader.getRotation(SpaceReader.getFacingAABB(blockPosNear, blockPosEnd), direction);

        RenderSystem.disableTexture();
        RenderSystem.clearColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);

        for(BlockPos src: BlockPos.betweenClosed(blockPosNear, blockPosEnd)){
            if(!level.isEmptyBlock(src)){
                BlockPos srcRelative = src.subtract(blockPosNear);
                BlockPos posRotated = srcRelative.rotate(rotation);
                BlockPos dst = blockPosBase.offset(posRotated);
                if(!level.isEmptyBlock(dst)) continue;
                
                int brightness = LevelRenderer.getLightColor(level, dst);

                BlockState blockState = level.getBlockState(src).rotate(level, src, rotation);
                IModelData modelData = ModelDataManager.getModelData(level, src);

                poseStack.pushPose();
                poseStack.translate(dst.getX(), dst.getY(), dst.getZ());

                if(modelData != null){
                    blockRenderer.getModelRenderer().renderModel(
                        poseStack.last(),
                        bufferSource.getBuffer(ModRenderType.CLONE_SPACE),
                        blockState,
                        blockRenderer.getBlockModel(blockState),
                        1.0f, 1.0f, 1.0f,
                        brightness, OverlayTexture.NO_OVERLAY, modelData
                    );
                }
                else{
                    blockRenderer.getModelRenderer().renderModel(
                        poseStack.last(),
                        bufferSource.getBuffer(ModRenderType.CLONE_SPACE),
                        blockState,
                        blockRenderer.getBlockModel(blockState),
                        1.0f, 1.0f, 1.0f,
                        brightness, OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE
                    );
                }
                poseStack.popPose();
            }
        }
        bufferSource.endBatch(ModRenderType.CLONE_SPACE);
    }
}
