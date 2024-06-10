package net.richardsprojects.lotrcompanions.npcs.ai;

import lotr.common.entity.npc.ExtendedHirableEntity;
import lotr.common.entity.npc.NPCEntity;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;

public class CustomWaterAvoidingRandomWalkingGoal extends WaterAvoidingRandomWalkingGoal {
    private NPCEntity entity;
    private ExtendedHirableEntity unit;

    public CustomWaterAvoidingRandomWalkingGoal(NPCEntity entity, ExtendedHirableEntity unit, double p_25988_) {
        super(entity, p_25988_);
        this.entity = entity;
        this.unit = unit;
    }

    public boolean canUse() {
        if (!unit.isFollowing()) {
            return false;
        }
        return super.canUse();
    }
}
