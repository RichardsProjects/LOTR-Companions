package net.richardsprojects.lotrcompanions.core;

import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.richardsprojects.lotrcompanions.LOTRCompanions;
import net.richardsprojects.lotrcompanions.entity.HiredGondorSolider;
import net.richardsprojects.lotrcompanions.entity.LOTRCEntity;

@Mod.EventBusSubscriber(modid = LOTRCompanions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonModEvents {

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(LOTRCEntity.HIRED_GONDOR_SOLDIER.get(), HiredGondorSolider.createAttributes().build());
    }

}
