package net.richardsprojects.lotrcompanions.mixins;

import lotr.common.data.FastTravelDataModule;
import lotr.common.world.map.Waypoint;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EntityTeleportEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.richardsprojects.lotrcompanions.entity.HiredBreeGuard;
import net.richardsprojects.lotrcompanions.entity.HiredGondorSoldier;
import net.richardsprojects.lotrcompanions.event.LOTRFastTravelWaypointEvent;
import net.richardsprojects.lotrcompanions.eventhandlers.EntityEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
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
            // TODO: This code and the event would work in the dev environment but not in the actual game and I can not
            //  figure out why and in the meantime went with the hacky solution performing the tp command on the player eventually
            //  I would like to let it redo it properly but for now this gets followers to properly follow you to a
            //  waypoint
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
