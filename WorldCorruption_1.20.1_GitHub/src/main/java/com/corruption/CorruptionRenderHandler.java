package com.corruption;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.joml.Matrix4f;

public class CorruptionRenderHandler {
    private Minecraft mc = Minecraft.getInstance();

    @SubscribeEvent
    public void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;
        if (mc.level == null || mc.player == null) return;

        Player player = mc.player;
        int playerChunkX = player.blockPosition().getX() >> 4;
        int playerChunkZ = player.blockPosition().getZ() >> 4;

        ChunkCorruptionClient clientData = ChunkCorruptionClient.getInstance();
        long worldTime = mc.level.getGameTime();
        int renderDist = mc.options.renderDistance().get();

        PoseStack poseStack = event.getPoseStack();
        Matrix4f projectionMatrix = event.getProjectionMatrix();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();

        for (int dx = -renderDist; dx <= renderDist; dx++) {
            for (int dz = -renderDist; dz <= renderDist; dz++) {
                int cx = playerChunkX + dx;
                int cz = playerChunkZ + dz;

                byte type = clientData.getType(cx, cz);
                if (type < 0 || type >= 5 || type == 2) continue;

                int color = CorruptionColors.getFlickerColor(type, worldTime, cx, cz);
                if (color < 0) continue;

                float[] rgb = CorruptionColors.toFloatRGB(color);
                renderChunkOverlay(poseStack, cx, cz, rgb[0], rgb[1], rgb[2], player);
            }
        }

        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    private void renderChunkOverlay(PoseStack poseStack, int chunkX, int chunkZ, 
                                     float r, float g, float b, Player player) {
        poseStack.pushPose();

        double camX = player.getX();
        double camY = player.getY();
        double camZ = player.getZ();

        poseStack.translate(-camX, -camY, -camZ);

        float x1 = chunkX << 4;
        float z1 = chunkZ << 4;
        float x2 = x1 + 16;
        float z2 = z1 + 16;
        float y = 64;

        BufferBuilder builder = Tesselator.getInstance().getBuilder();
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        builder.vertex(poseStack.last().pose(), x1, y, z1).color(r, g, b, 0.35f).endVertex();
        builder.vertex(poseStack.last().pose(), x2, y, z1).color(r, g, b, 0.35f).endVertex();
        builder.vertex(poseStack.last().pose(), x2, y, z2).color(r, g, b, 0.35f).endVertex();
        builder.vertex(poseStack.last().pose(), x1, y, z2).color(r, g, b, 0.35f).endVertex();

        BufferUploader.drawWithShader(builder.end());

        poseStack.popPose();
    }
}