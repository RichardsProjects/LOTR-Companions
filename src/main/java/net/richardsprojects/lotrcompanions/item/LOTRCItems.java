package net.richardsprojects.lotrcompanions.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.richardsprojects.lotrcompanions.LOTRCompanions;
import net.richardsprojects.lotrcompanions.entity.LOTRCEntities;

public class LOTRCItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
            LOTRCompanions.MOD_ID);

    public static final RegistryObject<ForgeSpawnEggItem> HIRED_GONDOR_SOLDIER_EGG = ITEMS.register("hired_gondor_soldier_spawn_egg",
            HiredGondorSoldierEggItem::new);
    public static final RegistryObject<Item> ONE_COIN = ITEMS.register("one_coin",
            OneCoinItem::new);
    public static final RegistryObject<Item> TEN_COIN = ITEMS.register("ten_coin",
            TenCoinItem::new);
    public static final RegistryObject<Item> HUNDRED_COIN = ITEMS.register("hundred_coin",
            HundredCoinItem::new);

}
