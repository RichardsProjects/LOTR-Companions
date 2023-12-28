package net.richardsprojects.lotrcompanions.mixins;

import lotr.common.init.LOTREntities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraftforge.fml.RegistryObject;
import net.richardsprojects.lotrcompanions.npcs.HiredBreeGuard;
import net.richardsprojects.lotrcompanions.npcs.HiredGondorSoldier;
import net.richardsprojects.lotrcompanions.npcs.LOTRCNpcs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(LOTREntities.class)
public class LOTREntitiesMixin {

    @Shadow(remap=false)
    private static <T extends lotr.common.entity.npc.NPCEntity> RegistryObject<EntityType<T>> regNPC(String key, EntityType.IFactory<T> factory, Supplier<AttributeModifierMap.MutableAttribute> attribFactory, LOTREntities.EntitySizeHolder size, LOTREntities.SpawnEggInfo.SpawnEggColors eggColors) {
        return null;
    }

    @Inject(method = "register()V", at = @At("HEAD"), remap=false)
    private static void bootstrapExtendedItems(CallbackInfo ci) {
        LOTRCNpcs.HIRED_BREE_GUARD = regNPC("hired_bree_guard", HiredBreeGuard::new, HiredBreeGuard::regAttrs, LOTREntities.EntitySizeHolder.manSize(), LOTREntities.SpawnEggInfo.SpawnEggColors.egg(16752511, 8010275));
        LOTRCNpcs.HIRED_GONDOR_SOLDIER = regNPC("hired_gondor_soldier", HiredGondorSoldier::new, HiredGondorSoldier::regAttrs, LOTREntities.EntitySizeHolder.manSize(), LOTREntities.SpawnEggInfo.SpawnEggColors.egg(16752511, 8010275));
    }
}
