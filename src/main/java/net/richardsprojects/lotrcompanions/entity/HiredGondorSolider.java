/**
 * This file has been modified from the Human Companions Mod
 * which can be found here:
 *
 * https://github.com/justinwon777/HumanCompanions/tree/main
 */

package net.richardsprojects.lotrcompanions.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class HiredGondorSolider extends AbstractLOTRCompanionEntity {

    public HiredGondorSolider(EntityType<? extends TameableEntity> entityType, World level) {
        super(entityType, level);
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));
    }

    public boolean isSword(ItemStack stack) {
        return stack.getItem() instanceof SwordItem;
    }

    public void checkSword() {
        ItemStack hand = this.getItemBySlot(EquipmentSlotType.MAINHAND);
        for (int i = 0; i < this.inventory.getContainerSize(); ++i) {
            ItemStack itemstack = this.inventory.getItem(i);
            if (isSword(itemstack)) {
                if (hand.isEmpty()) {
                    this.setItemSlot(EquipmentSlotType.MAINHAND, itemstack);
                } else if (isSword(hand)) {
                    if (getTotalAttackDamage(itemstack) > getTotalAttackDamage(hand)) {
                        this.setItemSlot(EquipmentSlotType.MAINHAND, itemstack);
                    }
                }
            }
        }
    }

    public void readAdditionalSaveData(CompoundNBT tag) {
        super.readAdditionalSaveData(tag);
        this.setItemSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
        checkSword();
    }

    public void tick() {
        if (!this.level.isClientSide()) {
            checkSword();
        }
        super.tick();
    }

    public ILivingEntityData finalizeSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn,
                                           SpawnReason reason, @Nullable ILivingEntityData spawnDataIn,
                                           @Nullable CompoundNBT dataTag) {
        this.inventory.setItem(4, Items.IRON_SWORD.getDefaultInstance());
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }
}
