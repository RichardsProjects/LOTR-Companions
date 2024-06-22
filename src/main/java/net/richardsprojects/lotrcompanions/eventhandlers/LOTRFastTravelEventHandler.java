package net.richardsprojects.lotrcompanions.eventhandlers;

import lotr.common.event.LOTRFastTravelWaypointEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.richardsprojects.lotrcompanions.utils.TeleportHelper;

public class LOTRFastTravelEventHandler {

    @SubscribeEvent
    public static void onPlayerLOTRWaypoint(LOTRFastTravelWaypointEvent event) {
        TeleportHelper.teleportUnitsToPlayer(event.getOriginalPos(), event.getTravelPos(), event.getWorld(), event.getPlayer());
    }

}

