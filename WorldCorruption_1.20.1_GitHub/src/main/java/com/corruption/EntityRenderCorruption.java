package com.corruption;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EntityRenderCorruption {
    private Minecraft mc = Minecraft.getInstance();

    @SubscribeEvent
    public void onRenderLivingPre(RenderLivingEvent.Pre<?, ?> event) {
        LivingEntity entity = event.getEntity();
        int cx = entity.blockPosition().getX() >> 4;
        int cz = entity.blockPosition().getZ() >> 4;

        ChunkCorruptionClient clientData = ChunkCorruptionClient.getInstance();
        byte type = clientData.getType(cx, cz);

        if (type == 2 || (type >= 0 && type != 2 && mc.level.random.nextFloat() < 0.3f)) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShaderColor(0.8f, 0.1f, 0.1f, 0.9f);
        }
    }

    @SubscribeEvent
    public void onRenderLivingPost(RenderLivingEvent.Post<?, ?> event) {
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();
    }
}