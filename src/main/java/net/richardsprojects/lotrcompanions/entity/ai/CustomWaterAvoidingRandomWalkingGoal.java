package net.richardsprojects.lotrcompanions.entity.ai;

import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.richardsprojects.lotrcompanions.entity.AbstractLOTRCompanionEntity;

public class CustomWaterAvoidingRandomWalkingGoal extends WaterAvoidingRandomWalkingGoal {
    AbstractLOTRCompanionEntity companion;

    public CustomWaterAvoidingRandomWalkingGoal(AbstractLOTRCompanionEntity p_25987_, double p_25988_) {
        super(p_25987_, p_25988_);
        this.companion = p_25987_;
    }

    public boolean canUse() {
        if (!companion.isFollowing()) {
            return false;
        }
        return super.canUse();
    }
}
