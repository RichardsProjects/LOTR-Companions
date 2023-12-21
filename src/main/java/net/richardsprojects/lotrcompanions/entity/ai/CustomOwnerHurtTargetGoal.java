package net.richardsprojects.lotrcompanions.entity.ai;

import lotr.common.entity.npc.NPCEntity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.richardsprojects.lotrcompanions.LOTRCompanions;
import net.richardsprojects.lotrcompanions.entity.HirableUnit;

import java.util.EnumSet;

public class CustomOwnerHurtTargetGoal extends TargetGoal {
    private final NPCEntity entity;
    private final HirableUnit unit;
    private LivingEntity ownerLastHurt;
    private int timestamp;

    public CustomOwnerHurtTargetGoal(NPCEntity entity, HirableUnit unit) {
        super(entity, false);

        this.entity = entity;
        this.unit = unit;

        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    public boolean canUse() {
        if (this.unit.isTame()) {
            LivingEntity livingentity = this.unit.getOwner();
            if (livingentity == null) {
                return false;
            } else {
                this.ownerLastHurt = livingentity.getLastHurtMob();
                if (this.ownerLastHurt instanceof TameableEntity) {
                    if (((TameableEntity) this.ownerLastHurt).isTame()) {
                        LivingEntity owner1 = ((TameableEntity) this.ownerLastHurt).getOwner();
                        LivingEntity owner2 = this.unit.getOwner();
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
                return i != this.timestamp && this.canAttack(this.ownerLastHurt, EntityPredicate.DEFAULT) && this.unit.wantsToAttack(this.ownerLastHurt, livingentity);
            }
        } else {
            return false;
        }
    }

    public void start() {
        this.mob.setTarget(this.ownerLastHurt);
        LivingEntity livingentity = this.unit.getOwner();
        if (livingentity != null) {
            this.timestamp = livingentity.getLastHurtMobTimestamp();
        }

        super.start();
    }
}
