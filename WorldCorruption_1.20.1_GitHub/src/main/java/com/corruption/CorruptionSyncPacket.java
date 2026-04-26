package com.corruption;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CorruptionSyncPacket {
    private int chunkX;
    private int chunkZ;
    private byte type;

    public CorruptionSyncPacket() {}

    public CorruptionSyncPacket(int cx, int cz, byte type) {
        this.chunkX = cx;
        this.chunkZ = cz;
        this.type = type;
    }

    public static void encode(CorruptionSyncPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.chunkX);
        buf.writeInt(msg.chunkZ);
        buf.writeByte(msg.type);
    }

    public static CorruptionSyncPacket decode(FriendlyByteBuf buf) {
        return new CorruptionSyncPacket(buf.readInt(), buf.readInt(), buf.readByte());
    }

    public static class Handler {
        public static void handle(CorruptionSyncPacket msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                if (ctx.get().getDirection().getReceptionSide().isClient()) {
                    ChunkCorruptionClient client = ChunkCorruptionClient.getInstance();
                    client.setType(msg.chunkX, msg.chunkZ, msg.type);
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }
}