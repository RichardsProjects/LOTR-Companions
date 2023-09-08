package net.richardsprojects.lotrcompanions.client.render;

import lotr.client.render.RandomTextureVariants;
import lotr.client.render.entity.AbstractManRenderer;
import lotr.common.entity.npc.NPCEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.richardsprojects.lotrcompanions.entity.AbstractHiredLOTREntity;

public class HiredGondorSoldierRenderer extends AbstractManRenderer<AbstractHiredLOTREntity> {
    public HiredGondorSoldierRenderer(EntityRendererManager mgr) {
        super(mgr);
    }

    public ResourceLocation getEntityTexture(NPCEntity entity) {
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXX");
        return new ResourceLocation("lotr", "textures/entity/gondor/gondor_soldier/0");
    }
}
