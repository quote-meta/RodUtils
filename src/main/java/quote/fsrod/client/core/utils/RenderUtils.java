package quote.fsrod.client.core.utils;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Vector3f;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class RenderUtils {
    
    @SuppressWarnings("resource")
    public static void renderFakeBlockFX(BlockPos pos){
        RenderSystem.disableTexture();
        RenderSystem.clearColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);

        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();

        drawFakeBlockFX(tesselator, buffer, pos, camera);
    }

    @SuppressWarnings("resource")
    public static void renderFakeFrameFX(AABB aabb){
        RenderSystem.disableTexture();
        RenderSystem.clearColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);

        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        Vec3 posNear = new Vec3(aabb.minX, aabb.minY, aabb.minZ);
        Vec3 posEnd = new Vec3(aabb.maxX, aabb.maxY, aabb.maxZ);
        drawBoxFrameFX(tesselator, buffer, posNear, posEnd, camera);
    }

    private static void drawFakeBlockFX(Tesselator tesselator, BufferBuilder builder, BlockPos pos, Camera camera){
        Vec3 vec3 = camera.getPosition();
        float m = -0.001F;
        float p = 1.001F;
        float baseX = (float)(pos.getX() - vec3.x);
        float baseY = (float)(pos.getY() - vec3.y);
        float baseZ = (float)(pos.getZ() - vec3.z);

        Vector3f[] avector3f = new Vector3f[]{
            new Vector3f(m, m, m),
            new Vector3f(m, p, m),
            new Vector3f(p, m, m),
            new Vector3f(p, p, m),
            new Vector3f(m, m, p),
            new Vector3f(m, p, p),
            new Vector3f(p, m, p),
            new Vector3f(p, p, p),
        };

        for (int i = 0; i < avector3f.length; i++) {
            Vector3f vector3f = avector3f[i];
            vector3f.add(baseX, baseY, baseZ);
        }
        
        int r = 100;
        int g = 100;
        int b = 200;
        int a = 200;

        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        builder.vertex(avector3f[1].x(), avector3f[1].y(), avector3f[1].z()).color(r, g, b, a).endVertex();
        builder.vertex(avector3f[3].x(), avector3f[3].y(), avector3f[3].z()).color(r, g, b, a).endVertex();
        builder.vertex(avector3f[2].x(), avector3f[2].y(), avector3f[2].z()).color(r, g, b, a).endVertex();
        builder.vertex(avector3f[0].x(), avector3f[0].y(), avector3f[0].z()).color(r, g, b, a).endVertex();
        builder.vertex(avector3f[4].x(), avector3f[4].y(), avector3f[4].z()).color(r, g, b, a).endVertex();
        builder.vertex(avector3f[6].x(), avector3f[6].y(), avector3f[6].z()).color(r, g, b, a).endVertex();
        builder.vertex(avector3f[7].x(), avector3f[7].y(), avector3f[7].z()).color(r, g, b, a).endVertex();
        builder.vertex(avector3f[5].x(), avector3f[5].y(), avector3f[5].z()).color(r, g, b, a).endVertex();
        builder.vertex(avector3f[0].x(), avector3f[0].y(), avector3f[0].z()).color(r, g, b, a).endVertex();
        builder.vertex(avector3f[2].x(), avector3f[2].y(), avector3f[2].z()).color(r, g, b, a).endVertex();
        builder.vertex(avector3f[6].x(), avector3f[6].y(), avector3f[6].z()).color(r, g, b, a).endVertex();
        builder.vertex(avector3f[4].x(), avector3f[4].y(), avector3f[4].z()).color(r, g, b, a).endVertex();
        builder.vertex(avector3f[5].x(), avector3f[5].y(), avector3f[5].z()).color(r, g, b, a).endVertex();
        builder.vertex(avector3f[7].x(), avector3f[7].y(), avector3f[7].z()).color(r, g, b, a).endVertex();
        builder.vertex(avector3f[3].x(), avector3f[3].y(), avector3f[3].z()).color(r, g, b, a).endVertex();
        builder.vertex(avector3f[1].x(), avector3f[1].y(), avector3f[1].z()).color(r, g, b, a).endVertex();
        builder.vertex(avector3f[4].x(), avector3f[4].y(), avector3f[4].z()).color(r, g, b, a).endVertex();
        builder.vertex(avector3f[5].x(), avector3f[5].y(), avector3f[5].z()).color(r, g, b, a).endVertex();
        builder.vertex(avector3f[1].x(), avector3f[1].y(), avector3f[1].z()).color(r, g, b, a).endVertex();
        builder.vertex(avector3f[0].x(), avector3f[0].y(), avector3f[0].z()).color(r, g, b, a).endVertex();
        builder.vertex(avector3f[2].x(), avector3f[2].y(), avector3f[2].z()).color(r, g, b, a).endVertex();
        builder.vertex(avector3f[3].x(), avector3f[3].y(), avector3f[3].z()).color(r, g, b, a).endVertex();
        builder.vertex(avector3f[7].x(), avector3f[7].y(), avector3f[7].z()).color(r, g, b, a).endVertex();
        builder.vertex(avector3f[6].x(), avector3f[6].y(), avector3f[6].z()).color(r, g, b, a).endVertex();
        tesselator.end();
    }

    private static void drawBoxFrameFX(Tesselator tesselator, BufferBuilder builder, Vec3 vecNear, Vec3 vecEnd, Camera camera){
        Vec3 vec3 = camera.getPosition();
        float baseX = (float)(vecNear.x() - vec3.x);
        float baseY = (float)(vecNear.y() - vec3.y);
        float baseZ = (float)(vecNear.z() - vec3.z);

        float mx = 0;
        float my = 0;
        float mz = 0;
        float px = (float)(vecEnd.x() - vecNear.x());
        float py = (float)(vecEnd.y() - vecNear.y());
        float pz = (float)(vecEnd.z() - vecNear.z());

        Vector3f[] avector3f = new Vector3f[]{
            new Vector3f(mx, my, mz),
            new Vector3f(mx, py, mz),
            new Vector3f(px, my, mz),
            new Vector3f(px, py, mz),
            new Vector3f(mx, my, pz),
            new Vector3f(mx, py, pz),
            new Vector3f(px, my, pz),
            new Vector3f(px, py, pz),
        };

        for (int i = 0; i < avector3f.length; i++) {
            Vector3f vector3f = avector3f[i];
            vector3f.add(baseX, baseY, baseZ);
        }
        
        int r = 100;
        int g = 200;
        int b = 100;
        int a = 200;
        
        builder.begin(VertexFormat.Mode.DEBUG_LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);
        builder.vertex(avector3f[0].x(), avector3f[0].y(), avector3f[0].z()).color(r, g, b, a).endVertex();
        builder.vertex(avector3f[2].x(), avector3f[2].y(), avector3f[2].z()).color(r, g, b, a).endVertex();
        builder.vertex(avector3f[6].x(), avector3f[6].y(), avector3f[6].z()).color(r, g, b, a).endVertex();
        builder.vertex(avector3f[4].x(), avector3f[4].y(), avector3f[4].z()).color(r, g, b, a).endVertex();
        builder.vertex(avector3f[0].x(), avector3f[0].y(), avector3f[0].z()).color(r, g, b, a).endVertex();
        tesselator.end();
        builder.begin(VertexFormat.Mode.DEBUG_LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);
        builder.vertex(avector3f[1].x(), avector3f[1].y(), avector3f[1].z()).color(r, g, b, a).endVertex();
        builder.vertex(avector3f[3].x(), avector3f[3].y(), avector3f[3].z()).color(r, g, b, a).endVertex();
        builder.vertex(avector3f[7].x(), avector3f[7].y(), avector3f[7].z()).color(r, g, b, a).endVertex();
        builder.vertex(avector3f[5].x(), avector3f[5].y(), avector3f[5].z()).color(r, g, b, a).endVertex();
        builder.vertex(avector3f[1].x(), avector3f[1].y(), avector3f[1].z()).color(r, g, b, a).endVertex();
        tesselator.end();
        builder.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
        builder.vertex(avector3f[0].x(), avector3f[0].y(), avector3f[0].z()).color(r, g, b, a).endVertex();
        builder.vertex(avector3f[1].x(), avector3f[1].y(), avector3f[1].z()).color(r, g, b, a).endVertex();
        builder.vertex(avector3f[2].x(), avector3f[2].y(), avector3f[2].z()).color(r, g, b, a).endVertex();
        builder.vertex(avector3f[3].x(), avector3f[3].y(), avector3f[3].z()).color(r, g, b, a).endVertex();
        builder.vertex(avector3f[6].x(), avector3f[6].y(), avector3f[6].z()).color(r, g, b, a).endVertex();
        builder.vertex(avector3f[7].x(), avector3f[7].y(), avector3f[7].z()).color(r, g, b, a).endVertex();
        builder.vertex(avector3f[4].x(), avector3f[4].y(), avector3f[4].z()).color(r, g, b, a).endVertex();
        builder.vertex(avector3f[5].x(), avector3f[5].y(), avector3f[5].z()).color(r, g, b, a).endVertex();
        tesselator.end();
    }
}
