package net.richardsprojects.lotrcompanions.entity.ai;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.richardsprojects.lotrcompanions.entity.AbstractLOTRCompanionEntity;

public class EatGoal extends Goal {
    protected final AbstractLOTRCompanionEntity companion;
    ItemStack food = ItemStack.EMPTY;

    public EatGoal(AbstractLOTRCompanionEntity entity) {
        companion = entity;
    }

    public boolean canUse() {
        if (companion.getHealth() < companion.getMaxHealth()) {
            food = companion.checkFood();
            return !food.isEmpty();
        }
        return false;
    }

    public void start() {
        companion.setItemSlot(EquipmentSlotType.OFFHAND, food);
        companion.startUsingItem(Hand.OFF_HAND);
        companion.setEating(true);
    }

    public void stop() {
        companion.setItemSlot(EquipmentSlotType.OFFHAND, ItemStack.EMPTY);
        companion.setEating(false);
    }

    public void tick () {
        if (companion.getHealth() < companion.getMaxHealth()) {
            food = companion.checkFood();
            if (!food.isEmpty()) {
                start();
            }
        }
    }
}