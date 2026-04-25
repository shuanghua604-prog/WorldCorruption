package com.corruption;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.world.ChunkEvent;

import java.util.HashMap;
import java.util.Map;

public class ChunkCorruptionClient {

    private static ChunkCorruptionClient instance;
    private Map<Long, Byte> clientMap = new HashMap<Long, Byte>();

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
        if (!clientMap.containsKey(key)) return -1;
        return clientMap.get(key);
    }

    public void clear() {
        clientMap.clear();
    }

    @SubscribeEvent
    public void onClientConnected(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        clear();
    }

    @SubscribeEvent
    public void onClientDisconnected(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        clear();
    }
}