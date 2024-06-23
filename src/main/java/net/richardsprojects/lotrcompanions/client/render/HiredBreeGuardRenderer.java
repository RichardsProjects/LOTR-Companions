package net.richardsprojects.lotrcompanions.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import lotr.client.render.entity.BreeManRenderer;
import lotr.common.entity.npc.BreeManEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;

public class HiredBreeGuardRenderer extends BreeManRenderer {

    public HiredBreeGuardRenderer(EntityRendererManager mgr) {
        super(mgr);
    }

    @Override
    public void render(BreeManEntity entity, float yaw, float partialTicks, MatrixStack matStack, IRenderTypeBuffer buf, int packedLight) {
        super.render(entity, yaw, partialTicks, matStack, buf, packedLight);

        //CompanionRenderUtils.renderHealthBar(this.entityRenderDispatcher, this.getFont(), entity, matStack, buf, packedLight);
    }
}
