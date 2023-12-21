package net.richardsprojects.lotrcompanions.networking;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;
import net.richardsprojects.lotrcompanions.entity.HirableUnit;
import net.richardsprojects.lotrcompanions.entity.HiredUnitHelper;

import java.util.function.Supplier;

public class SetStationaryPacket {
    private final int entityId;

    public SetStationaryPacket(int entityId) {
        this.entityId = entityId;
    }

    public static SetStationaryPacket decode(PacketBuffer buf) {
        return new SetStationaryPacket(buf.readInt());
    }

    public static void encode(SetStationaryPacket msg, PacketBuffer buf) {
        buf.writeInt(msg.entityId);
    }

    public int getEntityId() {
        return this.entityId;
    }

    public static void handle(SetStationaryPacket msg, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (msg != null) {
                context.get().enqueueWork(() -> {
                    ServerPlayerEntity player = context.get().getSender();
                    if (player != null && player.level instanceof ServerWorld) {
                        Entity entity = player.level.getEntity(msg.getEntityId());

                        if (HiredUnitHelper.isEntityHiredUnit(entity)) {
                            HirableUnit unit = HiredUnitHelper.getHirableUnit(entity);
                            unit.setStationary(!unit.isStationary());
                        }
                    }
                });
            }
        });
        context.get().setPacketHandled(true);
    }
}