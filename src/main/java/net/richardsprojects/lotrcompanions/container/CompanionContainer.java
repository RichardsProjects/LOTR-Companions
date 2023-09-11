package net.richardsprojects.lotrcompanions.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class CompanionContainer extends Container {
    private final IInventory container;
    private final int containerRows = 1;

    public CompanionContainer(int p_39230_, PlayerInventory p_39231_, IInventory companionInv) {
        super(null, p_39230_);
        checkContainerSize(companionInv, companionInv.getContainerSize());
        this.container = companionInv;
        companionInv.startOpen(p_39231_.player);

        // add the 9 companion inventory slots
        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(companionInv, k, 8 + k * 18, 110));
        }

        // add the 3 rows of player inventory
        for (int l = 0; l < 3; ++l) {
            for (int j1 = 0; j1 < 9; ++j1) {
                this.addSlot(new Slot(p_39231_, j1 + l * 9 + 9, 8 + j1 * 18, 142 + l * 18));
            }
        }

        // add the player's hotbar
        for (int i1 = 0; i1 < 9; ++i1) {
            this.addSlot(new Slot(p_39231_, i1, 8 + i1 * 18, 200));
            System.out.println("Adding slot: " + (8 + i1 * 18));
        }

        // add the 6 companion equipment slots
        this.addSlot(new Slot(companionInv, 9,8,31)); // i = 44
        this.addSlot(new Slot(companionInv, 10,8,49));
        this.addSlot(new Slot(companionInv, 11,8,67));
        this.addSlot(new Slot(companionInv, 12,8,85));
        this.addSlot(new Slot(companionInv, 13,62,67));
        this.addSlot(new Slot(companionInv, 14,62,85));
    }

    public boolean stillValid(PlayerEntity p_39242_) {
        return this.container.stillValid(p_39242_);
    }

    public ItemStack quickMoveStack(PlayerEntity p_39253_, int p_39254_) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(p_39254_);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (p_39254_ < this.containerRows * 9) {
                if (!this.moveItemStackTo(itemstack1, this.containerRows * 9, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, this.containerRows * 9, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    public void removed(PlayerEntity p_39251_) {
        super.removed(p_39251_);
        this.container.stopOpen(p_39251_);
    }

    public int getRowCount() {
        return this.containerRows;
    }
}

