package net.richardsprojects.lotrcompanions.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

import javax.annotation.Nullable;

public interface HirableUnit {

    @Nullable
    public LivingEntity getOwner();

    public boolean isFollowing();

    public boolean isInventoryOpen();

    public ItemStack checkFood();

    public Inventory getCustomInventory();

    public boolean isTame();

    public boolean wantsToAttack(LivingEntity p_142018_1_, LivingEntity p_142018_2_);

    public boolean isStationary();

}
