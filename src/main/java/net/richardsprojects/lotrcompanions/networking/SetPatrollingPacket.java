package net.richardsprojects.lotrcompanions.networking;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;
import net.richardsprojects.lotrcompanions.entity.HiredGondorSoldier;

import java.util.function.Supplier;

public class SetPatrollingPacket {
    private final int entityId;

    public SetPatrollingPacket(int entityId) {
        this.entityId = entityId;
    }

    public static SetPatrollingPacket decode(PacketBuffer buf) {
        return new SetPatrollingPacket(buf.readInt());
    }

    public static void encode(SetPatrollingPacket msg, PacketBuffer buf) {
        buf.writeInt(msg.entityId);
    }

    public int getEntityId() {
        return this.entityId;
    }

    public static void handle(SetPatrollingPacket msg, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (msg != null) {
                context.get().enqueueWork(() -> {
                    ServerPlayerEntity player = context.get().getSender();
                    if (player != null && player.level instanceof ServerWorld) {
                        Entity entity = player.level.getEntity(msg.getEntityId());
                        if (entity instanceof HiredGondorSoldier) {
                            // TODO: reimplement at some point
                            /*
                            HiredGondorSoldier companion = (HiredGondorSoldier) entity;
                            if (companion.isFollowing()) {
                                companion.setPatrolling(true);
                                companion.setFollowing(false);
                                companion.setGuarding(false);
                                companion.setPatrolPos(companion.blockPosition());
                            } else if (companion.isPatrolling()) {
                                companion.setPatrolling(false);
                                companion.setFollowing(false);
                                companion.setGuarding(true);
                                companion.setPatrolPos(companion.blockPosition());
                            } else {
                                companion.setPatrolling(false);
                                companion.setFollowing(true);
                                companion.setGuarding(false);
                            }*/
                        }
                    }
                });
            }
        });
        context.get().setPacketHandled(true);
    }
}
