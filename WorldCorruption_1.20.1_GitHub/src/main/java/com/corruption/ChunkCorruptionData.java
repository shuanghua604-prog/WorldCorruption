package com.corruption;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ChunkCorruptionData extends SavedData {

    private static final String DATA_NAME = CorruptionMod.MODID + "_chunkdata";
    private Map<Long, Byte> corruptionMap = new HashMap<>();
    public static final float CORRUPTION_CHANCE = 0.08f;

    public ChunkCorruptionData() {}

    public static ChunkCorruptionData load(CompoundTag tag) {
        ChunkCorruptionData data = new ChunkCorruptionData();
        int count = tag.getInt("count");
        for (int i = 0; i < count; i++) {
            data.corruptionMap.put(tag.getLong("k" + i), tag.getByte("v" + i));
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putInt("count", corruptionMap.size());
        int i = 0;
        for (Map.Entry<Long, Byte> entry : corruptionMap.entrySet()) {
            tag.putLong("k" + i, entry.getKey());
            tag.putByte("v" + i, entry.getValue());
            i++;
        }
        return tag;
    }

    public static ChunkCorruptionData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
            ChunkCorruptionData::load,
            ChunkCorruptionData::new,
            DATA_NAME
        );
    }

    public void generateChunk(int chunkX, int chunkZ, Random rand) {
        long key = chunkXZToLong(chunkX, chunkZ);
        if (corruptionMap.containsKey(key)) return;

        if (rand.nextFloat() < CORRUPTION_CHANCE) {
            corruptionMap.put(key, (byte) rand.nextInt(5));
        } else {
            corruptionMap.put(key, (byte) -1);
        }
        setDirty();
    }

    public byte getType(int chunkX, int chunkZ) {
        long key = chunkXZToLong(chunkX, chunkZ);
        return corruptionMap.getOrDefault(key, (byte) -1);
    }

    private long chunkXZToLong(int x, int z) {
        return ((long)x & 0xffffffffL) | (((long)z & 0xffffffffL) << 32);
    }
}