package net.richardsprojects.lotrcompanions.entity;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.richardsprojects.lotrcompanions.LOTRCompanions;

public class LOTRCEntity {

    private LOTRCEntity() {}

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, LOTRCompanions.MOD_ID);

    public static final RegistryObject<EntityType<HiredGondorSolider>> HIRED_GONDOR_SOLDIER =
            ENTITIES.register("hired_gondor_soldier", () -> EntityType.Builder.of(HiredGondorSolider::new, EntityClassification.AMBIENT)
                    .sized(0.6F, 1.8F)
                    .build(new ResourceLocation(LOTRCompanions.MOD_ID, "hired_gondor_soldier").toString()));

    /*public static final RegistryObject<EntityType<Archer>> Archer =
            ENTITIES.register("archer", () -> EntityType.Builder.of(Archer::new, EntityClassification.AMBIENT)
                    .sized(0.6F, 1.8F)
                    .build(new ResourceLocation(HumanCompanions.MOD_ID, "archer").toString()));
     */
}
