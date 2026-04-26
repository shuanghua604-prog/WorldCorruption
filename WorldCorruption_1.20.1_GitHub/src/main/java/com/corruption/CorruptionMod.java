package com.corruption;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

@Mod(CorruptionMod.MODID)
public class CorruptionMod {
    public static final String MODID = "corruption";
    public static final String VERSION = "1.0.0";
    private static final String PROTOCOL_VERSION = "1";

    public static SimpleChannel NETWORK;

    public CorruptionMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::setup);

        MinecraftForge.EVENT_BUS.register(new CorruptionEventHandler());

        NETWORK = NetworkRegistry.newSimpleChannel(
            new net.minecraft.resources.ResourceLocation(MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
        );

        NETWORK.registerMessage(0, CorruptionSyncPacket.class,
            CorruptionSyncPacket::encode,
            CorruptionSyncPacket::decode,
            CorruptionSyncPacket.Handler::handle
        );
    }

    private void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // 注册客户端事件
            net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent clientSetup = null;
        });
    }
}