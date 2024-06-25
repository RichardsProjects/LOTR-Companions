package net.richardsprojects.lotrcompanions.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import lotr.client.render.entity.GondorSoldierRenderer;
import lotr.common.entity.npc.GondorManEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;

public class HiredGondorSoldierRenderer extends GondorSoldierRenderer {
    public HiredGondorSoldierRenderer(EntityRendererManager mgr) {
        super(mgr);
    }

    @Override
    public void render(GondorManEntity entity, float yaw, float partialTicks, MatrixStack matStack, IRenderTypeBuffer buf, int packedLight) {
        super.render(entity, yaw, partialTicks, matStack, buf, packedLight);
    }
}
