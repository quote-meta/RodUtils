package quote.fsRod.client.core.handler;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import quote.fsRod.client.core.helper.RodCloneHelper;
import quote.fsRod.client.core.helper.RodReincarnationHelper;
import quote.fsRod.common.RodUtils;
import quote.fsRod.common.item.rod.ItemRodClone;
import quote.fsRod.common.item.rod.ItemRodReincarnation;
import quote.fsRod.common.item.utils.IItemHasSplitNBTList;

@SideOnly(Side.CLIENT)
public class ModRenderWorldHandler {

    @SubscribeEvent
    public void onDrawWorldPost(RenderWorldLastEvent event){
        drawFakeBlocks(event);
    }

    private void drawFakeBlocks(RenderWorldLastEvent event){
        EntityPlayer player = RodUtils.proxy.getEntityPlayerInstance();
        ItemStack stackMainHand = player.getHeldItemMainhand();
        float partialTicks = event.getPartialTicks();
        if(ItemRodClone.isRodClone(stackMainHand) || ItemRodClone.isRodTransfer(stackMainHand)){

            Integer dimension = RodCloneHelper.getDimension(stackMainHand);
            BlockPos blockPosSeeing = RodCloneHelper.getBlockPosSeeing(stackMainHand, player, partialTicks);
            BlockPos blockPosNear = RodCloneHelper.getBlockPosNear(stackMainHand);
            BlockPos blockPosEnd = RodCloneHelper.getBlockPosEnd(stackMainHand);
            BlockPos blockPosScheduled = RodCloneHelper.getBlockPosScheduled(stackMainHand);

            if(dimension != null && dimension.equals(player.dimension) && blockPosNear != null){
                if(blockPosEnd != null){
                    AxisAlignedBB aabbDst = null;
                    if(blockPosScheduled != null){
                        EnumFacing facing = RodCloneHelper.getFacingScheduled(stackMainHand);
                        aabbDst = RodCloneHelper.getScheduledAABB(blockPosNear, blockPosEnd, facing, blockPosScheduled);
                        renderFakeBlockFX(blockPosScheduled);
                    }
                    else{
                        aabbDst = RodCloneHelper.getScheduledAABB(stackMainHand, player, blockPosSeeing);
                    }
                    if(aabbDst != null){
                        renderFakeFrameFX(aabbDst);
                    }
                    AxisAlignedBB aabbSrc = new AxisAlignedBB(blockPosNear, blockPosEnd).expand(1, 1, 1);
                    renderFakeFrameFX(aabbSrc);
                    renderFakeBlockFX(blockPosNear);
                }
                else{
                    AxisAlignedBB aabbSrc = new AxisAlignedBB(blockPosNear, blockPosSeeing).expand(1, 1, 1);
                    renderFakeFrameFX(aabbSrc);
                    renderFakeBlockFX(blockPosNear);
                }
            }
            
            renderFakeBlockFX(blockPosSeeing);
        }
        if(ItemRodReincarnation.isRodReincarnation(stackMainHand)){
            BlockPos blockPosSeeing = RodReincarnationHelper.getBlockPosSeeing(stackMainHand, player, partialTicks);
            BlockPos blockPosNear = RodReincarnationHelper.getBlockPosNear(stackMainHand);
            BlockPos blockPosEnd = RodReincarnationHelper.getBlockPosEnd(stackMainHand);
            BlockPos blockPosScheduled = RodReincarnationHelper.getBlockPosScheduled(stackMainHand);
            String fileName = RodReincarnationHelper.getFileName(stackMainHand);

            if(!fileName.isEmpty()){
                if(stackMainHand.getTagCompound().hasKey(ItemRodReincarnation.NBT_DATA) && !stackMainHand.getTagCompound().hasKey(IItemHasSplitNBTList.NBT_SPLIT)){
                    // load mode
                    if(blockPosScheduled != null){
                        BlockPos blockPosData = RodReincarnationHelper.getBlockPosData(stackMainHand);
                        if(blockPosData != null){
                            EnumFacing facing = RodReincarnationHelper.getFacingScheduled(stackMainHand);
                            AxisAlignedBB aabbDst = RodReincarnationHelper.getScheduledAABB(blockPosData, facing, blockPosScheduled);
                            renderFakeBlockFX(blockPosScheduled);
                            renderFakeFrameFX(aabbDst);
                        }
                    }
                }
                else{
                    // save mode
                    if(blockPosNear != null){
                        if(blockPosEnd != null){
                            AxisAlignedBB aabbSrc = new AxisAlignedBB(blockPosNear, blockPosEnd).expand(1, 1, 1);
                            renderFakeFrameFX(aabbSrc);
                            renderFakeBlockFX(blockPosNear);
                        }
                        else{
                            AxisAlignedBB aabbSrc = new AxisAlignedBB(blockPosNear, blockPosSeeing).expand(1, 1, 1);
                            renderFakeFrameFX(aabbSrc);
                            renderFakeBlockFX(blockPosNear);
                        }
                    }
                }
                
                renderFakeBlockFX(blockPosSeeing);
            }
        }
    }

