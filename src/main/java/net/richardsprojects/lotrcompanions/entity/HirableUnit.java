package net.richardsprojects.lotrcompanions.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;

public interface HirableUnit {

    @Nullable
    LivingEntity getOwner();

    boolean isFollowing();

    boolean isInventoryOpen();

    ItemStack checkFood();

    Inventory getCustomInventory();

    boolean isTame();

    boolean wantsToAttack(LivingEntity p_142018_1_, LivingEntity p_142018_2_);

    boolean isStationary();

    void setMobKills(int kills);

    void giveExperiencePoints(int points);

    int getMobKills();

    ITextComponent getName();

    boolean isPatrolling();
    float getMaxHealth();

    float getHealth();

    int getExpLvl();

    int getMaxXp();

    int getCurrentXp();

    boolean isAlert();

    int getId();
}
