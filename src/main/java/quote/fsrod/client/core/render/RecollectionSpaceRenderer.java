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
import net.minecraftforge.client.model.data.EmptyModelData;
import quote.fsrod.client.core.utils.ModBufferSource;
import quote.fsrod.client.core.utils.ModRenderType;
import quote.fsrod.common.core.helper.rod.SpaceReader;
import quote.fsrod.common.structure.IStructure;

public class RecollectionSpaceRenderer {

    public static void renderSpace(Level level, IStructure structure, Direction direction, BlockPos blockPosBase, RenderLevelLastEvent event){
        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
        BufferSource bufferSource = ModBufferSource.get();
        PoseStack poseStack = event.getPoseStack();
        Rotation rotation = SpaceReader.getRotation(Direction.EAST, direction);

        RenderSystem.disableTexture();
        RenderSystem.clearColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);

        int sizeX = structure.getSizeX();
        int sizeY = structure.getSizeY();
        int sizeZ = structure.getSizeZ();
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                for (int k = 0; k < sizeZ; k++) {
                    BlockState blockState = structure.getStateAt(i, j, k, rotation);

                    if(blockState.isAir()) continue;

                    BlockPos srcRelative = new BlockPos(i, j, k);
                    BlockPos posRotated = srcRelative.rotate(rotation);
                    BlockPos dst = blockPosBase.offset(posRotated);
                    if(!level.isEmptyBlock(dst)) continue;
                    int brightness = LevelRenderer.getLightColor(level, dst);
    
                    poseStack.pushPose();
                    poseStack.translate(dst.getX(), dst.getY(), dst.getZ());
    
                    blockRenderer.getModelRenderer().renderModel(
                        poseStack.last(),
                        bufferSource.getBuffer(ModRenderType.CLONE_SPACE),
                        blockState,
                        blockRenderer.getBlockModel(blockState),
                        1.0f, 1.0f, 1.0f,
                        brightness, OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE
                    );
                    poseStack.popPose();
                }
            }
        }
        bufferSource.endBatch(ModRenderType.CLONE_SPACE);
    }
}
