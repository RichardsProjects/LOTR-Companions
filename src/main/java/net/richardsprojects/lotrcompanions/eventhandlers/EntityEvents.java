package net.richardsprojects.lotrcompanions.eventhandlers;

import lotr.common.entity.npc.*;
import net.minecraft.entity.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.EntityTeleportEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.richardsprojects.lotrcompanions.LOTRCompanions;
import net.richardsprojects.lotrcompanions.container.CompanionContainer;
import net.richardsprojects.lotrcompanions.entity.*;
import net.richardsprojects.lotrcompanions.event.LOTRFastTravelWaypointEvent;
import net.richardsprojects.lotrcompanions.item.LOTRCItems;

import java.util.*;

@Mod.EventBusSubscriber(modid = LOTRCompanions.MOD_ID)
public class EntityEvents {
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
        HiredGondorSoldier newEntity = (HiredGondorSoldier) LOTRCEntities.HIRED_GONDOR_SOLDIER.get().spawn(
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
        HiredBreeGuard newEntity = (HiredBreeGuard) LOTRCEntities.HIRED_BREE_GUARD.get().spawn(
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
        teleportUnitsToPlayer(originalPos, targetPos, world, (PlayerEntity) event.getEntity());
    }

    private static void teleportUnitsToPlayer(BlockPos originalPos, BlockPos target, ServerWorld world, PlayerEntity player) {
        AxisAlignedBB initial = new AxisAlignedBB(originalPos.getX(), originalPos.getY(), originalPos.getZ(),
                originalPos.getX() + 1, originalPos.getY() + 1, originalPos.getZ() + 1);
        List<HiredGondorSoldier> gondorSoldiers = world.getEntitiesOfClass(HiredGondorSoldier.class, initial.inflate(256));
        List<HiredBreeGuard> breeGuards = world.getEntitiesOfClass(HiredBreeGuard.class, initial.inflate(256));


        Entity playerMount = player.getVehicle();
        player.stopRiding();
        if (playerMount instanceof MobEntity) {
            playerMount.moveTo(target.getX(), target.getY(), target.getZ());
            ServerChunkProvider scp = world.getChunkSource();
            scp.removeEntity(playerMount);
            scp.addEntity(playerMount);
            world.updateChunkPos(playerMount);
            world.addFreshEntity(playerMount);
        }

        for (HiredGondorSoldier soldier : gondorSoldiers) {
            if (!soldier.isStationary()) soldier.moveTo(target.getX(), target.getY(), target.getZ());
            ServerChunkProvider scp = world.getChunkSource();
            scp.removeEntity(soldier);
            scp.addEntity(soldier);
            world.updateChunkPos(soldier);
            world.addFreshEntity(soldier);
        }

        for (HiredBreeGuard breeGuard : breeGuards) {
            if (!breeGuard.isStationary()) breeGuard.moveTo(target.getX(), target.getY(), target.getZ());
            ServerChunkProvider scp = world.getChunkSource();
            scp.removeEntity(breeGuard);
            scp.addEntity(breeGuard);
            world.updateChunkPos(breeGuard);
            world.addFreshEntity(breeGuard);
        }
    }

    // TODO: This code and the event would work in the dev environment but not in the actual game and I can not
    //  figure out why and in the meantime went with the hacky solution performing the tp command on the player eventually
    //  I would like to let it redo it properly but for now this gets followers to properly follow you to a
    //  waypoint
    @SubscribeEvent
    public static void onPlayerLOTRWaypoint(LOTRFastTravelWaypointEvent event) {
        System.out.println("Inside LOTRFastTravelWaypointEvent handler");
        teleportUnitsToPlayer(event.getOriginalPos(), event.getTravelPos(), event.getWorld(), event.getPlayer());
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
