/**
 * This file has been modified from the Human Companions Mod
 * which can be found here:
 *
 * https://github.com/justinwon777/HumanCompanions/tree/main
 */

package net.richardsprojects.lotrcompanions.entity;

import lotr.common.init.LOTRItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IItemProvider;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class HiredGondorSolider extends AbstractHiredLOTREntity {

    public HiredGondorSolider(EntityType entityType, World level) {
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
        setItemSlot(EquipmentSlotType.FEET, new ItemStack((IItemProvider) LOTRItems.GONDOR_BOOTS.get()));
        setItemSlot(EquipmentSlotType.LEGS, new ItemStack((IItemProvider)LOTRItems.GONDOR_LEGGINGS.get()));
        setItemSlot(EquipmentSlotType.CHEST, new ItemStack((IItemProvider)LOTRItems.GONDOR_CHESTPLATE.get()));
        setItemSlot(EquipmentSlotType.HEAD, new ItemStack((IItemProvider)LOTRItems.GONDOR_HELMET.get()));
        setItemSlot(EquipmentSlotType.OFFHAND, new ItemStack((IItemProvider)LOTRItems.GONDOR_SHIELD.get()));
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }
}
