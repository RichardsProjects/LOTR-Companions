package net.richardsprojects.lotrcompanions.eventhandlers;

import lotr.common.entity.npc.*;
import lotr.common.init.ExtendedItems;
import net.minecraft.entity.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.EntityTeleportEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.richardsprojects.lotrcompanions.container.CompanionContainer;
import net.richardsprojects.lotrcompanions.npcs.*;
import net.richardsprojects.lotrcompanions.item.LOTRCItems;
import net.richardsprojects.lotrcompanions.utils.TeleportHelper;

import java.util.*;

/**
 * For {@link net.minecraftforge.eventbus.api.Event} that are fired on the MinecraftForge.EVENT_BUS
 * */
public class ForgeEntityEvents {
    @SubscribeEvent
    public static void giveExperience(final LivingDeathEvent event) {
        Entity companion = event.getSource().getEntity();
        if (companion instanceof HiredGondorSoldier || companion instanceof HiredBreeGuard) {
            // TODO: put in some calculations for each mob type
            ((HirableUnit) companion).giveExperiencePoints(1);
            ((HirableUnit) companion).setMobKills(((HirableUnit) companion).getMobKills() + 1);
        }
    }

    @SubscribeEvent
    public static void playerCloseInventory(final PlayerContainerEvent.Close event) {
        if (!(event.getContainer() instanceof CompanionContainer)) {
            return;
        }

        CompanionContainer companionContainer = (CompanionContainer) event.getContainer();

        // make companion no longer stationary
        Entity entity = event.getPlayer().level.getEntity(companionContainer.getEntityId());
        if (entity instanceof HiredGondorSoldier) {
            ((HiredGondorSoldier) entity).setInventoryOpen(false);
        } else if (entity instanceof HiredBreeGuard) {
            ((HiredBreeGuard) entity).setInventoryOpen(false);
        }
    }

    @SubscribeEvent
    public static void lotrEntityDeathEvent(final LivingDropsEvent event) {
        if (!(event.getEntity() instanceof NPCEntity)) {
           return;
        }

        if (!(event.getEntity() instanceof ManEntity
            || event.getEntity() instanceof ElfEntity
            || event.getEntity() instanceof DwarfEntity
            || event.getEntity() instanceof OrcEntity)) {
            return;
        }

        // verify coins are in the collection - if not add them
        Set<Item> items = new HashSet<>();
        event.getDrops().forEach(e -> items.add(e.getItem().getItem()));

        if (!items.contains(ExtendedItems.SILVER_COIN_ONE.get())) {
            ItemEntity entity = new ItemEntity(event.getEntity().level, event.getEntity().getX(),
                    event.getEntity().getY(), event.getEntity().getZ(),
                    new ItemStack(ExtendedItems.SILVER_COIN_ONE.get(), new Random().nextInt(2) + 1));
            event.getDrops().add(entity);
        }
    }

