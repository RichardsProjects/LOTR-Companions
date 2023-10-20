package net.richardsprojects.lotrcompanions.entity;

import lotr.common.entity.npc.*;
import net.minecraft.advancements.criterion.PlayerEntityInteractionTrigger;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.richardsprojects.lotrcompanions.LOTRCompanions;
import net.richardsprojects.lotrcompanions.container.CompanionContainer;
import net.richardsprojects.lotrcompanions.item.LOTRCItems;

import java.util.Objects;
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
    @SubscribeEvent
    public static void hireGondorSoldier(PlayerInteractEvent.EntityInteract event) {
        // only allow this event to run on the server
        if (!(event.getWorld() instanceof ServerWorld)) {
            return;
        }

        if (!(event.getTarget() instanceof GondorSoldierEntity)) {
            return;
        }

        // check that they have a coin in their hand
        if (!(event.getItemStack().getItem().equals(LOTRCItems.ONE_COIN.get()) ||
              event.getItemStack().getItem().equals(LOTRCItems.HUNDRED_COIN.get()) ||
              event.getItemStack().getItem().equals(LOTRCItems.HUNDRED_COIN.get()))) {
            return;
            }

        int coins = totalCoins(event.getPlayer().inventory);
        System.out.println("Total Coins: " + coins);
        if (coins < 60) {
            event.getPlayer().sendMessage(new StringTextComponent("I require 60 coins in payment to be hired."), event.getPlayer().getUUID());
            return;
        }

        GondorSoldierEntity gondorSoldier = (GondorSoldierEntity) event.getTarget();
        HiredGondorSoldier newEntity = (HiredGondorSoldier) LOTRCEntities.HIRED_GONDOR_SOLDIER.get().spawn(
                (ServerWorld) event.getWorld(), null,
                event.getPlayer(), new BlockPos(gondorSoldier.getX(), gondorSoldier.getY(), gondorSoldier.getZ()),
                SpawnReason.NATURAL, true, false
        );
        if (newEntity != null) {
            newEntity.tame(event.getPlayer());
            gondorSoldier.remove();
            removeCoins(event.getPlayer().inventory, 60);
            event.getPlayer().sendMessage(new StringTextComponent("The Gondor Soldier has been hired for 60 coins"), event.getPlayer().getUUID());
        }
    }

    private static int totalCoins(PlayerInventory inventory) {
        int totalValue = 0;

        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack itemStack = inventory.getItem(i);
            if (itemStack.getItem().equals(LOTRCItems.ONE_COIN.get())) {
                totalValue += itemStack.getCount();
            } else if (itemStack.getItem().equals(LOTRCItems.TEN_COIN.get())) {
                totalValue += (itemStack.getCount() * 10);
            } else if (itemStack.getItem().equals(LOTRCItems.HUNDRED_COIN.get())) {
                totalValue += (itemStack.getCount() * 100);
            }
        }

        return totalValue;
    }

    private static boolean removeCoins(PlayerInventory inventory, int amount) {
        int coinsRemoved = 0;

        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack itemStack = inventory.getItem(i);

            if (!(itemStack.getItem().equals(LOTRCItems.ONE_COIN.get()) ||
                  itemStack.getItem().equals(LOTRCItems.TEN_COIN.get()) ||
                  itemStack.getItem().equals(LOTRCItems.HUNDRED_COIN.get()))) {
                continue;
            }

            int value;
            int currency = 0;

            if (itemStack.getItem().equals(LOTRCItems.ONE_COIN.get())) {
                currency = 1;
            } else if (itemStack.getItem().equals(LOTRCItems.TEN_COIN.get())) {
                currency = 10;
            } else if (itemStack.getItem().equals(LOTRCItems.HUNDRED_COIN.get())) {
                currency = 100;
            }
            value = itemStack.getCount() * currency;

            if (coinsRemoved + value >= amount) {
                // check if change has to be mode
                int amountLeft = amount - coinsRemoved;
                int newDifference = value - amountLeft;

                if (newDifference == 0) {
                    inventory.setItem(i, ItemStack.EMPTY);
                    return true;
                }

                if (newDifference % currency == 0) {
                    // no change has to be made - simply reduce it
                    itemStack.setCount(newDifference);
                    return true;
                } else {
                    int currencyOfChange = 1;
                    Item item = LOTRCItems.ONE_COIN.get();
                    if (newDifference % 100 == 0) {
                        currencyOfChange = 100;
                        item = LOTRCItems.HUNDRED_COIN.get();
                    } else if (newDifference % 10 == 0) {
                        currencyOfChange = 10;
                        item = LOTRCItems.TEN_COIN.get();
                    }
                    int newAmount = newDifference / currencyOfChange;
                    ItemStack newItemStack = new ItemStack(item, newAmount);
                    inventory.setItem(i, newItemStack);
                    return true;
                }
            } else {
                // remove all coins
                coinsRemoved += value;
                inventory.setItem(i, ItemStack.EMPTY);
            }
        }

        return false;
    }

    // TODO: Implement hiring Bree-Land Guards eventually for 20 coins
}
