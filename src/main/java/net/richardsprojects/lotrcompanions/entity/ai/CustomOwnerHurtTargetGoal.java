package net.richardsprojects.lotrcompanions.entity.ai;

import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.richardsprojects.lotrcompanions.LOTRCompanions;
import net.richardsprojects.lotrcompanions.entity.HiredGondorSoldier;

import java.util.EnumSet;

public class CustomOwnerHurtTargetGoal extends TargetGoal {
    private final HiredGondorSoldier follower;
    private LivingEntity ownerLastHurt;
    private int timestamp;

    public CustomOwnerHurtTargetGoal(HiredGondorSoldier entity) {
        super(entity, false);
        this.follower = entity;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    public boolean canUse() {
        if (this.follower.isTame()) {
            LivingEntity livingentity = this.follower.getOwner();
            if (livingentity == null) {
                return false;
            } else {
                this.ownerLastHurt = livingentity.getLastHurtMob();
                if (this.ownerLastHurt instanceof TameableEntity) {
                    if (((TameableEntity) this.ownerLastHurt).isTame()) {
                        LivingEntity owner1 = ((TameableEntity) this.ownerLastHurt).getOwner();
                        LivingEntity owner2 = this.follower.getOwner();
                        if (owner1 == owner2) {
                            if (!LOTRCompanions.FRIENDLY_FIRE_COMPANIONS) {
                                return false;
                            }
                        }
                    }
                } else if (this.ownerLastHurt instanceof CreeperEntity || this.ownerLastHurt instanceof ArmorStandEntity) {
                    return false;
                }
                int i = livingentity.getLastHurtMobTimestamp();
                return i != this.timestamp && this.canAttack(this.ownerLastHurt, EntityPredicate.DEFAULT) && this.follower.wantsToAttack(this.ownerLastHurt, livingentity);
            }
        } else {
            return false;
        }
    }

    public void start() {
        this.mob.setTarget(this.ownerLastHurt);
        LivingEntity livingentity = this.follower.getOwner();
        if (livingentity != null) {
            this.timestamp = livingentity.getLastHurtMobTimestamp();
        }

        super.start();
    }
}
