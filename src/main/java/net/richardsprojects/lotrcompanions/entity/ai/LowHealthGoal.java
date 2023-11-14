package net.richardsprojects.lotrcompanions.entity.ai;

import lotr.common.entity.npc.NPCEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.richardsprojects.lotrcompanions.LOTRCompanions;
import net.richardsprojects.lotrcompanions.entity.HirableUnit;
import net.richardsprojects.lotrcompanions.entity.HiredGondorSoldier;

public class LowHealthGoal extends Goal {
    protected final NPCEntity entity;
    protected final HirableUnit unit;

    int startTick = 0;
    StringTextComponent text = new StringTextComponent("I need food!");
    ItemStack food = ItemStack.EMPTY;

    public LowHealthGoal(NPCEntity entity, HirableUnit unit) {
        this.entity = entity;
        this.unit = unit;
    }

    public boolean canUse() {
        if (LOTRCompanions.LOW_HEALTH_FOOD) {
            if (this.entity.getHealth() < this.entity.getMaxHealth() / 2 && this.unit.isTame()) {
                food = unit.checkFood();
                return !food.isEmpty();
            }
        }
        return false;
    }

    public void start() {
        startTick = this.entity.tickCount;
        if (this.unit.getOwner() != null) {
            this.unit.getOwner().sendMessage(new TranslationTextComponent("chat.type.text", this.entity.getDisplayName(), text),
                    this.entity.getUUID());
        }
    }

    public void tick() {
        if ((this.entity.tickCount - startTick) % (15 * 20) == 0 && this.entity.tickCount > startTick) {
            if (this.unit.getOwner() != null) {
                this.unit.getOwner().sendMessage(new TranslationTextComponent("chat.type.text", this.entity.getDisplayName(), text),
                        this.entity.getUUID());
            }
        }

    }
}
