package net.richardsprojects.lotrcompanions.networking;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;
import net.richardsprojects.lotrcompanions.entity.AbstractHiredLOTREntity;

import java.util.function.Supplier;

public class ClearTargetPacket {
    private final int entityId;

    public ClearTargetPacket(int entityId) {
        this.entityId = entityId;
    }

    public static ClearTargetPacket decode(PacketBuffer buf) {
        return new ClearTargetPacket(buf.readInt());
    }

    public static void encode(ClearTargetPacket msg, PacketBuffer buf) {
        buf.writeInt(msg.entityId);
    }

    public int getEntityId() {
        return this.entityId;
    }

    public static void handle(ClearTargetPacket msg, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (msg != null) {
                context.get().enqueueWork(() -> {
                    ServerPlayerEntity player = context.get().getSender();
                    if (player != null && player.level instanceof ServerWorld) {
                        Entity entity = player.level.getEntity(msg.getEntityId());
                        if (entity instanceof AbstractHiredLOTREntity) {
                            AbstractHiredLOTREntity companion = (AbstractHiredLOTREntity) entity;
                            companion.clearTarget();
                        }
                    }
                });
            }
        });
        context.get().setPacketHandled(true);
    }
}
