package net.richardsprojects.lotrcompanions.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class Constants {

    public static final ItemStack BREE_GUARD_HEAD = new ItemStack(Items.IRON_HELMET).setHoverName(new StringTextComponent("Base Helmet").withStyle(TextFormatting.BOLD).withStyle(TextFormatting.GOLD));
    public static final ItemStack BREE_GUARD_CHEST = new ItemStack(Items.LEATHER_CHESTPLATE).setHoverName(new StringTextComponent("Base Chestplate").withStyle(TextFormatting.BOLD).withStyle(TextFormatting.GOLD));
    public static final ItemStack BREE_GUARD_LEGS = new ItemStack(Items.CHAINMAIL_LEGGINGS).setHoverName(new StringTextComponent("Base Chestplate").withStyle(TextFormatting.BOLD).withStyle(TextFormatting.GOLD));
    public static final ItemStack BREE_GUARD_BOOTS = new ItemStack(Items.CHAINMAIL_BOOTS).setHoverName(new StringTextComponent("Base Chestplate").withStyle(TextFormatting.BOLD).withStyle(TextFormatting.GOLD));

}
