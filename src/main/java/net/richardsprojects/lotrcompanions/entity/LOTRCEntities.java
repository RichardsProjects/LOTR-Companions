package net.richardsprojects.lotrcompanions.entity;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.richardsprojects.lotrcompanions.LOTRCompanions;

public class LOTRCEntities {

    private LOTRCEntities() {}

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, LOTRCompanions.MOD_ID);

    public static final RegistryObject<EntityType<HiredGondorSoldier>> HIRED_GONDOR_SOLDIER =
            ENTITIES.register("hired_gondor_soldier", () -> EntityType.Builder.of(HiredGondorSoldier::new, EntityClassification.AMBIENT)
                    .sized(0.6F, 1.8F)
                    .build(new ResourceLocation(LOTRCompanions.MOD_ID, "hired_gondor_soldier").toString()));

    // TODO: check if this is the correct size
    public static final RegistryObject<EntityType<HiredBreeGuard>> HIRED_BREE_GUARD =
            ENTITIES.register("hired_bree_guard", () -> EntityType.Builder.of(HiredBreeGuard::new, EntityClassification.AMBIENT)
                    .sized(0.6F, 1.8F)
                    .build(new ResourceLocation(LOTRCompanions.MOD_ID, "hired_bree_soldier").toString()));

}
