package net.richardsprojects.lotrcompanions.mixins;

import lotr.common.data.FastTravelDataModule;
import lotr.common.world.map.Waypoint;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.richardsprojects.lotrcompanions.event.LOTRFastTravelWaypointEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(FastTravelDataModule.class)
public abstract class LOTRFastTravelDataModuleMixin {

    @Inject(at = @At(value = "HEAD"), method="fastTravelTo", remap = false)
    private void fastTravelTo(ServerPlayerEntity player, Waypoint waypoint, CallbackInfo info) {
        System.out.println("FastTravelTo mixin was run");

        ServerWorld world = player.getLevel();
        BlockPos orig = new BlockPos(player.getX(), player.getY(), player.getZ());
        BlockPos travelPos = waypoint.getTravelPosition(world, player);
        System.out.println("Travel Pos: " + travelPos);

        System.out.println("Creating a LOTRFastTravelWaypointEvent event");

        if (travelPos != null) {
            LOTRFastTravelWaypointEvent event = new LOTRFastTravelWaypointEvent(player, world, orig, travelPos);
            MinecraftForge.EVENT_BUS.post(event);
            System.out.println("Posted LOTRFastTravelWaypointEvent event to the event bus");
        }
    }
}
