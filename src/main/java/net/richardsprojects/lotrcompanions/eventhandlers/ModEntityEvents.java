package net.richardsprojects.lotrcompanions.eventhandlers;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.richardsprojects.lotrcompanions.event.LOTRFastTravelWaypointEvent;
import net.richardsprojects.lotrcompanions.utils.TeleportHelper;

/**
 * For {@link net.minecraftforge.eventbus.api.Event} that are fired on the Mod bus IModBusEvent
 * */
public class ModEntityEvents {
    @SubscribeEvent
    public static void onPlayerLOTRWaypoint(LOTRFastTravelWaypointEvent event) {
        System.out.println("Inside LOTRFastTravelWaypointEvent handler");
        TeleportHelper.teleportUnitsToPlayer(event.getOriginalPos(), event.getTravelPos(), event.getWorld(), event.getPlayer());
    }
}
