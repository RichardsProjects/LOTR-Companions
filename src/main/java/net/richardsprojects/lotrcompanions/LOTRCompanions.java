package net.richardsprojects.lotrcompanions;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(LOTRCompanions.MOD_ID)
public class LOTRCompanions {

    private static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "lotrcompanions";

    public static final int BASE_HEALTH = 30;

    public LOTRCompanions() {
        MinecraftForge.EVENT_BUS.register(this);

        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

    }

    private void setupClientRendering(final FMLClientSetupEvent event) {
        //RenderingRegistry.registerEntityRenderingHandler(EntityInit.Archer.get(), CompanionRenderer::new);
    }

}
