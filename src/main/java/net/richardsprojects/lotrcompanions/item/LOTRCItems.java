package net.richardsprojects.lotrcompanions.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.richardsprojects.lotrcompanions.LOTRCompanions;
import net.richardsprojects.lotrcompanions.entity.LOTRCEntity;

public class LOTRCItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
            LOTRCompanions.MOD_ID);

    public static final RegistryObject<ForgeSpawnEggItem> HIRED_GONDOR_SOLDIER_EGG = ITEMS.register("hired_gondor_solider_spawn_egg",
            () -> new ForgeSpawnEggItem(LOTRCEntity.HIRED_GONDOR_SOLDIER,0xE8AF5A, 0xFF0000,
                    new Item.Properties().stacksTo(64).tab(ItemGroup.TAB_MISC)));


}
