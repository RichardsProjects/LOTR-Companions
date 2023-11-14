package net.richardsprojects.lotrcompanions.entity.ai;

import lotr.common.entity.npc.NPCEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.richardsprojects.lotrcompanions.entity.HirableUnit;
import net.richardsprojects.lotrcompanions.entity.HiredGondorSoldier;

public class EatGoal extends Goal {
    protected final NPCEntity entity;

    protected final HirableUnit companion;
    ItemStack food = ItemStack.EMPTY;
    ItemStack oldOffhand = ItemStack.EMPTY;
    boolean started = false;
    int timeLeft = -1;

    public EatGoal(NPCEntity entity, HirableUnit unit) {
        this.entity = entity;
        companion = unit;
    }

    public boolean canUse() {
        if (entity.getHealth() < entity.getMaxHealth() && !companion.isInventoryOpen()) {
            food = companion.checkFood();
            return !food.isEmpty();
        }
        return false;
    }

    public void start() {
        started = true;
        oldOffhand = companion.getCustomInventory().getItem(14).copy();
        companion.getCustomInventory().setItem(14, food);
        timeLeft = food.getUseDuration() + 1;
        entity.setItemInHand(Hand.OFF_HAND, food);
        entity.startUsingItem(Hand.OFF_HAND);
    }

    public void stop() {
        companion.getCustomInventory().setItem(14, oldOffhand);
        entity.setItemSlot(EquipmentSlotType.OFFHAND, oldOffhand);
        started = false;
        timeLeft = -1;
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    public void tick () {
        if (started && timeLeft > -1) {
            --timeLeft;
            if (timeLeft == 0) {
                stop();
            }
        } else {
            if (entity.getHealth() < entity.getMaxHealth()) {
                food = companion.checkFood();
                if (!food.isEmpty()) {
                    if (!started) start();
                }
            }
        }
    }
}