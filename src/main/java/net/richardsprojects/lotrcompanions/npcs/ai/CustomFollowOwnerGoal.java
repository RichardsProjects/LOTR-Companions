package net.richardsprojects.lotrcompanions.npcs.ai;

import lotr.common.entity.npc.ExtendedHirableEntity;
import lotr.common.entity.npc.NPCEntity;

public class CustomFollowOwnerGoal extends FollowOwnerGoal {

    public ExtendedHirableEntity companion;

    public CustomFollowOwnerGoal(NPCEntity entity, ExtendedHirableEntity p_25294_, double p_25295_, float p_25296_, float p_25297_, boolean p_25298_) {
        super(entity, p_25294_, p_25295_, p_25296_, p_25297_, p_25298_);
        this.companion = p_25294_;
    }

    public boolean canUse() {
        if (!companion.isFollowing()) {
            return false;
        }
        return super.canUse();
    }
}
