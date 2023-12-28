package net.richardsprojects.lotrcompanions.npcs;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.richardsprojects.lotrcompanions.LOTRCompanions;

public class LOTRCNpcs {

    //private LOTRCNpcs() {}

   // public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, LOTRCompanions.MOD_ID);

    public static RegistryObject<EntityType<HiredGondorSoldier>> HIRED_GONDOR_SOLDIER;
    public static RegistryObject<EntityType<HiredBreeGuard>> HIRED_BREE_GUARD;

}
