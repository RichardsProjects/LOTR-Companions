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
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

@Mixin(FastTravelDataModule.class)
public abstract class LOTRFastTravelDataModuleMixin {

    @Inject(at = @At(value = "HEAD"), method="Llotr/common/data/FastTravelDataModule;fastTravelTo(Lnet/minecraft/entity/player/ServerPlayerEntity;Llotr/common/world/map/Waypoint)V", remap = false)
    private void fastTravelTo(ServerPlayerEntity player, Waypoint waypoint, CallbackInfo info) {
        info.cancel();

        Logger.getLogger("LOTRFastTravelDataModuleMixin").info("Hello from LOTRFastTravelDataModuleMixin");
        System.out.println("Hello from LOTRFastTravelDataModuleMixin");

        ServerWorld world = player.getLevel();
        BlockPos travelPos = waypoint.getTravelPosition(world, player);

        if (travelPos == null) {
            LOTRLog.warn("Player %s fast travel to %s was cancelled because the waypoint returned a null travel position.",
                    UsernameHelper.getRawUsername(player), waypoint.getRawName());
        } else {
            double startXF = player.getX();
            double startYF = player.getY();
            double startZF = player.getZ();
            int startX = MathHelper.floor(startXF);
            int startZ = MathHelper.floor(startZF);

            List<MobEntity> entities = world.getEntitiesOfClass(MobEntity.class, player.getBoundingBox().inflate(256.0));

            List<MobEntity> entitiesToTransport = null;
            List<Entity> transportExclusions = null;

            for (MobEntity entity : entities) {
                if (entity instanceof TameableEntity) {
                    TameableEntity pet = (TameableEntity) entity;
                    if (pet.getOwner() == player && !pet.isOrderedToSit()) {
                        entitiesToTransport.add(pet);
                    }
                }

                if (entity.isLeashed() && entity.getLeashHolder() == player) {
                    entitiesToTransport.add(entity);
                }

                if (entity instanceof HiredGondorSoldier) {
                    HiredGondorSoldier soldier = (HiredGondorSoldier) entity;
                    if (!soldier.isStationary()) {
                        entitiesToTransport.add(soldier);
                    }
                }
            }

            for (MobEntity entity : entitiesToTransport) {
                for (Entity mount : entity.getPassengers()) {
                    if (entitiesToTransport.contains(mount)) {
                        transportExclusions.add(mount);
                    }
                }
            }

            System.out.println("Entities Before Transport Exclusions: " + ArrayUtils.toString(entitiesToTransport.toArray()));

            entitiesToTransport.removeAll(transportExclusions);

            System.out.println("Entities After Transport Exclusions: " + ArrayUtils.toString(entitiesToTransport.toArray()));

            Entity playerMount = player.getVehicle();
            player.stopRiding();
            player.teleportTo((double) travelPos.getX() + 0.5, (double) travelPos.getY(), (double) travelPos.getZ() + 0.5);
            player.fallDistance = 0.0F;
            if (playerMount instanceof MobEntity) {
                playerMount = this.fastTravelEntity(world, (MobEntity) playerMount, (double) travelPos.getX() + 0.5, (double) travelPos.getY(), (double) travelPos.getZ() + 0.5);
            }

            if (playerMount != null) {
                this.setUUIDToMount(((Entity) playerMount).getUUID());
            }

            for (MobEntity entity : entitiesToTransport) {
                Entity cMount = entity.getVehicle();
                entity.stopRiding();
                entity = this.fastTravelEntity(world, entity, (double) travelPos.getX() + 0.5, (double) travelPos.getY(), (double) travelPos.getZ() + 0.5);
                if (cMount instanceof MobEntity) {
                    Entity mount = this.fastTravelEntity(world, (MobEntity) cMount, (double) travelPos.getX() + 0.5, (double) travelPos.getY(), (double) travelPos.getZ() + 0.5);
                    entity.startRiding(mount);
                }
            }

            this.sendFTScreenPacket(player, waypoint, startX, startZ);
            this.setTimeSinceFTWithUpdate(0);
            this.incrementWPUseCount(waypoint);
            player.awardStat(LOTRStats.FAST_TRAVEL);
            double dx = player.getX() - startXF;
            double dy = player.getY() - startYF;
            double dz = player.getZ() - startZF;
            int distanceInM = Math.round(MathHelper.sqrt(dx * dx + dy * dy + dz * dz));
            if (distanceInM > 0) {
                player.awardStat(LOTRStats.FAST_TRAVEL_ONE_M, distanceInM);
            }
        }
    }

    @Shadow protected abstract void sendFTScreenPacket(ServerPlayerEntity player, Waypoint waypoint, int startX, int startZ);

    @Shadow public abstract void setTimeSinceFTWithUpdate(int i);

    @Shadow protected abstract <T extends MobEntity> T fastTravelEntity(ServerWorld world, T entity, double x, double y, double z);

    @Shadow public abstract void incrementWPUseCount(Waypoint waypoint);

    @Shadow protected abstract void setUUIDToMount(UUID uuid);
}
