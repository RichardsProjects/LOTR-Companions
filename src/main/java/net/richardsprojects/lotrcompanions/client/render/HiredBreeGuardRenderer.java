package net.richardsprojects.lotrcompanions.client.render;

import lotr.client.render.entity.BreeManRenderer;
import lotr.client.render.entity.GondorSoldierRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;

public class HiredBreeGuardRenderer extends BreeManRenderer {

    public HiredBreeGuardRenderer(EntityRendererManager mgr) {
        super(mgr);
    }

    /*public ResourceLocation getEntityTexture(NPCEntity entity) {
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXX");
        return new ResourceLocation("lotr", "textures/entity/gondor/gondor_soldier/0");
    }*/
}
