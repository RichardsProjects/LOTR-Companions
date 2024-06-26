package net.richardsprojects.lotrcompanions.npcs.ai;

import lotr.common.entity.npc.ExtendedHirableEntity;
import lotr.common.entity.npc.NPCEntity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.entity.passive.TameableEntity;
import net.richardsprojects.lotrcompanions.LOTRCompanions;

import java.util.EnumSet;

public class CustomOwnerHurtByTargetGoal extends TargetGoal {

    private final ExtendedHirableEntity follower;
    private final NPCEntity entity;
    private LivingEntity ownerLastHurtBy;
    private int timestamp;

    public CustomOwnerHurtByTargetGoal(NPCEntity entity, ExtendedHirableEntity p_26107_) {
        super(entity, false);
        this.follower = p_26107_;
        this.entity = entity;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    public boolean canUse() {
        if (this.follower.isTame()) {
            LivingEntity livingentity = this.follower.getOwner();
            if (livingentity == null) {
                return false;
            } else {
                this.ownerLastHurtBy = livingentity.getLastHurtByMob();
                if (this.ownerLastHurtBy instanceof TameableEntity) {
                    if (((TameableEntity) this.ownerLastHurtBy).isTame()) {
                        LivingEntity owner1 = ((TameableEntity) this.ownerLastHurtBy).getOwner();
                        LivingEntity owner2 = this.follower.getOwner();
                        if (owner1 == owner2) {
                            if (!LOTRCompanions.FRIENDLY_FIRE_COMPANIONS) {
                                return false;
                            }
                        }
                    }
                }
                int i = livingentity.getLastHurtByMobTimestamp();
                return i != this.timestamp && this.canAttack(this.ownerLastHurtBy, EntityPredicate.DEFAULT) && this.follower.wantsToAttack(this.ownerLastHurtBy, livingentity);
            }
        } else {
            return false;
        }
    }

    public void start() {
        this.mob.setTarget(this.ownerLastHurtBy);
        LivingEntity livingentity = this.follower.getOwner();
        if (livingentity != null) {
            this.timestamp = livingentity.getLastHurtByMobTimestamp();
        }

        super.start();
    }
}
