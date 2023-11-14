package net.richardsprojects.lotrcompanions.entity.ai;

import lotr.common.entity.npc.NPCEntity;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.richardsprojects.lotrcompanions.entity.HirableUnit;
import net.richardsprojects.lotrcompanions.entity.HiredGondorSoldier;

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
