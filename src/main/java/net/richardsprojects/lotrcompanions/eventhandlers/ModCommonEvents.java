package net.richardsprojects.lotrcompanions.eventhandlers;

import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.richardsprojects.lotrcompanions.entity.HiredBreeGuard;
import net.richardsprojects.lotrcompanions.entity.HiredGondorSoldier;
import net.richardsprojects.lotrcompanions.entity.LOTRCEntities;

/**
 * For {@link net.minecraftforge.eventbus.api.Event} that are fired on the Mod bus IModBusEvent
 * */
public class ModCommonEvents {

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(LOTRCEntities.HIRED_GONDOR_SOLDIER.get(), HiredGondorSoldier.createAttributes().build());
        event.put(LOTRCEntities.HIRED_BREE_GUARD.get(), HiredBreeGuard.createAttributes().build());
    }

}