    @SubscribeEvent
    public static void hireGondorSoldier(PlayerInteractEvent.EntityInteract event) {
        // TODO: Clean up code between hireGOndorSoldier and hireBreelandGuard so that they are one method with less
        //  repeated code

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
              event.getItemStack().getItem().equals(LOTRCItems.TEN_COIN.get()))) {
            return;
            }

        int coins = totalCoins(event.getPlayer().inventory);
        System.out.println("Total Coins: " + coins);
        if (coins < 60) {
            event.getPlayer().sendMessage(new StringTextComponent("I require 60 coins in payment to be hired."), event.getPlayer().getUUID());
            return;
        }

        GondorSoldierEntity gondorSoldier = (GondorSoldierEntity) event.getTarget();
        HiredGondorSoldier newEntity = (HiredGondorSoldier) LOTRCNpcs.HIRED_GONDOR_SOLDIER.get().spawn(
                (ServerWorld) event.getWorld(), null,
                event.getPlayer(), new BlockPos(gondorSoldier.getX(), gondorSoldier.getY(), gondorSoldier.getZ()),
                SpawnReason.NATURAL, true, false
        );
        if (newEntity != null) {
            newEntity.tame(event.getPlayer());
            gondorSoldier.remove();
            removeCoins(event.getPlayer(), event.getPlayer().inventory, 60);
            event.getPlayer().sendMessage(new StringTextComponent("The Gondor Soldier has been hired for 60 coins"), event.getPlayer().getUUID());
        }
    }

    @SubscribeEvent
    public static void hireBreeGuard(PlayerInteractEvent.EntityInteract event) {
        // only allow this event to run on the server
        if (!(event.getWorld() instanceof ServerWorld)) {
            return;
        }

        if (!(event.getTarget() instanceof BreeGuardEntity)) {
            return;
        }

        // check that they have a coin in their hand
        if (!(event.getItemStack().getItem().equals(LOTRCItems.ONE_COIN.get()) ||
                event.getItemStack().getItem().equals(LOTRCItems.HUNDRED_COIN.get()) ||
                event.getItemStack().getItem().equals(LOTRCItems.TEN_COIN.get()))) {
            return;
        }

        // TODO: Make prices be based upon faction reputation

        int coins = totalCoins(event.getPlayer().inventory);
        if (coins < 40) {
            event.getPlayer().sendMessage(new StringTextComponent("I require 40 coins in payment to be hired."), event.getPlayer().getUUID());
            return;
        }

        BreeGuardEntity breeGuard = (BreeGuardEntity) event.getTarget();
        HiredBreeGuard newEntity = (HiredBreeGuard) LOTRCNpcs.HIRED_BREE_GUARD.get().spawn(
                (ServerWorld) event.getWorld(), null,
                event.getPlayer(), new BlockPos(breeGuard.getX(), breeGuard.getY(), breeGuard.getZ()),
                SpawnReason.NATURAL, true, false
        );

        // TODO: Update gear to match correctly

        if (newEntity != null) {
            newEntity.tame(event.getPlayer());
            breeGuard.remove();
            removeCoins(event.getPlayer(), event.getPlayer().inventory, 40);
            event.getPlayer().sendMessage(new StringTextComponent("The Bree-land Guard has been hired for 40 coins"), event.getPlayer().getUUID());
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

    private static boolean removeCoins(PlayerEntity player, PlayerInventory inventory, int amount) {
        // TODO: Fix bug in here with stacks over 64

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
                    int newCount = newDifference / currency;
                    if (newCount > 64) {
                        itemStack.setCount(64);
                        List<ItemStack> stacks = new ArrayList<>();
                        int remainder = newCount - 64;
                        while (remainder > 64) {
                            stacks.add(new ItemStack(itemStack.getItem(), 64));
                            remainder -= 64;
                        }
                        if (remainder > 0) {
                            stacks.add(new ItemStack(itemStack.getItem(), remainder));
                        }
                        for (int index = 0; index < stacks.size(); index++) {
                            if (!inventory.add(stacks.get(index))) {
                                ItemEntity itemEntity = new ItemEntity(player.level, player.getX(), player.getY(), player.getZ(), stacks.get(index));
                                player.level.addFreshEntity(itemEntity);
                            }
                        }
                    } else {
                        itemStack.setCount(newCount);
                    }
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
                    if (newAmount > 64) {
                        ItemStack newItemStack = new ItemStack(item, 64);
                        inventory.setItem(i, newItemStack);
                        List<ItemStack> stacks = new ArrayList<>();
                        int remainder = newAmount - 64;
                        while (remainder > 64) {
                            stacks.add(new ItemStack(item, 64));
                            remainder -= 64;
                        }
                        if (remainder > 0) {
                            stacks.add(new ItemStack(item, remainder));
                        }
                        for (int index = 0; index < stacks.size(); index++) {
                            if (!inventory.add(stacks.get(index))) {
                                ItemEntity itemEntity = new ItemEntity(player.level, player.getX(), player.getY(), player.getZ(), stacks.get(index));
                                player.level.addFreshEntity(itemEntity);
                            }
                        }
                    } else {
                        ItemStack newItemStack = new ItemStack(item, newAmount);
                        inventory.setItem(i, newItemStack);
                    }
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

    @SubscribeEvent
    public static void onPlayerTeleport(EntityTeleportEvent event) {
        if (!(event.getEntity() instanceof PlayerEntity)) {
            return;
        }

        if (!(event.getEntity().level instanceof ServerWorld)) {
            return;
        }

        ServerWorld world = (ServerWorld) event.getEntity().level;
        BlockPos originalPos = new BlockPos(event.getPrevX(), event.getPrevY(), event.getPrevZ());
        BlockPos targetPos = new BlockPos(event.getTargetX(), event.getTargetY(), event.getTargetZ());
        TeleportHelper.teleportUnitsToPlayer(originalPos, targetPos, world, (PlayerEntity) event.getEntity());
    }

    @SubscribeEvent
    public static void preventFriendlyFireFromPlayerToCompanion(LivingDamageEvent event) {
        if (!HiredUnitHelper.isEntityHiredUnit(event.getEntity())) {
            return;
        }

        UUID owner = HiredUnitHelper.getHirableUnit(event.getEntity()).getOwnerUUID();

        if (event.getSource() != null && event.getSource().getEntity() != null
                && event.getSource().getEntity() instanceof PlayerEntity) {
            // cancel a damage event if damage comes from owner
            PlayerEntity player = (PlayerEntity) event.getSource().getEntity();
            if (owner.equals(player.getUUID())) {
                event.setCanceled(true);
            }
        }
    }
}