    private void renderFakeBlockFX(BlockPos pos){
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE); //add
        GlStateManager.alphaFunc(516, 0.003921569F);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        double interpPosX = TileEntityRendererDispatcher.staticPlayerX;
        double interpPosY = TileEntityRendererDispatcher.staticPlayerY;
        double interpPosZ = TileEntityRendererDispatcher.staticPlayerZ;
        drawFakeBlockFX(tessellator, buffer, pos, interpPosX, interpPosY, interpPosZ);

        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.popMatrix();
    }

    private void renderFakeFrameFX(AxisAlignedBB aabb){
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE); //add
        GlStateManager.alphaFunc(516, 0.003921569F);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        double interpPosX = TileEntityRendererDispatcher.staticPlayerX;
        double interpPosY = TileEntityRendererDispatcher.staticPlayerY;
        double interpPosZ = TileEntityRendererDispatcher.staticPlayerZ;
        Vec3d posNear = new Vec3d(aabb.minX, aabb.minY, aabb.minZ);
        Vec3d posEnd = new Vec3d(aabb.maxX, aabb.maxY, aabb.maxZ);
        drawBoxFrameFX(tessellator, buffer, posNear, posEnd, interpPosX, interpPosY, interpPosZ);

        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.popMatrix();
    }

    private void drawFakeBlockFX(Tessellator tessellator, BufferBuilder buffer, BlockPos pos, double interpPosX, double interpPosY, double interpPosZ){
        double width = 1.01;
        double minX = pos.getX() - interpPosX;
        double maxX = pos.getX() + width - interpPosX;
        double minY = pos.getY() - interpPosY;
        double maxY = pos.getY() + width - interpPosY;
        double minZ = pos.getZ() - interpPosZ;
        double maxZ = pos.getZ() + width - interpPosZ;
        
        int r = 100;
        int g = 100;
        int b = 200;
        int a = 200;
        
        buffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(minX, maxY, minZ).color(r, g, b, a).endVertex();
        buffer.pos(maxX, maxY, minZ).color(r, g, b, a).endVertex();
        buffer.pos(maxX, minY, minZ).color(r, g, b, a).endVertex();
        buffer.pos(minX, minY, minZ).color(r, g, b, a).endVertex();
        buffer.pos(minX, minY, maxZ).color(r, g, b, a).endVertex();
        buffer.pos(maxX, minY, maxZ).color(r, g, b, a).endVertex();
        buffer.pos(maxX, maxY, maxZ).color(r, g, b, a).endVertex();
        buffer.pos(minX, maxY, maxZ).color(r, g, b, a).endVertex();
        buffer.pos(minX, minY, minZ).color(r, g, b, a).endVertex();
        buffer.pos(maxX, minY, minZ).color(r, g, b, a).endVertex();
        buffer.pos(maxX, minY, maxZ).color(r, g, b, a).endVertex();
        buffer.pos(minX, minY, maxZ).color(r, g, b, a).endVertex();
        buffer.pos(minX, maxY, maxZ).color(r, g, b, a).endVertex();
        buffer.pos(maxX, maxY, maxZ).color(r, g, b, a).endVertex();
        buffer.pos(maxX, maxY, minZ).color(r, g, b, a).endVertex();
        buffer.pos(minX, maxY, minZ).color(r, g, b, a).endVertex();
        buffer.pos(minX, minY, maxZ).color(r, g, b, a).endVertex();
        buffer.pos(minX, maxY, maxZ).color(r, g, b, a).endVertex();
        buffer.pos(minX, maxY, minZ).color(r, g, b, a).endVertex();
        buffer.pos(minX, minY, minZ).color(r, g, b, a).endVertex();
        buffer.pos(maxX, minY, minZ).color(r, g, b, a).endVertex();
        buffer.pos(maxX, maxY, minZ).color(r, g, b, a).endVertex();
        buffer.pos(maxX, maxY, maxZ).color(r, g, b, a).endVertex();
        buffer.pos(maxX, minY, maxZ).color(r, g, b, a).endVertex();
        tessellator.draw();
    }

    private void drawBoxFrameFX(Tessellator tessellator, BufferBuilder buffer, Vec3d vecNear, Vec3d vecEnd, double interpPosX, double interpPosY, double interpPosZ){
        double width = 1.01;
        double minX = Math.min(vecNear.x, vecEnd.x) - interpPosX;
        double maxX = Math.max(vecNear.x, vecEnd.x) - interpPosX;
        double minY = Math.min(vecNear.y, vecEnd.y) - interpPosY;
        double maxY = Math.max(vecNear.y, vecEnd.y) - interpPosY;
        double minZ = Math.min(vecNear.z, vecEnd.z) - interpPosZ;
        double maxZ = Math.max(vecNear.z, vecEnd.z) - interpPosZ;
        
        int r = 100;
        int g = 200;
        int b = 100;
        int a = 200;
        
        buffer.begin(3, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(minX, minY, minZ).color(r, g, b, a).endVertex();
        buffer.pos(maxX, minY, minZ).color(r, g, b, a).endVertex();
        buffer.pos(maxX, minY, maxZ).color(r, g, b, a).endVertex();
        buffer.pos(minX, minY, maxZ).color(r, g, b, a).endVertex();
        buffer.pos(minX, minY, minZ).color(r, g, b, a).endVertex();
        tessellator.draw();
        buffer.begin(3, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(minX, maxY, minZ).color(r, g, b, a).endVertex();
        buffer.pos(maxX, maxY, minZ).color(r, g, b, a).endVertex();
        buffer.pos(maxX, maxY, maxZ).color(r, g, b, a).endVertex();
        buffer.pos(minX, maxY, maxZ).color(r, g, b, a).endVertex();
        buffer.pos(minX, maxY, minZ).color(r, g, b, a).endVertex();
        tessellator.draw();
        buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(minX, minY, minZ).color(r, g, b, a).endVertex();
        buffer.pos(minX, maxY, minZ).color(r, g, b, a).endVertex();
        buffer.pos(maxX, minY, minZ).color(r, g, b, a).endVertex();
        buffer.pos(maxX, maxY, minZ).color(r, g, b, a).endVertex();
        buffer.pos(maxX, minY, maxZ).color(r, g, b, a).endVertex();
        buffer.pos(maxX, maxY, maxZ).color(r, g, b, a).endVertex();
        buffer.pos(minX, minY, maxZ).color(r, g, b, a).endVertex();
        buffer.pos(minX, maxY, maxZ).color(r, g, b, a).endVertex();
        tessellator.draw();
    }
}