package net.richardsprojects.lotrcompanions.npcs;

import lotr.common.entity.npc.ExtendedHirableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class HiredUnitHelper {

    public static void die(World world, DamageSource source, ExtendedHirableEntity unit) {
        if (!world.isClientSide && world.getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES) && unit.getOwner() instanceof ServerPlayerEntity) {
            // TODO: Update this show cause of death
            unit.getOwner().sendMessage(new TranslationTextComponent("notification.hired_companion_died", unit.getHiredUnitName()), unit.getOwnerUUID());
        }
    }

    public static void giveExperiencePoints(ExtendedHirableEntity unit, int points) {
        int newExperience = unit.getCurrentXp() + points;
        if (newExperience >= unit.getMaxXp()) {
            unit.setExpLvl(unit.getExpLvl() + 1);
            int difference = newExperience - unit.getMaxXp();
            unit.setCurrentXp(difference);
            unit.setMaxXp(unit.getMaxXp() + 2);
            unit.setHiredUnitHealth(unit.getHiredUnitHealth() + 2);
            unit.setBaseHealth(unit.getBaseHealth() + 2);

            if (unit.getOwner() != null) {
                unit.getOwner().sendMessage(
                        new StringTextComponent("Your hired companion ").
                                append(unit.getHiredUnitName()).
                                append(" has reached level " + unit.getExpLvl() + "!"),
                        unit.getOwnerUUID());
            }
        } else {
            unit.setCurrentXp(newExperience);
        }
    }

    public static boolean isEntityHiredUnit(Entity entity) {
        return
                entity instanceof HiredGondorSoldier
             || entity instanceof HiredBreeGuard;
    }

    public static ExtendedHirableEntity getExtendedHirableEntity(Entity entity) {
        if (!isEntityHiredUnit(entity)) return null;

        // attempt to cast to proper unit
        ExtendedHirableEntity result = null;

        if (entity instanceof HiredBreeGuard) {
            result = (ExtendedHirableEntity) entity;
        } else if (entity instanceof HiredGondorSoldier) {
            result = (ExtendedHirableEntity) entity;
        }

        return result;
    }

}
