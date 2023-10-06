package net.richardsprojects.lotrcompanions.entity.ai;

import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.richardsprojects.lotrcompanions.entity.HiredGondorSoldier;

public class CustomSitGoal extends Goal {
    private final HiredGondorSoldier mob;

    public CustomSitGoal(HiredGondorSoldier mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
    }

    public boolean canContinueToUse() {
        return this.mob.isStationary();
    }

    public boolean canUse() {
        if (!this.mob.isTame()) {
            return false;
        } else if (this.mob.isInWaterOrBubble()) {
            return false;
        } else if (!this.mob.isOnGround()) {
            return false;
        } else {
            LivingEntity livingentity = this.mob.getOwner();
            if (livingentity == null) {
                return true;
            } else {
                return (!(this.mob.distanceToSqr(livingentity) < 144.0D) || livingentity.getLastHurtByMob() == null) && this.mob.isStationary();
            }
        }
    }

    public void start() {
        this.mob.getNavigation().stop();
    }

    public void stop() {

    }
}
