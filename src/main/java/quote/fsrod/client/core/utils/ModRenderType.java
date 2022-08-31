package quote.fsrod.client.core.utils;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import quote.fsrod.common.lib.LibMisc;

public class ModRenderType {

    public static final RenderType CLONE_SPACE = new CloneSpaceRenderType();
    public static final RenderType BOX = new BoxRenderType();

    private static class CloneSpaceRenderType extends RenderType{

        private CloneSpaceRenderType(){
            super(
                String.format("%s_%s", LibMisc.MOD_ID, "clone_space_type"),
                DefaultVertexFormat.BLOCK,
                VertexFormat.Mode.QUADS,
                2097152,
                true,
                true,
                () -> {
                    RenderType.translucent().setupRenderState();

                    RenderSystem.disableDepthTest();
                    RenderSystem.enableBlend();
                    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 0.5f);
                },
                () -> {
                    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                    RenderSystem.disableBlend();
                    RenderSystem.enableDepthTest();

                    RenderType.translucent().clearRenderState();
                }
            );
        }
    }

    private static class BoxRenderType extends RenderType{

        private BoxRenderType(){
            super(
                String.format("%s_%s", LibMisc.MOD_ID, "box_type"),
                DefaultVertexFormat.POSITION_COLOR_NORMAL,
                VertexFormat.Mode.QUADS,
                256,
                false,
                false,
                () -> {
                    RenderSystem.setShader(GameRenderer::getPositionColorShader);
                    RenderSystem.disableDepthTest();
                    RenderSystem.enableBlend();
                    RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
                },
                () -> {
                    RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                    RenderSystem.disableBlend();
                    RenderSystem.enableDepthTest();
                }
            );
        }
    }
}
