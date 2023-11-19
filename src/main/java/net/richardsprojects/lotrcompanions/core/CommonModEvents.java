package net.richardsprojects.lotrcompanions.core;

import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.richardsprojects.lotrcompanions.LOTRCompanions;
import net.richardsprojects.lotrcompanions.client.render.HiredBreeGuardRenderer;
import net.richardsprojects.lotrcompanions.entity.HiredBreeGuard;
import net.richardsprojects.lotrcompanions.entity.HiredGondorSoldier;
import net.richardsprojects.lotrcompanions.entity.LOTRCEntities;

@Mod.EventBusSubscriber(modid = LOTRCompanions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonModEvents {

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(LOTRCEntities.HIRED_GONDOR_SOLDIER.get(), HiredGondorSoldier.createAttributes().build());
        event.put(LOTRCEntities.HIRED_BREE_GUARD.get(), HiredBreeGuard.createAttributes().build());
    }

}
