package com.corruption;

import java.util.HashMap;
import java.util.Map;

public class ChunkCorruptionClient {
    private static ChunkCorruptionClient instance;
    private Map<Long, Byte> clientMap = new HashMap<>();

    public static ChunkCorruptionClient getInstance() {
        if (instance == null) instance = new ChunkCorruptionClient();
        return instance;
    }

    public void setType(int chunkX, int chunkZ, byte type) {
        long key = ((long)chunkX & 0xffffffffL) | (((long)chunkZ & 0xffffffffL) << 32);
        clientMap.put(key, type);
    }

    public byte getType(int chunkX, int chunkZ) {
        long key = ((long)chunkX & 0xffffffffL) | (((long)chunkZ & 0xffffffffL) << 32);
        return clientMap.getOrDefault(key, (byte) -1);
    }

    public void clear() { clientMap.clear(); }
}