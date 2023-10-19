package net.richardsprojects.lotrcompanions.entity;

import lotr.common.entity.npc.BreeHobbitEntity;
import lotr.common.entity.npc.HobbitEntity;
import lotr.common.entity.npc.NPCEntity;
import lotr.common.entity.npc.WargEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.richardsprojects.lotrcompanions.LOTRCompanions;
import net.richardsprojects.lotrcompanions.container.CompanionContainer;
import net.richardsprojects.lotrcompanions.item.LOTRCItems;

import java.util.Random;

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

    @SubscribeEvent
    public static void lotrEntityDeathEvent(final LivingDeathEvent event) {
        if (!(event.getEntity() instanceof NPCEntity)) {
            return;
        }

        // hobbits and wargs don't drop coins
        if (event.getEntity() instanceof HobbitEntity || event.getEntity() instanceof BreeHobbitEntity || event.getEntity() instanceof WargEntity) {
            return;
        }

        // drop between 0 and 4 coins
        Random random = new Random();
        int count = random.nextInt(5);
        if (count > 0) {
            ItemEntity item = new ItemEntity(event.getEntity().level, event.getEntity().getX(),
                    event.getEntity().getY() + 1, event.getEntity().getZ(),
                    new ItemStack(LOTRCItems.ONE_COIN.get(), count));
            event.getEntity().level.addFreshEntity(item);
        }
    }

    // TODO: Implement hiring Gondor Soldier for 60 coins

    // TODO: Implement hiring Bree-Land Guards eventually for 20 coins

}
