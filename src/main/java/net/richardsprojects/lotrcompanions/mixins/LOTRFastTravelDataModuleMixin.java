package net.richardsprojects.lotrcompanions.mixins;

import lotr.common.data.FastTravelDataModule;
import lotr.common.world.map.Waypoint;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.richardsprojects.lotrcompanions.event.LOTRFastTravelWaypointEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
            // TODO: This code and the event would work in the dev environment but not in the actual game and I can not
            //  figure out why. One alternative I tried was the hacky solution of performing the tp command on the player
            //  but it didn't seem to work very well either - any help in figuring this out would be really appreciated!
            LOTRFastTravelWaypointEvent event = new LOTRFastTravelWaypointEvent(player, world, orig, travelPos);
            MinecraftForge.EVENT_BUS.post(event);

            /*
            MinecraftServer source = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
            source.getCommands().performCommand(source.createCommandSourceStack(), "execute in lotr:middle_earth run tp " + player.getName().getString() + " "
                + travelPos.getX() + " " + travelPos.getY() + " " + travelPos.getZ());
             */
        }
    }
}
