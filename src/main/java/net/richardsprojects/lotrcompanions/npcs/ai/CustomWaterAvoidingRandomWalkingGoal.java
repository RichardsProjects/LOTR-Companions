package net.richardsprojects.lotrcompanions.npcs.ai;

import lotr.common.entity.npc.NPCEntity;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.richardsprojects.lotrcompanions.npcs.HirableUnit;

public class CustomWaterAvoidingRandomWalkingGoal extends WaterAvoidingRandomWalkingGoal {
    private NPCEntity entity;
    private HirableUnit unit;

    public CustomWaterAvoidingRandomWalkingGoal(NPCEntity entity, HirableUnit unit, double p_25988_) {
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
