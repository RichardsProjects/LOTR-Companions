package net.richardsprojects.lotrcompanions.entity;

import net.minecraft.entity.Entity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.richardsprojects.lotrcompanions.LOTRCompanions;

@Mod.EventBusSubscriber(modid = LOTRCompanions.MOD_ID)
public class EntityEvents {
    @SubscribeEvent
    public static void giveExperience(final LivingDeathEvent event) {
        Entity companion = event.getSource().getEntity();
        if (companion instanceof HiredGondorSoldier) {
            // TODO: put in some calculations for each mob type
            ((HiredGondorSoldier) companion).giveExperiencePoints(1);
            ((HiredGondorSoldier) companion).setMobKills(((HiredGondorSoldier) companion).getMobKills() + 1);
        }
    }

}
