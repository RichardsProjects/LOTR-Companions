package net.richardsprojects.lotrcompanions.entity;

import net.minecraft.entity.Entity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.richardsprojects.lotrcompanions.LOTRCompanions;
import net.richardsprojects.lotrcompanions.container.CompanionContainer;

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

    @SubscribeEvent
    public static void playerCloseInventory(final PlayerContainerEvent.Close event) {
        System.out.println("Player Close Inventory event called");

        if (!(event.getContainer() instanceof CompanionContainer)) {
            return;
        }

        System.out.println("Player Close Inventory event called");

        CompanionContainer companionContainer = (CompanionContainer) event.getContainer();

        // make companion no longer stationary
        Entity entity = event.getPlayer().level.getEntity(companionContainer.getEntityId());
        if (entity instanceof HiredGondorSoldier) {
            //((HiredGondorSoldier) entity).setStationary(false);
            ((HiredGondorSoldier) entity).setInventoryOpen(false);
        }
    }

}
