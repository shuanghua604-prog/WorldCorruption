package com.corruption;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.Random;

public class CorruptionEventHandler {
    private Random chunkRandom = new Random();

    @SubscribeEvent
    public void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;

        ChunkAccess chunk = event.getChunk();
        int cx = chunk.getPos().x;
        int cz = chunk.getPos().z;

        ChunkCorruptionData data = ChunkCorruptionData.get(level);

        long seed = level.getSeed();
        chunkRandom.setSeed(seed + cx * 341873128712L + cz * 132897987541L);

        data.generateChunk(cx, cz, chunkRandom);

        byte type = data.getType(cx, cz);
        CorruptionMod.NETWORK.send(
            PacketDistributor.ALL.noArg(),
            new CorruptionSyncPacket(cx, cz, type)
        );
    }
}