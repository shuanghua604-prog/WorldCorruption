package com.corruption;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

public class CorruptionSyncPacket implements IMessage {

    private int chunkX;
    private int chunkZ;
    private byte type;

    public CorruptionSyncPacket() {}

    public CorruptionSyncPacket(int cx, int cz, byte type) {
        this.chunkX = cx;
        this.chunkZ = cz;
        this.type = type;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        chunkX = buf.readInt();
        chunkZ = buf.readInt();
        type = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(chunkX);
        buf.writeInt(chunkZ);
        buf.writeByte(type);
    }

    public static class Handler implements IMessageHandler<CorruptionSyncPacket, IMessage> {
        @Override
        public IMessage onMessage(CorruptionSyncPacket message, MessageContext ctx) {
            if (ctx.side.isClient()) {
                ChunkCorruptionClient client = ChunkCorruptionClient.getInstance();
                client.setType(message.chunkX, message.chunkZ, message.type);
            }
            return null;
        }
    }
}