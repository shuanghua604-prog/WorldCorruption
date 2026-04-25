package com.corruption;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.ChunkEvent;

import java.util.Random;

public class CorruptionEventHandler {

    private Random chunkRandom = new Random();

    @SubscribeEvent
    public void onChunkLoad(ChunkEvent.Load event) {
        if (event.world.isRemote) return;

        Chunk chunk = event.getChunk();
        int cx = chunk.xPosition;
        int cz = chunk.zPosition;

        ChunkCorruptionData data = ChunkCorruptionData.get(event.world);

        // 使用世界种子 + 区块坐标作为随机源，保证一致性
        chunkRandom.setSeed(event.world.getSeed() + cx * 341873128712L + cz * 132897987541L);

        data.generateChunk(cx, cz, chunkRandom);

        // 同步到客户端
        byte type = data.getType(cx, cz);
        CorruptionMod.network.sendToAll(new CorruptionSyncPacket(cx, cz, type));
    }
}