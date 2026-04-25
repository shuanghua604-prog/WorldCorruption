package com.corruption;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.chunk.Chunk;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ChunkCorruptionData extends WorldSavedData {

    private static final String DATA_NAME = CorruptionMod.MODID + "_ChunkData";
    private Map<Long, Byte> corruptionMap = new HashMap<Long, Byte>();

    // 腐化生成概率
    public static final float CORRUPTION_CHANCE = 0.08f; // 8%

    public ChunkCorruptionData(String name) {
        super(name);
    }

    public static ChunkCorruptionData get(World world) {
        ChunkCorruptionData data = (ChunkCorruptionData) world.loadItemData(ChunkCorruptionData.class, DATA_NAME);
        if (data == null) {
            data = new ChunkCorruptionData(DATA_NAME);
            world.setItemData(DATA_NAME, data);
        }
        return data;
    }

    // 生成新区块时调用
    public void generateChunk(int chunkX, int chunkZ, Random rand) {
        long key = chunkXZToLong(chunkX, chunkZ);
        if (corruptionMap.containsKey(key)) return; // 已存在

        if (rand.nextFloat() < CORRUPTION_CHANCE) {
            // 随机类型 0-4 (蓝/黑/红/橙/灰)
            byte type = (byte) rand.nextInt(5);
            corruptionMap.put(key, type);
            markDirty();
        } else {
            corruptionMap.put(key, (byte) -1); // -1 = 正常
            markDirty();
        }
    }

    // 获取区块腐化类型，-1=正常
    public byte getType(int chunkX, int chunkZ) {
        long key = chunkXZToLong(chunkX, chunkZ);
        if (!corruptionMap.containsKey(key)) return -1;
        return corruptionMap.get(key);
    }

    // 检查是否为腐化区块
    public boolean isCorrupted(int chunkX, int chunkZ) {
        return getType(chunkX, chunkZ) >= 0;
    }

    private long chunkXZToLong(int x, int z) {
        return ((long)x & 0xffffffffL) | (((long)z & 0xffffffffL) << 32);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        corruptionMap.clear();
        int count = nbt.getInteger("count");
        for (int i = 0; i < count; i++) {
            long key = nbt.getLong("k" + i);
            byte val = nbt.getByte("v" + i);
            corruptionMap.put(key, val);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setInteger("count", corruptionMap.size());
        int i = 0;
        for (Map.Entry<Long, Byte> entry : corruptionMap.entrySet()) {
            nbt.setLong("k" + i, entry.getKey());
            nbt.setByte("v" + i, entry.getValue());
            i++;
        }
    }
}