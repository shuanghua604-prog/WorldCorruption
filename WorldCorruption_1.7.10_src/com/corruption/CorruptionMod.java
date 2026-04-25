package com.corruption;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = CorruptionMod.MODID, version = CorruptionMod.VERSION, acceptableRemoteVersions = "*")
public class CorruptionMod {
    public static final String MODID = "corruption";
    public static final String VERSION = "1.0.0";

    public static SimpleNetworkWrapper network;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        // 注册网络通道
        network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
        network.registerMessage(CorruptionSyncPacket.Handler.class, CorruptionSyncPacket.class, 0, Side.CLIENT);

        MinecraftForge.EVENT_BUS.register(new CorruptionEventHandler());
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new CorruptionRenderHandler());
        MinecraftForge.EVENT_BUS.register(new EntityRenderCorruption());
        MinecraftForge.EVENT_BUS.register(ChunkCorruptionClient.getInstance());
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    }
}