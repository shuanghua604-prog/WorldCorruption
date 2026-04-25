package com.corruption;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.opengl.GL11;

public class CorruptionRenderHandler {

    private Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (mc.theWorld == null || mc.thePlayer == null) return;

        EntityLivingBase player = mc.thePlayer;
        int playerChunkX = MathHelper.floor_double(player.posX) >> 4;
        int playerChunkZ = MathHelper.floor_double(player.posZ) >> 4;

        // 获取客户端的腐化数据（需要同步）
        ChunkCorruptionClient clientData = ChunkCorruptionClient.getInstance();
        long worldTime = mc.theWorld.getTotalWorldTime();

        // 遍历视距范围内的区块
        int renderDist = mc.gameSettings.renderDistanceChunks;
        for (int dx = -renderDist; dx <= renderDist; dx++) {
            for (int dz = -renderDist; dz <= renderDist; dz++) {
                int cx = playerChunkX + dx;
                int cz = playerChunkZ + dz;

                byte type = clientData.getType(cx, cz);
                if (type < 0 || type >= 5) continue; // 正常区块或无效类型
                if (type == 2) continue; // 红色是实体专用，跳过区块渲染

                int color = CorruptionColors.getFlickerColor(type, worldTime, cx, cz);
                if (color < 0) continue;

                float[] rgb = CorruptionColors.toFloatRGB(color);

                // 在区块位置绘制半透明覆盖层
                renderCorruptionOverlay(cx, cz, rgb[0], rgb[1], rgb[2], event.partialTicks);
            }
        }
    }

    private void renderCorruptionOverlay(int chunkX, int chunkZ, 
                                          float r, float g, float b, float partialTicks) {
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(r, g, b, 0.35f); // 35% 透明度

        double px = mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * partialTicks;
        double py = mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * partialTicks;
        double pz = mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * partialTicks;

        GL11.glTranslated(-px, -py, -pz);

        // 绘制区块覆盖平面 (Y=64 处)
        double x1 = chunkX << 4;
        double z1 = chunkZ << 4;
        double x2 = x1 + 16;
        double z2 = z1 + 16;
        double y = 64;

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex3d(x1, y, z1);
        GL11.glVertex3d(x2, y, z1);
        GL11.glVertex3d(x2, y, z2);
        GL11.glVertex3d(x1, y, z2);
        GL11.glEnd();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }
}