package net.richardsprojects.lotrcompanions.utils;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorld;
import net.richardsprojects.lotrcompanions.entity.HiredBreeGuard;
import net.richardsprojects.lotrcompanions.entity.HiredGondorSoldier;

public class TeleportHelper {
	public static void teleportUnitsToPlayer(BlockPos originalPos, BlockPos target, ServerWorld world, PlayerEntity player) {
        AxisAlignedBB initial = new AxisAlignedBB(originalPos.getX(), originalPos.getY(), originalPos.getZ(),
                originalPos.getX() + 1, originalPos.getY() + 1, originalPos.getZ() + 1);
        List<HiredGondorSoldier> gondorSoldiers = world.getEntitiesOfClass(HiredGondorSoldier.class, initial.inflate(256));
        List<HiredBreeGuard> breeGuards = world.getEntitiesOfClass(HiredBreeGuard.class, initial.inflate(256));

        for (HiredGondorSoldier soldier : gondorSoldiers) {
            if (!soldier.isStationary()) soldier.moveTo(target.getX(), target.getY(), target.getZ());
            ServerChunkProvider scp = world.getChunkSource();
            scp.removeEntity(soldier);
            scp.addEntity(soldier);
            world.updateChunkPos(soldier);
            world.addFreshEntity(soldier);
        }

        for (HiredBreeGuard breeGuard : breeGuards) {
            if (!breeGuard.isStationary()) breeGuard.moveTo(target.getX(), target.getY(), target.getZ());
            ServerChunkProvider scp = world.getChunkSource();
            scp.removeEntity(breeGuard);
            scp.addEntity(breeGuard);
            world.updateChunkPos(breeGuard);
            world.addFreshEntity(breeGuard);
        }
    }
}
