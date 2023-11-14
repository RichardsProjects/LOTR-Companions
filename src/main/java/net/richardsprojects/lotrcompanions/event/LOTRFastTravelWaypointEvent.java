package net.richardsprojects.lotrcompanions.event;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.eventbus.api.Event;

public class LOTRFastTravelWaypointEvent extends Event {

    private ServerPlayerEntity player;
    private ServerWorld world;
    private BlockPos travelPos;

    public LOTRFastTravelWaypointEvent(ServerPlayerEntity player, ServerWorld world, BlockPos travelPos) {
        this.player = player;
        this.world = world;
        this.travelPos = travelPos;
    }

    public ServerPlayerEntity getPlayer() {
        return player;
    }

    public ServerWorld getWorld() {
        return world;
    }

    public BlockPos getTravelPos() {
        return travelPos;
    }
}
