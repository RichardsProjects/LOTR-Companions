package net.richardsprojects.lotrcompanions;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.richardsprojects.lotrcompanions.client.render.HiredBreeGuardRenderer;
import net.richardsprojects.lotrcompanions.client.render.HiredGondorSoldierRenderer;
import net.richardsprojects.lotrcompanions.core.PacketHandler;
import net.richardsprojects.lotrcompanions.entity.LOTRCEntities;
import net.richardsprojects.lotrcompanions.eventhandlers.ModCommonEvents;
import net.richardsprojects.lotrcompanions.eventhandlers.ForgeEntityEvents;
import net.richardsprojects.lotrcompanions.eventhandlers.ModEntityEvents;
import net.richardsprojects.lotrcompanions.item.LOTRCItems;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(LOTRCompanions.MOD_ID)
public class LOTRCompanions {

    // configurable values
    public static final boolean LOW_HEALTH_FOOD = true;
    public static final boolean FRIENDLY_FIRE_PLAYER = true;
    public static final boolean FALL_DAMAGE = true;
    public static final int BASE_HEALTH = 30;

    private static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "lotrcompanions";

    public static final boolean FRIENDLY_FIRE_COMPANIONS = false;

    public static IEventBus eventBus;
    public LOTRCompanions() {
    	//Register Listeners that use the Forge Event Bus
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(ForgeEntityEvents.class);
        
        //Register Listeners that use the Mod Event Bus
        eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        LOTRCEntities.ENTITIES.register(eventBus);
        LOTRCItems.ITEMS.register(eventBus);
        eventBus.register(this);
        eventBus.register(ModEntityEvents.class);
        eventBus.register(ModCommonEvents.class);
        
        PacketHandler.register();
    }

    @SubscribeEvent
    public void setupClientRendering(final FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(LOTRCEntities.HIRED_GONDOR_SOLDIER.get(), HiredGondorSoldierRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(LOTRCEntities.HIRED_BREE_GUARD.get(), HiredBreeGuardRenderer::new);
    }

}
