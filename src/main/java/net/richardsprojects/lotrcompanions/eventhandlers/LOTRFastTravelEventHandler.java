package net.richardsprojects.lotrcompanions.eventhandlers;

import lotr.common.event.LOTRFastTravelWaypointEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.richardsprojects.lotrcompanions.utils.TeleportHelper;

public class LOTRFastTravelEventHandler {

    @SubscribeEvent
    public static void onPlayerLOTRWaypoint(LOTRFastTravelWaypointEvent event) {
        System.out.println("Event received 1");
        TeleportHelper.teleportUnitsToPlayer(event.getOriginalPos(), event.getTravelPos(), event.getWorld(), event.getPlayer());
        System.out.println("Event received 2");
    }

}

