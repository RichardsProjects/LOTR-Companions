package net.richardsprojects.lotrcompanions.mixins;

import lotr.common.LOTRLog;
import lotr.common.data.FastTravelDataModule;
import lotr.common.stat.LOTRStats;
import lotr.common.util.UsernameHelper;
import lotr.common.world.map.Waypoint;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.richardsprojects.lotrcompanions.entity.HiredGondorSoldier;
import net.richardsprojects.lotrcompanions.event.LOTRFastTravelWaypointEvent;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@Mixin(FastTravelDataModule.class)
public abstract class LOTRFastTravelDataModuleMixin {

    @Inject(at = @At(value = "HEAD"), method="fastTravelTo", remap = false)
    private void fastTravelTo(ServerPlayerEntity player, Waypoint waypoint, CallbackInfo info) {
        ServerWorld world = player.getLevel();
        BlockPos travelPos = waypoint.getTravelPosition(world, player);

        if (travelPos != null) {
            LOTRFastTravelWaypointEvent event = new LOTRFastTravelWaypointEvent(player, world, travelPos);
            MinecraftForge.EVENT_BUS.post(event);
        }
    }

    @Shadow protected abstract void sendFTScreenPacket(ServerPlayerEntity player, Waypoint waypoint, int startX, int startZ);

    @Shadow public abstract void setTimeSinceFTWithUpdate(int i);

    @Shadow protected abstract <T extends MobEntity> T fastTravelEntity(ServerWorld world, T entity, double x, double y, double z);

    @Shadow public abstract void incrementWPUseCount(Waypoint waypoint);

    @Shadow protected abstract void setUUIDToMount(UUID uuid);
}
