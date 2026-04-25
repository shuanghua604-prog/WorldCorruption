package com.corruption;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderLivingEvent;
import org.lwjgl.opengl.GL11;

public class EntityRenderCorruption {

    private Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onRenderLivingPre(RenderLivingEvent.Pre event) {
        EntityLivingBase entity = event.entity;

        // 检查实体所在区块是否腐化
        int cx = entity.chunkCoordX;
        int cz = entity.chunkCoordZ;

        ChunkCorruptionClient clientData = ChunkCorruptionClient.getInstance();
        byte type = clientData.getType(cx, cz);

        // 红色腐化 (type 2) 或任何腐化区内的实体都可能受影响
        if (type == 2 || (type >= 0 && type != 2 && mc.theWorld.rand.nextFloat() < 0.3f)) {
            // 强制纯色渲染
            GL11.glPushMatrix();
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            long worldTime = mc.theWorld.getTotalWorldTime();
            int color = CorruptionColors.getFlickerColor(2, worldTime, cx, cz); // 用红色
            float[] rgb = CorruptionColors.toFloatRGB(color);

            GL11.glColor4f(rgb[0], rgb[1], rgb[2], 0.9f);
        }
    }

    @SubscribeEvent
    public void onRenderLivingPost(RenderLivingEvent.Post event) {
        // 恢复渲染状态
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor4f(1f, 1f, 1f, 1f);
        GL11.glPopMatrix();
    }
}